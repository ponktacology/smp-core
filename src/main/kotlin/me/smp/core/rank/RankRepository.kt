package me.smp.core.rank

import me.smp.core.*
import me.smp.core.nametag.FrozenNametagHandler
import me.smp.core.player.PlayerNotFoundInCacheException
import me.smp.shared.Duration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.java.JavaPlugin
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
    private val cache = ConcurrentHashMap<UUID, MutableList<Grant>>()
    private val permissionAttachments = HashMap<UUID, PermissionAttachment>()

    fun getByPlayer(player: Player): Rank {
        val grants = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return resolveRank(grants)
    }

    fun getByUUID(uuid: UUID): Rank {
        SyncCatcher.verify()
        if (uuid == Console.UUID) return Rank.CONSOLE

        cache[uuid]?.let {
            return resolveRank(it)
        }

        return resolveRank(findOrCreate(uuid))
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

    @Synchronized
    fun grant(uuid: UUID, grantId: Int) {
        SyncCatcher.verify()
        val grant =
            database.grants.find { it.player eq uuid and (it.id eq grantId) }
                ?: error("trying to grant non-existing grant")
        cache[uuid]?.let {
            it.add(grant)
            Bukkit.getPlayer(uuid)?.let { player ->
                FrozenNametagHandler.reloadPlayer(player)
                TaskDispatcher.dispatch { recalculatePermissions(player) }
            }
        }
    }

    @Synchronized
    fun addGrant(grant: Grant) {
        SyncCatcher.verify()
        database.grants.add(grant)
    }

    @Synchronized
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

    @Synchronized
    fun unGrant(uuid: UUID, rank: Rank, issuer: UUID, reason: String) {
        SyncCatcher.verify()
        cache[uuid]?.let { grants ->
            grants.filter { it.rank == rank && it.isActive() }
                .forEach {
                    it.removed = true
                    it.issuer = issuer
                    it.removedAt = System.currentTimeMillis()
                    it.remover = issuer
                    it.removeReason = reason
                }
            Bukkit.getPlayer(uuid)?.let { player ->
                FrozenNametagHandler.reloadPlayer(player)
                TaskDispatcher.dispatch { recalculatePermissions(player) }
            }
        }
    }

    private fun findOrCreate(uuid: UUID): MutableList<Grant> {
        SyncCatcher.verify()
        database.useTransaction {
            val grants = database.grants.filter { it.player eq uuid }.toMutableList()
            return if (grants.isEmpty()) {
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
                return grants
            } else return grants
        }
    }

    private fun resolveRank(grants: List<Grant>): Rank {
        return grants.filter { it.isActive() }.maxByOrNull { it.rank.power }?.rank ?: Rank.DEFAULT
    }

    fun recalculatePermissions(player: Player) {
        val grants = cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        val attachment = permissionAttachments.computeIfAbsent(player.uniqueId) { player.addAttachment(plugin) }
        attachment.permissions.forEach { (t, _) -> attachment.unsetPermission(t) }
        grants.filter { it.isActive() }.forEach { grant ->
            val rank = grant.rank
            rank.permissions.forEach {
                val value = !it.startsWith("-")
                attachment.setPermission(if (value) it else it.replaceFirst("-", "").trim(), value)
            }
        }
        player.recalculatePermissions()
    }
}
