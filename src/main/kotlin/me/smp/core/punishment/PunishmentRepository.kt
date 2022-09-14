package me.smp.core.punishment

import me.smp.core.PlayerNotOnlineException
import me.smp.core.SyncCatcher
import me.smp.core.UUIDCache
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PunishmentRepository : KoinComponent, UUIDCache {

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

    fun punish(punishment: Punishment) {
        SyncCatcher.verify()
        database.punishments.add(punishment)
        cache[punishment.player]?.let {
            cache[punishment.player]!!.add(punishment)
        }
    }

    fun removePunishments(uuid: UUID, type: Punishment.Type, issuer: UUID, reason: String) {
        SyncCatcher.verify()
        val punishments =
            cache[uuid] ?: database.punishments.filter { it.player eq uuid }.filter { it.type eq type }.toList()

        punishments.forEach {
            if (it.isActive()) {
                it.removed = true
                it.removedAt = System.currentTimeMillis()
                it.remover = issuer
                it.removeReason = reason
                println(it)
                it.flushChanges()
            }
        }
    }
}