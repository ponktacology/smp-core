package gg.traphouse.core.scoreboard

import fr.mrmicky.fastboard.FastBoard
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ScoreboardRepository {

    private val cache = ConcurrentHashMap<UUID, FastBoard>()

    fun getOrCreate(player: Player): FastBoard {
        return cache.computeIfAbsent(player.uniqueId) { FastBoard(player) }
    }

    fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    fun flushCache() {
        cache.clear()
    }
}
