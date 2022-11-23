package me.smp.core.freeze

import me.smp.core.Config
import me.smp.core.player.PlayerLookupService
import me.smp.core.rank.RankService
import me.smp.core.util.StaffUtil
import me.smp.shared.network.NetworkHandler
import me.smp.shared.network.NetworkListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FreezeListener : Listener, NetworkListener, KoinComponent {

    private val freezeService: FreezeService by inject()
    private val rankService: RankService by inject()
    private val playerLookupService: PlayerLookupService by inject()

    @NetworkHandler
    fun onFreeze(packet: PacketFreeze) {
        val player = packet.player
        var component = Component.text("[Freeze] ", NamedTextColor.AQUA)
            .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
            .append(rankService.getDisplayName(packet.issuer))
            .append(Component.text(" froze "))
            .append(rankService.getDisplayName(player))

        Bukkit.getPlayer(player)?.let {
            component = component
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${it.name}"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport to ${it.name}")))
        }

        StaffUtil.messageStaff(component)
    }

    @NetworkHandler
    fun onFreeze(packet: PacketFreezeLogout) {
        val player = packet.player

        var component = Component.text("[Freeze] ", NamedTextColor.AQUA)
            .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
            .append(rankService.getDisplayName(player))
            .append(Component.text(" logged out while frozen"))

        playerLookupService.getNameByUUID(packet.player)?.let {
            component = component.clickEvent(
                ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/ban -s $it perm Logged out while being frozen"
                )
            )
                .hoverEvent(HoverEvent.showText(Component.text("Click to ban $it")))
        }

        StaffUtil.messageStaff(component)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (freezeService.isFrozen(player)) {
            freezeService.loggedOutWhileFrozen(player)
        }
    }
}