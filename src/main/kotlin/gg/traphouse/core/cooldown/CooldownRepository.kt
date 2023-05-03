package gg.traphouse.core.cooldown

import gg.traphouse.core.TaskDispatcher
import gg.traphouse.core.UUIDCache
import gg.traphouse.core.player.PlayerNotFoundInCacheException
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
    private val cooldownsByType = HashMap<String, CooldownType>()

    fun registerPersistentCooldown(cooldownType: CooldownType) = cooldownsByType.put(cooldownType.type, cooldownType)

    fun isOnCooldown(player: Player, type: CooldownType): Boolean {
        val cooldowns = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return cooldowns.isOnCooldown(type)
    }

    fun reset(player: Player, type: CooldownType) {
        val cooldowns = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        cooldowns.reset(type)
    }

    override fun loadCache(uuid: UUID) {
        val cooldowns = PlayerCooldowns()
        val loadedCooldowns = database.cooldowns.filter { it.player eq uuid }.toList()

        loadedCooldowns.forEach {
            val type = cooldownsByType[it.type] ?: return@forEach
            cooldowns.register(it.toDomain(type))
        }

        cooldownsByType.values.forEach { type ->
            if (loadedCooldowns.any { it.type == type.type }) return@forEach

            val remoteCooldown = RemoteCooldown {
                this.player = uuid
                this.type = type.type
                this.resetAt = System.currentTimeMillis()
            }
            database.cooldowns.add(remoteCooldown)
            cooldowns.register(remoteCooldown.toDomain(type))
        }

        cache[uuid] = cooldowns
    }

    override fun verifyCache(uuid: UUID) =
        cache.containsKey(uuid) && cache[uuid]!!.entries().size == cooldownsByType.size

    override fun flushCache(uuid: UUID) {
        val cooldowns = cache.remove(uuid) ?: return
        TaskDispatcher.dispatchAsync { cooldowns.entries().forEach { database.cooldowns.update(it.toRemote(uuid)) } }
    }

    fun flushCache() {
        cache.clear()
        cooldownsByType.clear()
    }
}
