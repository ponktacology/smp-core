package gg.traphouse.core

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GeneralListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Component.empty())
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(Component.empty())
    }
}
