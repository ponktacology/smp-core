package gg.traphouse.core.util

import gg.traphouse.core.Console
import gg.traphouse.core.Unicode
import me.vaperion.blade.exception.BladeExitMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

object SenderUtil {

    fun resolveIssuerUUID(sender: CommandSender) = when (sender) {
        is ConsoleCommandSender -> Console.UUID
        is Player -> sender.uniqueId
        else -> throw BladeExitMessage("Couldn't resolve issuer.")
    }

    fun Player.sendNoPermission() = this.sendPrefixed(Unicode.WARNING_SIGN, NamedTextColor.RED, "No permission.")

    fun Player.sendOnCooldown(message: String) = this.sendPrefixed(Unicode.CLOCK, NamedTextColor.YELLOW, message)

    fun CommandSender.sendSuccess(success: Component) =
        this.sendPrefixed(Unicode.HAPPY_FACE, NamedTextColor.GREEN, success)

    fun CommandSender.sendSuccess(success: String) = sendSuccess(Component.text(success))

    fun CommandSender.sendError(error: Component) = this.sendPrefixed(Unicode.SAD_FACE, NamedTextColor.RED, error)

    fun CommandSender.sendError(error: String) = sendError(Component.text(error))

    fun CommandSender.sendPrefixed(prefix: String, color: NamedTextColor, message: Component) =
        this.sendMessage(Component.text("$prefix ", color).append(message))

    fun CommandSender.sendPrefixed(prefix: String, color: NamedTextColor, message: String) =
        this.sendPrefixed(prefix, color, Component.text(message))
}
