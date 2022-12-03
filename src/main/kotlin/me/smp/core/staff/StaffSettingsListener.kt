package me.smp.core.staff

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffSettingsListener : Listener, KoinComponent {

    private val staffService: StaffService by inject()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        staffService.applyToPlayer(player)
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player) return
        val staffSettings = staffService.getByOnlinePlayer(player)
        if (staffSettings.god) { event.isCancelled = true }
    }
}
