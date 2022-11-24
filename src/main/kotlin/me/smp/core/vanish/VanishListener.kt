package me.smp.core.vanish

import me.smp.core.TaskDispatcher
import me.smp.core.rank.RankService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VanishListener : Listener, KoinComponent {

    private val rankService: RankService by inject()
    private val vanishService: VanishService by inject()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (!rankService.getByPlayer(player).isStaff()) return
        TaskDispatcher.dispatchAsync { vanishService.updateVanish(player) }
    }
}
