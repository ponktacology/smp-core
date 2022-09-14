package me.smp.core.rank

import me.smp.core.SenderUtil
import me.smp.core.Duration
import me.smp.core.PlayerMetadata
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import me.vaperion.blade.exception.BladeExitMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RankCommands : KoinComponent {

    private val rankService: RankService by inject()

    @Command("rank add")
    @Async
    @Permission("core.rank.add")
    @Description("Add rank to the player")
    fun add(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("rank") rank: Rank,
        @Name("duration") duration: Duration,
        @Text @Optional("Promoted") @Name("reason") reason: String
    ) {
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByOnlinePlayer(sender), rank)) {
                throw BladeExitMessage("You don't have permission to add this rank to this player.")
            }
        }
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)
        rankService.grant(Grant {
            this.player = player.uuid
            this.rank = rank
            this.issuer = issuerUUID
            this.addedAt = System.currentTimeMillis()
            this.duration = duration
            this.reason = reason
            this.removed = false
        })
        sender.sendMessage("Successfully added ${rank.name} rank to the ${player.name}.")
    }

    @Command("rank remove")
    @Async
    @Permission("core.rank.remove")
    @Description("Remove rank from the player")
    fun remove(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("rank") rank: Rank,
        @Text @Optional("Demoted") @Name("reason") reason: String
    ) {
        if (sender is Player) {
            if (RankValidator.isMorePowerful(rankService.getByOnlinePlayer(sender), rank)) {
                throw BladeExitMessage("You don't have permission to remove this rank from this player.")
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
    fun check(@Sender sender: Player, @Name("player") @Optional("me") player: PlayerMetadata) {
        sender.sendMessage("${player.name}'s rank is ${rankService.getByUUID(player.uuid).name}.")
    }
}