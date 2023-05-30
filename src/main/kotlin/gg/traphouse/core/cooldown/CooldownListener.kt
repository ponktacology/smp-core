package gg.traphouse.core.cooldown

import gg.traphouse.core.util.SenderUtil.sendOnCooldown
import gg.traphouse.core.util.StaffUtil.isStaff
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CooldownListener : KoinComponent, Listener {

    private val cooldownService: CooldownService by inject()

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerMessage(event: AsyncChatEvent) {
        val player = event.player

        if (!player.isStaff() && cooldownService.hasCooldown(player, CoreCooldowns.CHAT)) {
            player.sendOnCooldown("Odczekaj chwilę zanim znowu wyślesz wiadomość.")
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, CoreCooldowns.CHAT)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (!player.isStaff() && cooldownService.hasCooldown(player, CoreCooldowns.COMMAND)) {
            player.sendOnCooldown("Odczekaj chwilę zanim znowu użyjesz komendy.")
            event.isCancelled = true
            return
        }

        cooldownService.reset(player, CoreCooldowns.COMMAND)
    }
}
