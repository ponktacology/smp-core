package me.smp.core.rank

import me.smp.core.*
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class RankRepository : KoinComponent, UUIDCache {

    private val database: Database by inject()
    private val Database.grants get() = this.sequenceOf(Grants)
    private val cache = ConcurrentHashMap<UUID, Rank>()

    fun getByOnlinePlayer(player: Player): Rank {
        return cache[player.uniqueId] ?: throw PlayerNotOnlineException()
    }

    fun getByUUID(uuid: UUID): Rank {
        SyncCatcher.verify()
        if (uuid == Console.UUID) return Rank.CONSOLE

        cache[uuid]?.let {
            return it
        }

        val grants = database.grants.filter { it.player eq uuid }.toList()

        return if (grants.isEmpty()) {
            Rank.DEFAULT
        } else {
            grants.filter { it.isActive() }.maxBy { it.rank }.rank
        }
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        val grants = database.grants.filter { it.player eq uuid }.toList()
        cache[uuid] = if (grants.isEmpty()) {
            database.grants.add(Grant {
                this.player = uuid
                this.rank = Rank.DEFAULT
                this.addedAt = System.currentTimeMillis()
                this.issuer = Console.UUID
                this.reason = "Default Rank"
                this.duration = Duration.PERMANENT
                this.removed = false
            })
            Rank.DEFAULT
        } else {
            grants.filter { it.isActive() }.maxBy { it.rank }.rank
        }
    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }

    fun grantRank(uuid: UUID, grant: Grant): Int {
        SyncCatcher.verify()
        return database.grants.add(grant).also {
            cache[uuid]?.let {
                if (it < grant.rank) {
                    cache[uuid] = grant.rank
                }
            }
        }
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

        cache[uuid]?.let {
            if (it == rank) {
                loadCache(uuid) //Resolve rank again
            }
        }
    }
}