package me.smp.core.nametag

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NameTagListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getOnlinePlayers().forEach {
            NameTags.color(event.player, it, NamedTextColor.WHITE)
            NameTags.color(it, event.player, NamedTextColor.WHITE)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerQuitEvent) {
        Bukkit.getOnlinePlayers().forEach {
            NameTags.reset(event.player, it)
            NameTags.reset(it, event.player)
        }
    }
}
