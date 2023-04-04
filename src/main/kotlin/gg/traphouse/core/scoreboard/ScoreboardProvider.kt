package gg.traphouse.core.scoreboard

import org.bukkit.entity.Player

interface ScoreboardProvider {

    fun title(player: Player): String

    fun scoreboard(player: Player): List<String>
}
