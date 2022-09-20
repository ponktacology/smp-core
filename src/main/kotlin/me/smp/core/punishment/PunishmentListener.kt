package me.smp.core.punishment


import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.logging.Level
import java.util.logging.Logger

class PunishmentListener : KoinComponent, Listener {

    private val logger: Logger by inject()
    private val punishmentService: PunishmentService by inject()

    @EventHandler
    fun onProfileLogin(event: AsyncPlayerPreLoginEvent) {
        punishmentService.getByUUID(event.uniqueId, Punishment.Type.BAN)?.let {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text("Banned :(!"))
            logger.log(Level.INFO, "Player ${event.name} tried to join but is banned.")
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerChatMessage(event: AsyncChatEvent) {
        punishmentService.getByPlayer(event.player, Punishment.Type.MUTE)?.let {
            event.player.sendMessage("You can't use chat while muted!")
            event.isCancelled = true
        }
    }

}