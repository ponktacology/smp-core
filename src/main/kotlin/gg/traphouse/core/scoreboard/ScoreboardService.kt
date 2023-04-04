package gg.traphouse.core.scoreboard

import gg.traphouse.core.TaskDispatcher
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScoreboardService : KoinComponent {

    private val scoreboardRepository: ScoreboardRepository by inject()
    private var provider: ScoreboardProvider? = null

    fun start() {
        TaskDispatcher.runRepeatingAsync(ScoreboardTask(), 10L)
    }

    fun registerProvider(provider: ScoreboardProvider) {
        this.provider = provider
    }

    fun reload(player: Player) {
        val provider = provider ?: return
        val board = scoreboardRepository.getOrCreate(player)
        val lines = provider.scoreboard(player)
        board.updateTitle(provider.title(player))
        board.updateLines(lines)
    }

    private inner class ScoreboardTask : Runnable {
        override fun run() {
            Bukkit.getOnlinePlayers().forEach {
                this@ScoreboardService.reload(it)
            }
        }
    }
}