package me.smp.core.staff.freeze

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FreezeRepository {

    private val cache = ConcurrentHashMap.newKeySet<UUID>()

    fun isFrozen(player: Player) = cache.contains(player.uniqueId)

    fun freeze(player: Player) = cache.add(player.uniqueId)

    fun unFreeze(player: Player) = flushCache(player.uniqueId)

    fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }
}
