package me.smp.core.cooldown

import me.smp.core.TaskDispatcher
import me.smp.core.UUIDCache
import me.smp.core.player.PlayerNotFoundInCacheException
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CooldownRepository : UUIDCache, KoinComponent {

    private val database: Database by inject()
    private val Database.cooldowns get() = this.sequenceOf(CooldownsTable)
    private val cache = ConcurrentHashMap<UUID, PlayerCooldowns>()
    private val cooldownById = HashMap<String, CooldownType>()

    fun registerPersistentCooldown(cooldownType: CooldownType) = cooldownById.put(cooldownType.id, cooldownType)

    fun isOnCooldown(player: Player, type: CooldownType): Boolean {
        val cooldowns = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return cooldowns.isOnCooldown(type)
    }

    fun reset(player: Player, type: CooldownType) {
        val cooldowns = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        cooldowns.reset(type)
    }

    override fun loadCache(uuid: UUID) {
        database.useTransaction {
            val cooldowns = PlayerCooldowns()
            val loadedCooldowns = database.cooldowns.filter { it.player eq uuid }.toList()

            loadedCooldowns.forEach {
                val type = cooldownById[it.type] ?: return
                cooldowns.register(it.toDomain(type))
            }

            val loadedCooldownsIds = loadedCooldowns.map { it.type }.toList()

            cooldownById.values.forEach { type ->
                if (type.id in loadedCooldownsIds) return@forEach
                val remoteCooldown = RemoteCooldown {
                    this.player = uuid
                    this.type = type.id
                    this.resetAt = System.currentTimeMillis()
                }
                println("ADDED NEW COOLDOWN")
                database.cooldowns.add(remoteCooldown)
                cooldowns.register(remoteCooldown.toDomain(type))
            }

            cache[uuid] = cooldowns
        }
    }

    override fun verifyCache(uuid: UUID) = cache.containsKey(uuid) && cache[uuid]!!.entries().size == cooldownById.size

    override fun flushCache(uuid: UUID) {
        val cooldowns = cache.remove(uuid) ?: return
        TaskDispatcher.dispatchAsync {
            cooldowns.entries().forEach { database.cooldowns.update(it.toRemote(uuid)) }
        }
    }

    fun flushCache() {
        cache.clear()
        cooldownById.clear()
    }
}
