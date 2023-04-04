package gg.traphouse.core.punishment

import gg.traphouse.core.SyncCatcher
import gg.traphouse.core.player.PlayerNotFoundInCacheException
import gg.traphouse.shared.punishment.Punishment
import gg.traphouse.shared.punishment.Punishments
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PunishmentRepository : KoinComponent {

    private val database: Database by inject()
    private val Database.punishments get() = this.sequenceOf(Punishments)
    private val cache = ConcurrentHashMap<UUID, PlayerPunishments>()

    fun getByPlayer(player: Player, type: Punishment.Type): Punishment? {
        val punishments = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return punishments.findActive(type)
    }

    fun getByUUID(uuid: UUID, type: Punishment.Type): Punishment? {
        SyncCatcher.verify()
        cache[uuid]?.let {
            return it.findActive(type)
        }

        val punishments =
            database.punishments.filter { it.player eq uuid }
                .toList()

        return punishments.firstOrNull { it.type == type && it.isActive() }
    }

    fun getByUUID(uuid: UUID, address: String, type: Punishment.Type): Punishment? {
        SyncCatcher.verify()
        cache[uuid]?.let {
            return it.findActive(type)
        }

        val punishments =
            database.punishments.filter { it.player eq uuid or (it.address.isNotNull() and (it.address eq address)) }
                .toList()

        return punishments.firstOrNull { it.type == type && it.isActive() }
    }

    fun punish(punishment: Punishment) {
        cache[punishment.player]?.add(punishment)
    }

    fun addPunishment(punishment: Punishment) {
        SyncCatcher.verify()
        database.punishments.add(punishment)
    }

    fun unPunish(uuid: UUID, type: Punishment.Type, issuer: UUID, reason: String) {
        cache[uuid]?.unPunish(type, issuer, reason)
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
    }

    fun getById(punishmentId: Int): Punishment? {
        SyncCatcher.verify()
        return database.punishments.find { it.id eq punishmentId }
    }

    fun loadCache(uuid: UUID, address: String) {
        SyncCatcher.verify()
        cache[uuid] = PlayerPunishments().also { punishments ->
            punishments.addAll(database.punishments.filter { it.player eq uuid or (it.address.isNotNull() and (it.address eq address)) }
                .toList())
        }
    }

    fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }

}
