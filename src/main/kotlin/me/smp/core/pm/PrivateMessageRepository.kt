package me.smp.core.pm

import me.smp.core.PlayerNotFoundInCacheException
import me.smp.core.SyncCatcher
import me.smp.core.UUIDCache
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PrivateMessageRepository : KoinComponent, UUIDCache {

    private val database: Database by inject()
    private val Database.settings get() = this.sequenceOf(PrivateMessagesSettings)
    private val Database.ignored get() = this.sequenceOf(IgnoredPlayers)
    private val settingsCache = ConcurrentHashMap<UUID, PrivateMessageSettings>()
    private val ignoredCache = ConcurrentHashMap<UUID, MutableList<IgnoredPlayer>>()
    private val replyCache = ConcurrentHashMap<UUID, UUID>()

    fun getSettingsByPlayer(player: Player): PrivateMessageSettings {
        return settingsCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
    }

    fun getIgnoredByPlayer(player: Player): List<IgnoredPlayer> {
        return ignoredCache[player.uniqueId] ?: throw PlayerNotFoundInCacheException()
    }

    fun ignore(player: UUID, ignored: UUID): Int {
        SyncCatcher.verify()
        val ignoredPlayer = IgnoredPlayer {
            this.player = player
            this.ignored = ignored
        }

        return database.ignored.add(ignoredPlayer).also {
            ignoredCache[player]?.add(ignoredPlayer)
        }
    }

    fun unignore(player: UUID, ignored: UUID): Int {
        SyncCatcher.verify()

        ignoredCache[player]?.let {
            it.removeIf { record -> record.ignored == ignored }
        }

        return database.ignored.removeIf { it.player eq player and (it.ignored eq ignored) }
    }

    fun getReplier(player: Player) = replyCache[player.uniqueId]

    fun setReplier(player: Player, replier: Player) {
        replyCache[player.uniqueId] = replier.uniqueId
    }

    override fun loadCache(uuid: UUID) {
        SyncCatcher.verify()
        settingsCache[uuid] = findOrCreate(uuid)
        ignoredCache[uuid] = database.ignored
            .filter { it.player eq uuid }
            .toCollection(CopyOnWriteArrayList())
    }

    override fun flushCache(uuid: UUID) {
        settingsCache.remove(uuid)
        ignoredCache.remove(uuid)
        replyCache.remove(uuid)
    }

    override fun verifyCache(uuid: UUID) = settingsCache.containsKey(uuid) && ignoredCache.containsKey(uuid)

    private fun findOrCreate(uuid: UUID) = database.settings.find { it.player eq uuid } ?: PrivateMessageSettings {
        this.player = uuid
        this.enabled = true
    }.also {
        database.settings.add(it)
    }

}