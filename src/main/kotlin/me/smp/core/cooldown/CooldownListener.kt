package me.smp.core.cooldown

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CooldownListener : KoinComponent, Listener {

    private val cooldownService: CooldownService by inject()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMessage(event: AsyncChatEvent) {
        val player = event.player

        if (!player.hasPermission("chat.cooldown.bypass") &&
            cooldownService.isOnCooldown(player, Cooldowns.CHAT)) {
            player.sendMessage("${ChatColor.RED}Wait before using chat again.")
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, Cooldowns.CHAT)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (!player.hasPermission("command.cooldown.bypass") &&
            cooldownService.isOnCooldown(player, Cooldowns.COMMAND)) {
            player.sendMessage("${ChatColor.RED}Wait before using commands again.")
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, Cooldowns.COMMAND)
    }
}