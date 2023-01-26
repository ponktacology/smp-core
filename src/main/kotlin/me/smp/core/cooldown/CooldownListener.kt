package me.smp.core.cooldown

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CooldownListener : KoinComponent, Listener {

    private val cooldownService: CooldownService by inject()

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerMessage(event: AsyncChatEvent) {
        val player = event.player

        if (!player.hasPermission("chat.cooldown.bypass") &&
            cooldownService.hasCooldown(player, CoreCooldowns.CHAT)
        ) {
            player.sendMessage(Component.text("Wait before using chat again.", NamedTextColor.RED))
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, CoreCooldowns.CHAT)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (!player.hasPermission("command.cooldown.bypass") &&
            cooldownService.hasCooldown(player, CoreCooldowns.COMMAND)
        ) {
            player.sendMessage(Component.text("Wait before using commands again.", NamedTextColor.RED))
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, CoreCooldowns.COMMAND)
    }
}
