package me.smp.core.teleport

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TeleportService : KoinComponent {

    private val teleportRepository: TeleportRepository by inject()

    fun teleport(player: Player, location: Location, delayInSeconds: Int) {
        if (player.hasPermission("core.teleport.bypassDelay")) {
            player.teleport(location)
            return
        }

        teleportRepository.teleport(player, location, delayInSeconds)
    }

    fun isTeleporting(player: Player) = teleportRepository.isTeleporting(player)

    fun cancel(player: Player) {
        teleportRepository.cancelTeleport(player)
        player.sendActionBar(Component.text("Teleportation was cancelled", NamedTextColor.RED))
    }
}