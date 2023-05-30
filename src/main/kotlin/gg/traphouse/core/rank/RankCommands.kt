package gg.traphouse.core.rank

import gg.traphouse.core.player.PlayerContainer
import gg.traphouse.core.staff.StaffService
import gg.traphouse.core.util.SenderUtil
import gg.traphouse.core.util.SenderUtil.sendError
import gg.traphouse.core.util.SenderUtil.sendNoPermission
import gg.traphouse.core.util.SenderUtil.sendSuccess
import gg.traphouse.core.util.StaffUtil.isStaff
import gg.traphouse.shared.Duration
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RankCommands : KoinComponent {

    private val staffService: StaffService by inject()
    private val rankService: RankService by inject()

    @Command("list", "online", "gracze", "who")
    @Description("Lista graczy, którzy są aktualnie online")
    fun list(@Sender sender: CommandSender) {
        val ranks = Component.join(
            JoinConfiguration.separator(
                Component.text(", ", NamedTextColor.WHITE)
            ),
            Rank.values().map { it.getPrefix() })

        var visiblePlayers = 0
        val players = Component.join(
            JoinConfiguration.separator(Component.text(", ", NamedTextColor.WHITE)),
            Bukkit.getOnlinePlayers()
                .filter {
                    val vanished = !sender.isStaff() && staffService.getByOnlinePlayer(it).vanish

                    if (!vanished) {
                        visiblePlayers++
                    }

                    return@filter !vanished
                }
                .sortedByDescending { rankService.getByPlayer(it).power }
                .take(250)
                .map {
                    return@map it.name().color(rankService.getByPlayer(it).color)
                })

        sender.sendMessage(Component.text("${visiblePlayers}/${Bukkit.getMaxPlayers()}"))
        sender.sendMessage(ranks)
        sender.sendMessage(players)
    }

    @Command("rank add", "grant")
    @Async
    @Permission("core.rank.add")
    @Description("Nadaje rangę graczowi")
    fun add(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerContainer,
        @Name("rank") rank: Rank,
        @Name("duration") duration: Duration,
        @Text @Optional("Awans") @Name("reason")
        reason: String
    ) {
        if (rank == Rank.DEFAULT) {
            sender.sendError("Nie możesz nadać graczowi podstawowej rangi.")
            return
        }
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByPlayer(sender), rank)) {
                sender.sendNoPermission()
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

        sender.sendSuccess("Pomyślnie nadano ${player.name} rangę ${rank.name}.")
    }

    @Command("rank remove", "removegrant")
    @Async
    @Permission("core.rank.remove")
    @Description("Usuwa rangę graczowi")
    fun remove(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerContainer,
        @Name("rank") rank: Rank,
        @Text @Optional("Degrad") @Name("reason")
        reason: String
    ) {
        if (rank == Rank.DEFAULT) {
            sender.sendError("Nie możesz zdegradować gracza z podstawowej rangi.")
            return
        }
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByPlayer(sender), rank)) {
                sender.sendNoPermission()
                return
            }
        }
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)
        rankService.removeRanks(player.uuid, rank, issuerUUID, reason)
        sender.sendSuccess("Pomyślnie zdegradowano gracza ${player.name} z rangi ${rank.name}.")
    }

    @Command("rank check")
    @Async
    @Permission("core.rank.check")
    @Description("Sprawdza rangę gracza")
    fun check(
        @Sender sender: CommandSender,
        @Name("player") @Optional("me")
        player: PlayerContainer
    ) {
        sender.sendSuccess("Ranga gracza ${player.name} to ${rankService.getByUUID(player.uuid).name}.")
    }
}
