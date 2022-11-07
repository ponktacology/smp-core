package me.smp.core.cooldown

import me.smp.core.Cooldown
import me.smp.core.Duration
import me.smp.core.TaskDispatcher
import me.smp.core.UUIDCache
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.support.postgresql.insertOrUpdate
import java.util.*
import kotlin.collections.HashMap

class CooldownRepository : UUIDCache, KoinComponent {

    private val database: Database by inject()
    private val Database.cooldowns get() = this.sequenceOf(PlayerCooldowns)
    private val cache = HashMap<UUID, MutableMap<String, Cooldown>>()
    private val cooldowns = HashMap<String, CooldownType>()

    fun registerPersistentCooldown(cooldownType: CooldownType) = cooldowns.put(cooldownType.id, cooldownType)

    fun isOnCooldown(uuid: UUID, type: CooldownType): Boolean {
        val cooldowns = cache[uuid] ?: return false
        val cooldown = cooldowns[type.id] ?: return false
        return !cooldown.hasExpired()
    }

    fun reset(uuid: UUID, type: CooldownType) {
        cache.computeIfAbsent(uuid) { HashMap() }
            .computeIfAbsent(type.id) { Cooldown(Duration(type.duration)) }.reset()

        TaskDispatcher.dispatchAsync {
            database.insertOrUpdate(PlayerCooldowns) {
                set(it.id, type.id)
                set(it.player, uuid)
                set(it.resetAt, System.currentTimeMillis())
                onConflict { doNothing() }
            }
        }
    }

    override fun loadCache(uuid: UUID) {
        database.cooldowns.filter { it.player eq uuid }.forEach { cooldown ->
            val cooldownType = cooldowns[cooldown.id] ?: return@forEach
            cache.computeIfAbsent(uuid) { HashMap() }
                .computeIfAbsent(cooldownType.id) {
                    Cooldown(Duration(cooldownType.duration), cooldown.resetAt)
                }.reset()
        }

    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    override fun verifyCache(uuid: UUID) = true

    fun flushCache() {
        cache.clear()
        cooldowns.clear()
    }
}