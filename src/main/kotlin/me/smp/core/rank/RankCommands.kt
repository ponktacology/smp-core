package me.smp.core.rank

import me.smp.core.player.PlayerContainer
import me.smp.core.staff.StaffService
import me.smp.core.util.SenderUtil
import me.smp.shared.Duration
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RankCommands : KoinComponent {

    private val staffService: StaffService by inject()
    private val rankService: RankService by inject()

    @Command("list")
    @Description("Show all online players")
    fun list(@Sender sender: Player) {
        val ranks = Component.join(
            JoinConfiguration.separator(
                Component.text(",", NamedTextColor.WHITE)
            ),
            Rank.values().map { it.getPrefix() })

        var visiblePlayers = 0
        val players = Component.join(
            JoinConfiguration.separator(Component.text(",", NamedTextColor.WHITE)),
            Bukkit.getOnlinePlayers()
                .filter {
                    val vanished = staffService.getByOnlinePlayer(it).vanish

                    if (!vanished) {
                        visiblePlayers++
                    }

                    return@filter !vanished
                }
                .take(250)
                .map {
                    return@map rankService.getDisplayName(it)
                })

        sender.sendMessage(Component.text("${visiblePlayers}/${Bukkit.getMaxPlayers()}"))
        sender.sendMessage(ranks)
        sender.sendMessage(players)
    }

    @Command("rank add", "grant")
    @Async
    @Permission("core.rank.add")
    @Description("Add rank to the player")
    fun add(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerContainer,
        @Name("rank") rank: Rank,
        @Name("duration") duration: Duration,
        @Text @Optional("Promoted") @Name("reason")
        reason: String
    ) {
        if (rank == Rank.DEFAULT) {
            sender.sendMessage("You can't grant a default rank.")
            return
        }
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByPlayer(sender), rank)) {
                sender.sendMessage("You don't have permission to add this rank to this player.")
                return
            }
        }
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)

        rankService.grant(
            Grant {
                this.player = player.uuid
                this.rank = rank
                this.issuer = issuerUUID
                this.addedAt = System.currentTimeMillis()
                this.duration = duration
                this.reason = reason
                this.removed = false
            }
        )
        sender.sendMessage("Successfully added ${rank.name} rank to the ${player.name}.")
    }

    @Command("rank remove", "removegrant")
    @Async
    @Permission("core.rank.remove")
    @Description("Remove rank from the player")
    fun remove(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerContainer,
        @Name("rank") rank: Rank,
        @Text @Optional("Demoted") @Name("reason")
        reason: String
    ) {
        if (rank == Rank.DEFAULT) {
            sender.sendMessage("You can't remove default rank from a player.")
            return
        }
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByPlayer(sender), rank)) {
                sender.sendMessage("You don't have permission to remove this rank from this player.")
                return
            }
        }
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)
        rankService.removeRanks(player.uuid, rank, issuerUUID, reason)
        sender.sendMessage("Successfully removed ${rank.name} from the ${player.name}.")
    }

    @Command("rank check")
    @Async
    @Permission("core.rank.check")
    @Description("Check player's rank")
    fun check(
        @Sender sender: CommandSender,
        @Name("player") @Optional("me")
        player: PlayerContainer
    ) {
        sender.sendMessage("${player.name}'s rank is ${rankService.getByUUID(player.uuid).name}.")
    }
}
