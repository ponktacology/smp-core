package me.smp.core

import me.vaperion.blade.exception.BladeExitMessage
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

object SenderUtil {

    fun resolveIssuerUUID(sender: CommandSender) = when (sender) {
        is ConsoleCommandSender -> Console.UUID
        is Player -> sender.uniqueId
        else -> throw BladeExitMessage("Couldn't resolve issuer.")
    }
}
