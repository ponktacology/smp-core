package me.smp.core.teleport

import me.smp.core.util.LocationUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TeleportListener : Listener, KoinComponent {

    private val teleportService: TeleportService by inject()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!LocationUtil.hasChanged(event.from, event.to)) return
        val player = event.player
        if (teleportService.isTeleporting(player)) {
            teleportService.cancel(player)
        }
    }
}