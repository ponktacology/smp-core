package gg.traphouse.core.fix

import gg.traphouse.core.Plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CaseInsensetiveCommandsFix : KoinComponent {

    private val plugin: Plugin by inject()

    init {
        plugin.server.pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun on(event: PlayerCommandPreprocessEvent) {
               // TODO: Fix event.message = event.message.lowercase()
            }
        }, plugin)
    }
}