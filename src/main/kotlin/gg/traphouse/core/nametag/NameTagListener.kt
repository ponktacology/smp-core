package gg.traphouse.core.nametag

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NameTagListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        NameTagHandler.initiatePlayer(event.player)
        NameTagHandler.reloadPlayerNow(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
      //  NameTagHandler.getTeamMap().remove(event.player.name)
    }
}
