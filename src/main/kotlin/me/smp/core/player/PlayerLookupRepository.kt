package me.smp.core.player

import me.smp.core.Config
import me.smp.core.Console
import me.smp.core.SyncCatcher
import me.smp.shared.MojangFetcher
import me.smp.shared.network.NetworkService
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


private const val NAME_PREFIX = "{name-cache}"
private const val ADDRESS_PREFIX = "{address_cache}"


class PlayerLookupRepository : KoinComponent {

    private val networkService: NetworkService by inject()

    fun loadCache(uuid: UUID, name: String, address: String? = null) {
        SyncCatcher.verify()
        networkService.setExpiring(
            "$NAME_PREFIX${name.uppercase()}",
            uuid.toString(),
            Config.NAME_CACHE_EXPIRY_SECONDS
        )
        networkService.setExpiring("$NAME_PREFIX$uuid", name, Config.NAME_CACHE_EXPIRY_SECONDS)
        address?.let {
            networkService.setExpiring("${ADDRESS_PREFIX}$uuid", it, Config.ADDRESS_CACHE_EXPIRY_SECONDS)
        }
    }

    fun getUUIDByName(name: String): UUID? {
        SyncCatcher.verify()
        Bukkit.getPlayer(name)?.let {
            return it.uniqueId
        }

        networkService.get("$NAME_PREFIX${name.uppercase()}")?.let {
            return UUID.fromString(it)
        }

        return MojangFetcher.fetchPlayer(name)?.let {
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
            return it
        }

        return MojangFetcher.fetchPlayer(uuid.toString().replace("-", ""))?.let {
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


}
