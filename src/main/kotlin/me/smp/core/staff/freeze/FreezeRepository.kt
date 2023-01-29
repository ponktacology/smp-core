package me.smp.core.staff.freeze

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashSet

class FreezeRepository {

    private val lock = ReentrantLock()
    private val cache = HashSet<UUID>()

    fun isFrozen(player: Player): Boolean {
        lock.lock()
        try {
            return cache.contains(player.uniqueId)
        } finally {
            lock.unlock()
        }
    }

    fun freeze(player: Player) = cache.add(player.uniqueId)

    fun unFreeze(player: Player) = flushCache(player.uniqueId)

    fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }
}
