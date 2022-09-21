package me.smp.core.network

import com.google.gson.Gson
import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import me.smp.core.SyncCatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentHashMap

class NetworkRepository : KoinComponent {

    private val redisClient: RedisClient by inject()
    private val redisPubSub = redisClient.connectPubSub()
    private val redisPublish = redisClient.connect()
    private val handlerCache = ConcurrentHashMap<String, MutableSet<NetworkHandlerMeta>>()
    private val typeCache = ConcurrentHashMap<String, Class<*>>()
    private val gson = Gson()

    fun startListening() {
        redisPubSub.addListener(object : RedisPubSubAdapter<String, String>() {
            override fun subscribed(channel: String?, count: Long) {
                println("Subscribed? $channel")
            }

            override fun unsubscribed(channel: String?, count: Long) {
                println("Unsubscribed? $channel")
            }

            override fun message(channel: String, message: String) {
                SyncCatcher.verify()
                println("Received packet $channel $message")
                handlerCache[channel]?.let {
                    println("FOUND HANDLER CACHE")
                    val type = typeCache[channel] ?: return@let
                    println("FOUND TYPE CACHE")
                    val deserialized = gson.fromJson(message, type)
                    it.forEach { it(deserialized) }
                }
            }
        })
    }

    fun registerListener(networkListener: NetworkListener) {
        networkListener.javaClass.declaredMethods.forEach {
            println("Method ${it.name}")
            if (!it.isAnnotationPresent(NetworkHandler::class.java)) return@forEach
            if (it.parameterCount != 1) return@forEach
            val parameter = it.parameters[0]
            typeCache.putIfAbsent(parameter.type.simpleName, parameter.type)
            handlerCache.computeIfAbsent(parameter.type.simpleName) { mutableSetOf() }
                .add(NetworkHandlerMeta(networkListener, it))
            redisPubSub.sync().subscribe(parameter.type.simpleName)
            println("Subscribing ${parameter.type.simpleName}")
        }
    }

    fun publish(obj: Any) {
        SyncCatcher.verify()
        println("Pushed to ${obj.javaClass.simpleName}")
        redisPublish.sync().publish(obj.javaClass.simpleName, gson.toJson(obj))
    }
}