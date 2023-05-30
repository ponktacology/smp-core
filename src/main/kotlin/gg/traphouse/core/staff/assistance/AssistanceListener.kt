package gg.traphouse.core.staff.assistance

import gg.traphouse.core.Config
import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.StaffUtil
import gg.traphouse.shared.network.NetworkHandler
import gg.traphouse.shared.network.NetworkListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AssistanceListener : NetworkListener, KoinComponent {

    private val rankService: RankService by inject()

    @NetworkHandler
    fun onPlayerReport(packet: PacketReportPlayer) {
        val player = packet.player
        val issuer = packet.issuer
        var component = Component.text("[Report] ", NamedTextColor.RED)
            .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
            .append(rankService.getDisplayName(issuer))
            .append(Component.text(" zgłosił ", NamedTextColor.GRAY))
            .append(rankService.getDisplayName(player))
            .append(Component.text(" za: ", NamedTextColor.GRAY))
            .append(Component.text(packet.reason, NamedTextColor.GRAY))
        Bukkit.getPlayer(player)?.let {
            component = component
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${it.name}"))
                .hoverEvent(HoverEvent.showText(Component.text("Kliknij, aby się przeteleportować do ${it.name}")))
        }
        StaffUtil.messageStaff(component)
    }

    @NetworkHandler
    fun onPlayerReport(packet: PacketPlayerRequest) {
        val player = packet.player
        var component = Component.text("[HelpOP] ", NamedTextColor.LIGHT_PURPLE)
            .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
            .append(rankService.getDisplayName(player))
            .append(Component.text(" prosi o pomoc: ", NamedTextColor.GRAY))
            .append(Component.text(packet.request, NamedTextColor.GRAY))
        Bukkit.getPlayer(player)?.let {
            component = component
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${it.name}"))
                .hoverEvent(HoverEvent.showText(Component.text("Kliknij, aby się przeteleportować do ${it.name}")))
        }
        StaffUtil.messageStaff(component)
    }
}
