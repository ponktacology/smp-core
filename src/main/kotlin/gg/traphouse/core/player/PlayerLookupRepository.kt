package gg.traphouse.core.player

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import gg.traphouse.core.Config
import gg.traphouse.core.Console
import gg.traphouse.core.SyncCatcher
import gg.traphouse.shared.MojangFetcher
import gg.traphouse.shared.network.NetworkService
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit


private const val NAME_PREFIX = "{name-cache}"
private const val ADDRESS_PREFIX = "{address_cache}"


class PlayerLookupRepository : KoinComponent {

    private val networkService: NetworkService by inject()

    private val uuidCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .concurrencyLevel(4)
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build<UUID, PlayerContainer>()

    private val nameCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .concurrencyLevel(4)
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build<String, PlayerContainer>()

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

        uuidCache.put(uuid, PlayerContainer(uuid, name))
        nameCache.put(name.uppercase(), PlayerContainer(uuid, name))
    }

    fun getUUIDByName(name: String): UUID? {
        SyncCatcher.verify()
        Bukkit.getPlayer(name)?.let {
            return it.uniqueId
        }

        nameCache.getIfPresent(name.uppercase())?.let {
            return it.uuid
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

        uuidCache.getIfPresent(uuid)?.let {
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
