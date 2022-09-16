package me.smp.core.warp

import me.smp.core.teleport.TeleportService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WarpService : KoinComponent {

    private val warpRepository: WarpRepository by inject()
    private val teleportService: TeleportService by inject()

    fun getByName(name: String) = warpRepository.getByName(name)

    fun teleport(player: Player, warp: Warp) {
        warp.permission?.let {
            if (!player.hasPermission(it)) {
                player.sendMessage("You don't have permission to use this warp.")
                return
            }
        }

        val location = warp.location ?: run {
            player.sendMessage("This warp is currently unavailable.")
            return
        }

        teleportService.teleport(player, location, 5)
    }

    fun add(warp: Warp) = warpRepository.createWarp(warp)

    fun remove(warp: Warp) = warpRepository.removeWarp(warp)

    fun update(warp: Warp) = warpRepository.update(warp)

    fun getAll() = warpRepository.getAll()

}