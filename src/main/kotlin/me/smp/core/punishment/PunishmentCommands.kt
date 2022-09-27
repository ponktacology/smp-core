package me.smp.core.punishment

import me.smp.core.Duration
import me.smp.core.PlayerMetadata
import me.smp.core.SenderUtil
import me.vaperion.blade.annotation.argument.*
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PunishmentCommands : KoinComponent {

    private val punishmentService: PunishmentService by inject()

    @Command("ban")
    @Permission("core.ban")
    @Async
    fun ban(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("duration") duration: Duration,
        @Name("reason") @Text @Optional("Didn't respect server rules.") reason: String,
        @Flag('s') silent: Boolean
    ) = addPunishment(sender, player, Punishment.Type.BAN, duration, reason, silent)

    @Command("unban")
    @Permission("core.unban")
    @Async
    fun unban(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("reason") @Text @Optional("Didn't respect server rules.") reason: String,
        @Flag('s') silent: Boolean
    ) = removePunishment(sender, player, Punishment.Type.BAN, reason, silent)

    @Command("mute")
    @Permission("core.mute")
    @Async
    fun mute(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("duration") duration: Duration,
        @Name("reason") @Text @Optional("Spam.") reason: String,
        @Flag('s') silent: Boolean
    ) = addPunishment(sender, player, Punishment.Type.MUTE, duration, reason, silent)

    @Command("unmute")
    @Permission("core.unmute")
    @Async
    fun unmute(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("reason") @Text @Optional("Unmuted.") reason: String,
        @Flag('s') silent: Boolean
    ) = removePunishment(sender, player, Punishment.Type.MUTE, reason, silent)

    @Command("kick")
    @Permission("core.kick")
    @Async
    fun kick(
        @Sender sender: CommandSender,
        @Name("player") player: PlayerMetadata,
        @Name("reason") @Text @Optional("Didn't respect server rules.") reason: String,
        @Flag('s') silent: Boolean
    ) = addPunishment(sender, player, Punishment.Type.KICK, Duration(0), reason, silent)

    private fun addPunishment(
        sender: CommandSender, player: PlayerMetadata,
        type: Punishment.Type, duration: Duration, reason: String,
        silent: Boolean
    ) {
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)

        punishmentService.getByUUID(player.uuid, type)?.let {
            sender.sendMessage("This player is already punished.")
            return
        }

        punishmentService.punish(Punishment {
            this.player = player.uuid
            this.type = type
            this.address = Bukkit.getPlayer(player.uuid)?.address?.address.toString()
            this.duration = duration
            this.issuer = issuerUUID
            this.addedAt = System.currentTimeMillis()
            this.reason = reason
            this.removed = false
        }, silent)
    }

    private fun removePunishment(
        sender: CommandSender, player: PlayerMetadata,
        type: Punishment.Type, reason: String,
        silent: Boolean
    ) {
        val issuerUUID = SenderUtil.resolveIssuerUUID(sender)

        punishmentService.getByUUID(player.uuid, type) ?: kotlin.run {
            sender.sendMessage("This player is not punished.")
            return
        }

        punishmentService.removePunishments(player.uuid, type, issuerUUID, reason, silent)
    }
}