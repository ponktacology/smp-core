package me.smp.core.rank

import me.smp.core.Console
import me.smp.core.SyncCatcher
import me.smp.core.UUIDCache
import me.smp.core.player.PlayerNotFoundInCacheException
import me.smp.shared.Duration
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.not
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RankRepository : KoinComponent, UUIDCache {

    private val database: Database by inject()
    private val Database.grants get() = this.sequenceOf(Grants)
    private val cache = ConcurrentHashMap<UUID, Rank>()

    fun getByPlayer(player: Player): Rank {
        return cache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
    }

    fun getByUUID(uuid: UUID): Rank {
        SyncCatcher.verify()
        if (uuid == Console.UUID) return Rank.CONSOLE

        cache[uuid]?.let {
            return it
        }

        return findOrCreate(uuid)
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        cache[uuid] = findOrCreate(uuid)
    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    override fun verifyCache(uuid: UUID) = cache.containsKey(uuid)

    fun flushCache() {
        cache.clear()
    }

    fun grantRank(uuid: UUID, rank: Rank) {
        cache[uuid]?.let {
            if (it.power < rank.power) {
                cache[uuid] = rank
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

    fun unGrant(uuid: UUID, rank: Rank) {
        cache[uuid]?.let {
            if (it == rank) {
                loadCache(uuid) // Resolve rank again
            }
        }
    }

    private fun findOrCreate(uuid: UUID): Rank {
        database.useTransaction {
            val grants = database.grants.filter { it.player eq uuid }.toList()
            return if (grants.isEmpty()) {
                database.grants.add(
                    Grant {
                        this.player = uuid
                        this.rank = Rank.DEFAULT
                        this.addedAt = System.currentTimeMillis()
                        this.issuer = Console.UUID
                        this.reason = "Default Rank"
                        this.duration = Duration.PERMANENT
                        this.removed = false
                    }
                )
                Rank.DEFAULT
            } else grants.filter { it.isActive() }.maxBy { it.rank.power }.rank
        }
    }
}
