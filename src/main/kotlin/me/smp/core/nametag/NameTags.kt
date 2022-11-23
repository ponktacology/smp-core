package me.smp.core.nametag

import me.smp.core.rank.Rank
import me.smp.core.rank.RankService
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NameTags : KoinComponent {

    private val rankService: RankService by inject()

    private val teamNames = mutableSetOf<String>()

    fun color(player: Player, other: Player, color: NamedTextColor) {
        if (player == other) return

        var scoreboard = player.scoreboard
        if (scoreboard == Bukkit.getServer().scoreboardManager.mainScoreboard) {
            scoreboard = Bukkit.getServer().scoreboardManager.newScoreboard
        }

        val rank = rankService.getByPlayer(other)
        var team = player.scoreboard.getTeam(getTeamName(color, rank))

        if (team == null) {
            val teamName = getTeamName(color, rank)
            team = player.scoreboard.registerNewTeam(teamName)

            teamNames.add(teamName)

            team.color(color)
            team.prefix(rank.getPrefix())
        }

        if (!team.hasEntry(other.name)) {
            reset(player, other)
            println("Added ${other.name} to ${player.name} ${team.name}")
            team.addEntry(other.name)
        }

        player.scoreboard = scoreboard
    }

    fun reset(player: Player, other: Player) {
        if (other == player) return
        player.scoreboard.getObjective(DisplaySlot.BELOW_NAME)?.unregister()
        teamNames.forEach {
            player.scoreboard.getTeam(it)?.removeEntry(other.name)
            println("Removed ${other.name} from ${player.name} $it")
        }
    }

    private fun getTeamName(color: NamedTextColor, rank: Rank) = rank.name + color.toString()
}
