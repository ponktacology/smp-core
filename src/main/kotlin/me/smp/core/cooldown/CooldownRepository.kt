package me.smp.core.cooldown

import me.smp.core.Cooldown
import me.smp.core.Duration
import java.util.*

class CooldownRepository {

    private val cache = HashMap<UUID, MutableMap<CooldownType, Cooldown>>()

    fun isOnCooldown(uuid: UUID, type: CooldownType): Boolean {
        val cooldowns = cache[uuid] ?: return false
        val cooldown = cooldowns[type] ?: return false
        return !cooldown.hasExpired()
    }

    fun reset(uuid: UUID, type: CooldownType) {
        cache.computeIfAbsent(uuid) { HashMap() }
            .computeIfAbsent(type) { Cooldown(Duration(type.duration)) }.reset()
    }

    fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() = cache.clear()
}