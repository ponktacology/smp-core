package me.smp.core.nametag

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NameTagListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getOnlinePlayers().forEach {
            NameTags.color(event.player, it, NamedTextColor.WHITE)
            NameTags.color(it, event.player, NamedTextColor.WHITE)
        }
    }
}