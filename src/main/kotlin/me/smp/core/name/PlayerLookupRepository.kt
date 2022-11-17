package me.smp.core.name

import com.google.gson.JsonParser
import me.smp.core.Config
import me.smp.core.Console
import me.smp.core.PlayerContainer
import me.smp.core.SyncCatcher
import me.smp.shared.SimpleHttp
import me.smp.shared.network.NetworkService
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

private val UUID_CONVERT_REGEX = Regex("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})")
private const val NAME_PREFIX = "{name-cache}"
private const val ADDRESS_PREFIX = "{address_cache}"
private const val MINE_TOOLS_URL = "https://api.minetools.eu/uuid/"

class PlayerLookupRepository : KoinComponent {

    private val networkService: NetworkService by inject()

    fun loadCache(uuid: UUID, name: String, address: String? = null) {
        SyncCatcher.verify()
        println("Caching $uuid and $name${if (address != null) " and $address" else ""}")
        networkService.setExpiring("$NAME_PREFIX${name.uppercase()}", uuid.toString(), Config.NAME_CACHE_EXPIRY_SECONDS)
        networkService.setExpiring("$NAME_PREFIX$uuid", name, Config.NAME_CACHE_EXPIRY_SECONDS)

        address?.let {
            networkService.setExpiring("${ADDRESS_PREFIX}$uuid", it, Config.ADDRESS_CACHE_EXPIRY_SECONDS)
        }

        println("Cached $uuid and $name")
    }

    fun getUUIDByName(name: String): UUID? {
        SyncCatcher.verify()
        Bukkit.getPlayer(name)?.let {
            println("Got from online player")
            return it.uniqueId
        }

        networkService.get("$NAME_PREFIX${name.uppercase()}")?.let {
            println("Got from redis cache")
            return UUID.fromString(it)
        }

        return fetchFromMineTools(name)?.let {
            loadCache(it.uuid, it.name)
            return it.uuid
        }
    }

    fun getNameByUUID(uuid: UUID): String? {
        SyncCatcher.verify()

        if (uuid == Console.UUID) return Console.DISPLAY_NAME

        Bukkit.getPlayer(uuid)?.let {
            return it.name
        }

        networkService.get("$NAME_PREFIX$uuid")?.let {
            println("Got from redis cache")
            return it
        }

        return fetchFromMineTools(uuid.toString().replace("-", ""))?.let {
            loadCache(it.uuid, it.name)
            return it.name
        }
    }

    fun getAddressByName(name: String): String? {
        val uuid = getUUIDByName(name) ?: return null

        return getAddressByUUID(uuid)
    }

    fun getAddressByUUID(uuid: UUID): String? {
        SyncCatcher.verify()

        if (uuid == Console.UUID) error("can't fetch address of a console")

        Bukkit.getPlayer(uuid)?.let {
            return it.address.hostName
        }

        return networkService.get("$ADDRESS_PREFIX$uuid")
    }

    private fun fetchFromMineTools(param: String): PlayerContainer? {
        SyncCatcher.verify()
        val response = SimpleHttp.get("$MINE_TOOLS_URL$param")
        if (response.statusCode() != 200) return null
        val jsonObject = JsonParser.parseString(response.body()).asJsonObject
        if (jsonObject.get("status").asString != "OK") return null
        return PlayerContainer(
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
