package me.smp.core.chat.staff

import me.smp.core.network.NetworkHandler
import me.smp.core.network.NetworkListener
import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffChatListener : NetworkListener, KoinComponent {

    private val rankService: RankService by inject()

    @NetworkHandler
    fun onStaffChatMessage(packet: PacketStaffChat) {
        val player = packet.player
        val displayName = rankService.getDisplayName(player)
        val component = Component.text("[StaffChat] ", NamedTextColor.AQUA)
            .append(displayName)
            .append(Component.text(": ${packet.message}", NamedTextColor.WHITE))

        for (staff in Bukkit.getOnlinePlayers().filter { rankService.getByPlayer(it).isStaff() }) {
            staff.sendMessage(component)
        }
    }
}