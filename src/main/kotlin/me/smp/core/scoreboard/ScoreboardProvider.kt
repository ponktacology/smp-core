package me.smp.core.scoreboard

import org.bukkit.entity.Player

interface ScoreboardProvider {

    fun scoreboard(player: Player): List<String>
}
