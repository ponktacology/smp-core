package gg.traphouse.core.staff

import gg.traphouse.core.TaskDispatcher
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishListener : Listener, KoinComponent {

    private val staffService: StaffService by inject()

    @EventHandler(ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        if (!staffService.getByOnlinePlayer(event.player).vanish) return
        event.block.type = Material.AIR
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: BlockPlaceEvent) {
        if (!staffService.getByOnlinePlayer(event.player).vanish) return
        TaskDispatcher.dispatchLater({ event.blockPlaced.type = event.itemInHand.type }, 1L)
        event.isCancelled = true
    }
}