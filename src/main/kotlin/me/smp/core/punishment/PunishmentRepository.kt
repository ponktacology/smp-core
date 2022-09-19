package me.smp.core.punishment

import me.smp.core.PlayerNotOnlineException
import me.smp.core.SyncCatcher
import me.smp.core.UUIDCache
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.not
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class PunishmentRepository : KoinComponent, UUIDCache {

    private val database: Database by inject()
    private val Database.punishments get() = this.sequenceOf(Punishments)
    private val cache = ConcurrentHashMap<UUID, MutableList<Punishment>>()

    fun getByOnlinePlayer(player: Player, type: Punishment.Type): Punishment? {
        val punishments = cache[player.uniqueId] ?: throw PlayerNotOnlineException()
        return punishments.firstOrNull { it.type == type && it.isActive() }
    }

    fun getByUUID(uuid: UUID, type: Punishment.Type): Punishment? {
        SyncCatcher.verify()
        cache[uuid]?.let {
            return it.firstOrNull { punishment -> punishment.type == type && punishment.isActive() }
        }

        val grants = database.punishments.filter { it.player eq uuid }.toList()

        return grants.firstOrNull { it.type == type && it.isActive() }
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        cache[uuid] = database.punishments.filter { it.player eq uuid }.toMutableList()
    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }

    fun punish(punishment: Punishment) {
        SyncCatcher.verify()
        database.punishments.add(punishment)
        cache[punishment.player]?.let {
            cache[punishment.player]!!.add(punishment)
        }
    }

    fun removePunishments(uuid: UUID, type: Punishment.Type, issuer: UUID, reason: String) {
        SyncCatcher.verify()
        database.batchUpdate(Punishments) {
            item {
                set(it.removed, true)
                set(it.removedAt, System.currentTimeMillis())
                set(it.remover, issuer)
                set(it.removeReason, reason)
                where {
                    it.player eq uuid and !it.removed and (it.type eq type)
                }
            }
        }
        cache[uuid]?.let {
            it.filter { punishment -> !punishment.removed && punishment.type == type }
                .forEach { punishment ->
                    punishment.removed = true
                    punishment.removedAt = System.currentTimeMillis()
                    punishment.remover = issuer
                    punishment.removeReason = reason
                }
        }
    }
}