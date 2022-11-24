package me.smp.core.nametag

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        FrozenNametagHandler.initiatePlayer(event.player)
        FrozenNametagHandler.reloadPlayer(event.player)
        FrozenNametagHandler.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        FrozenNametagHandler.getTeamMap().remove(event.player.name)
    }
}
