package me.smp.core.assistance

import me.smp.core.network.NetworkHandler
import me.smp.core.network.NetworkListener
import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
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
        val displayName = rankService.getDisplayName(player)
        val issuerDisplayName = rankService.getDisplayName(issuer)
        val component = Component.text("[Report] ", NamedTextColor.RED)
            .append(issuerDisplayName)
            .append(Component.text(" reported ", NamedTextColor.GRAY))
            .append(displayName)
            .append(Component.text(" for: ", NamedTextColor.GRAY))
            .append(Component.text(packet.reason, NamedTextColor.GRAY))
        messageStaff(component)
    }

    @NetworkHandler
    fun onPlayerReport(packet: PacketPlayerRequest) {
        val player = packet.player
        val displayName = rankService.getDisplayName(player)
        val component = Component.text("[Request] ", NamedTextColor.LIGHT_PURPLE)
            .append(displayName)
            .append(Component.text(" requested help: ", NamedTextColor.GRAY))
            .append(Component.text(packet.request, NamedTextColor.GRAY))
        messageStaff(component)
    }

    fun messageStaff(component: Component) {
        for (staff in Bukkit.getOnlinePlayers().filter { rankService.getByPlayer(it).isStaff() }) {
            staff.sendMessage(component)
        }
    }
}