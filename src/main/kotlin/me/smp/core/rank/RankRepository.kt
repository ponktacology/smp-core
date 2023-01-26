package me.smp.core.rank

import me.smp.core.*
import me.smp.core.nametag.FrozenNametagHandler
import me.smp.core.player.PlayerNotFoundInCacheException
import me.smp.shared.Duration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
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
import kotlin.collections.HashMap

class RankRepository : KoinComponent, UUIDCache {

    private val plugin: Plugin by inject()
    private val database: Database by inject()
    private val Database.grants get() = this.sequenceOf(Grants)
    private val cache = ConcurrentHashMap<UUID, PlayerGrants>()
    private val permissionAttachments = HashMap<UUID, PermissionAttachment>()

    fun getByPlayer(player: Player): Rank {
        val grants = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return grants.findPrimary()
    }

    fun getByUUID(uuid: UUID): Rank {
        SyncCatcher.verify()
        if (uuid == Console.UUID) return Rank.CONSOLE
        val ranks = cache[uuid] ?: findOrCreate(uuid)
        return ranks.findPrimary()
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        cache[uuid] = findOrCreate(uuid)
    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
        permissionAttachments.remove(uuid)?.remove()
    }

    override fun verifyCache(uuid: UUID) = cache.containsKey(uuid)

    fun flushCache() {
        cache.clear()
        permissionAttachments.forEach { (_, u) -> u.remove() }
        permissionAttachments.clear()
    }

    fun grant(uuid: UUID, grantId: Int) {
        SyncCatcher.verify()
        val grant = database.grants.find { it.player eq uuid and (it.id eq grantId) }
            ?: error("trying to grant non-existing grant")
        cache[uuid]?.let {
            it.add(grant)
            Bukkit.getPlayer(uuid)?.let { player ->
                FrozenNametagHandler.reloadPlayer(player)
                TaskDispatcher.dispatch { recalculatePermissions(player) }
            }
        }
    }

    fun addGrant(grant: Grant) {
        SyncCatcher.verify()
        database.grants.add(grant)
    }

    fun removeRank(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        SyncCatcher.verify()
        database.batchUpdate(Grants) {
            item {
                set(it.removed, true)
                set(it.removedAt, System.currentTimeMillis())
                set(it.remover, issuer)
                set(it.removeReason, reason)
                where {
                    it.player eq uuid and !it.removed and (it.rank eq rank)
                }
            }
        }
    }

    fun unGrant(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        SyncCatcher.verify()
        cache[uuid]?.let {
            it.unGrant(rank, issuer, reason)
            Bukkit.getPlayer(uuid)?.let { player ->
                FrozenNametagHandler.reloadPlayer(player)
                TaskDispatcher.dispatch { recalculatePermissions(player) }
            }
        }
    }

    private fun findOrCreate(uuid: UUID): PlayerGrants {
        SyncCatcher.verify()
        val grants = database.grants.filter { it.player eq uuid }.toMutableList()

        if (grants.isEmpty()) {
            val grant = Grant {
                this.player = uuid
                this.rank = Rank.DEFAULT
                this.addedAt = System.currentTimeMillis()
                this.issuer = Console.UUID
                this.reason = "Default Rank"
                this.duration = Duration.PERMANENT
                this.removed = false
            }
            database.grants.add(grant)
            grants.add(grant)
        }

        return PlayerGrants().also {
            it.addAll(grants)
        }
    }

    fun recalculatePermissions(player: Player) {
        val grants = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        val attachment = permissionAttachments.computeIfAbsent(player.uniqueId) { player.addAttachment(plugin) }
        attachment.permissions.forEach { (t, _) -> attachment.unsetPermission(t) }
        grants.activeGrants().forEach { grant ->
            val rank = grant.rank
            rank.permissions.forEach {
                val value = !it.startsWith("-")
                attachment.setPermission(if (value) it else it.replaceFirst("-", "").trim(), value)
            }
        }
        player.recalculatePermissions()
    }
}
