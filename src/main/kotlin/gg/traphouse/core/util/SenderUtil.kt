package gg.traphouse.core.util

import gg.traphouse.core.Console
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

    fun Player.sendSuccess(success: String) = sendSuccess(Component.text(success))

    fun Player.sendError(error: String) = sendError(Component.text(error))

    fun Player.sendSuccess(success: Component) {
        this.sendMessage(Component.text("☺ ", NamedTextColor.GREEN).append(success))
    }

    fun Player.sendError(error: Component) {
        this.sendMessage(Component.text("☹ ", NamedTextColor.RED).append(error))
    }
}
