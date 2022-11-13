package me.smp.core.network

import com.google.gson.Gson
import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import me.smp.core.SyncCatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger

class NetworkRepository : KoinComponent {

    private val logger: Logger by inject()
    private val redisClient: RedisClient by inject()
    private val redisPubSub = redisClient.connectPubSub()
    private val redisPublish = redisClient.connect()
    private val handlerCache = ConcurrentHashMap<String, MutableSet<NetworkHandlerMeta>>()
    private val typeCache = ConcurrentHashMap<String, Class<*>>()
    private val gson = Gson()

    fun startListening() {
        redisPubSub.addListener(object : RedisPubSubAdapter<String, String>() {
            override fun subscribed(channel: String?, count: Long) {
                logger.log(Level.INFO, "[NETWORK] Subscribed to $channel")
            }

            override fun message(channel: String, message: String) {
                SyncCatcher.verify()
                handlerCache[channel]?.let {
                    val type = typeCache[channel] ?: return@let
                    val deserialized = gson.fromJson(message, type)
                    it.forEach { it(deserialized) }
                }
            }
        })
    }

    fun registerListener(networkListener: NetworkListener) {
        networkListener.javaClass.declaredMethods.forEach {
            if (!it.isAnnotationPresent(NetworkHandler::class.java)) return@forEach
            if (it.parameterCount != 1) return@forEach
            val parameter = it.parameters[0]
            typeCache.putIfAbsent(parameter.type.simpleName, parameter.type)
            handlerCache.computeIfAbsent(parameter.type.simpleName) { mutableSetOf() }
                .add(NetworkHandlerMeta(networkListener, it))
            redisPubSub.sync().subscribe(parameter.type.simpleName)
        }
    }

    fun publish(obj: Any) {
        SyncCatcher.verify()
        redisPublish.sync().publish(obj.javaClass.simpleName, gson.toJson(obj))
    }
}
