package me.smp.core.pm

import me.smp.core.SyncCatcher
import me.smp.core.TaskDispatcher
import me.smp.core.UUIDCache
import me.smp.core.player.PlayerNotFoundInCacheException
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PrivateMessageRepository : KoinComponent, UUIDCache {

    private val database: Database by inject()
    private val Database.settings get() = this.sequenceOf(PrivateMessagesSettings)
    private val Database.ignored get() = this.sequenceOf(IgnoredPlayersTable)
    private val settingsCache = ConcurrentHashMap<UUID, PrivateMessageSettings>()
    private val ignoredCache = ConcurrentHashMap<UUID, IgnoredPlayers>()
    private val replyCache = ConcurrentHashMap<UUID, UUID>()

    fun getSettingsByPlayer(player: Player): PrivateMessageSettings {
        return settingsCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
    }

    fun isIgnoring(player: Player, other: UUID): Boolean {
        val ignoredPlayers = ignoredCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        return ignoredPlayers.isIgnoring(other)
    }

    fun ignore(player: Player, ignored: UUID) {
        val ignoredPLayers = ignoredCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        ignoredPLayers.ignore(ignored)

        TaskDispatcher.dispatchAsync {
            database.ignored.add(RemoteIgnoredPlayer {
                this.player = player.uniqueId
                this.ignored = ignored
            })
        }

    }

    fun unIgnore(player: Player, ignored: UUID) {
        val ignoredPLayers = ignoredCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
        ignoredPLayers.unIgnore(ignored)

        TaskDispatcher.dispatchAsync {
            database.ignored.removeIf { it.player eq player.uniqueId and (it.ignored eq ignored) }
        }
    }

    fun getReplier(player: Player) = replyCache[player.uniqueId]

    fun setReplier(player: Player, replier: Player) {
        replyCache[player.uniqueId] = replier.uniqueId
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        settingsCache[uuid] = findOrCreate(uuid)
        val ignoredPlayers = IgnoredPlayers()
        database.ignored
            .filter { it.player eq uuid }
            .forEach { ignoredPlayers.ignore(it.ignored) }

        ignoredCache[uuid] = ignoredPlayers
    }

    private fun findOrCreate(uuid: UUID): PrivateMessageSettings {
        return database.settings.find { it.player eq uuid } ?: PrivateMessageSettings {
            this.player = uuid
            this.enabled = true
        }.also {
            database.settings.add(it)
        }
    }

    override fun verifyCache(uuid: UUID) = settingsCache.containsKey(uuid) && ignoredCache.containsKey(uuid)

    override fun flushCache(uuid: UUID) {
        replyCache.remove(uuid)
        ignoredCache.remove(uuid)
        settingsCache.remove(uuid)?.let {
            TaskDispatcher.dispatchAsync { database.settings.update(it) }
        }
    }
}
