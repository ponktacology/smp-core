package me.smp.core.name

import com.google.gson.JsonParser
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.multi
import me.smp.core.Config
import me.smp.core.Console
import me.smp.core.PlayerMetadata
import me.smp.core.SyncCatcher
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.*

private val UUID_CONVERT_REGEX = Regex("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})")
private const val PREFIX = "{name-cache}"

class NameRepository : KoinComponent {

    private val redisClient: RedisClient by inject()
    private val redisCommands = redisClient.connect().sync()

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    fun loadCache(uuid: UUID, name: String) {
        println("Caching $uuid and $name")
        SyncCatcher.verify()
        redisCommands.multi {
            setex("$PREFIX${name.uppercase()}", Config.NAME_CACHE_EXPIRY_SECONDS, uuid.toString())
            setex("$PREFIX$uuid", Config.NAME_CACHE_EXPIRY_SECONDS, name)
            println("Cahed $uuid and $name")
        }

    }

    fun getByName(name: String): UUID? {
        SyncCatcher.verify()
        Bukkit.getPlayer(name)?.let {
            println("Got from online player")
            return it.uniqueId
        }

        redisCommands.get("$PREFIX${name.uppercase()}")?.let {
            println("Got from redis cache")
            return UUID.fromString(it)
        }

        return fetchFromMineTools(name)?.let {
            loadCache(it.uuid, it.name)
            return it.uuid
        }
    }

    fun getByUUID(uuid: UUID): String? {
        SyncCatcher.verify()

        if (uuid == Console.UUID) return Console.DISPLAY_NAME

        Bukkit.getPlayer(uuid)?.let {
            return it.name
        }

        redisCommands.get("$PREFIX$uuid")?.let {
            println("Got from redis cache")
            return it
        }

        return fetchFromMineTools(uuid.toString().replace("-", ""))?.let {
            loadCache(it.uuid, it.name)
            return it.name
        }
    }

    private fun fetchFromMineTools(param: String): PlayerMetadata? {
        SyncCatcher.verify()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.minetools.eu/uuid/$param"))
            .timeout(Duration.ofSeconds(5))
            .build()
        val response: HttpResponse<String> =
            HttpClient.newHttpClient().send(request, BodyHandlers.ofString())
        if (response.statusCode() != 200) return null
        val jsonObject = JsonParser.parseString(response.body()).asJsonObject
        if (jsonObject.get("status").asString != "OK") return null
        return PlayerMetadata(
            UUID.fromString(
                jsonObject.get("id").asString.replace(
                    UUID_CONVERT_REGEX,
                    "$1-$2-$3-$4-$5"
                )
            ),
            jsonObject.get("name").asString
        )
    }

}