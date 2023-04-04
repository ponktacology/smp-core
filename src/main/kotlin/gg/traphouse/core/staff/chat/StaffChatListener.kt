package gg.traphouse.core.staff.chat

import gg.traphouse.core.Config
import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.StaffUtil
import gg.traphouse.shared.network.NetworkHandler
import gg.traphouse.shared.network.NetworkListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffChatListener : NetworkListener, KoinComponent {

    private val rankService: RankService by inject()

    @NetworkHandler
    fun onStaffChatMessage(packet: PacketStaffChat) {
        val player = packet.player
        val component = Component.text("[StaffChat] ", NamedTextColor.AQUA)
            .append(Component.text("[${Config.SERVER_NAME}] ", NamedTextColor.BLUE))
            .append(rankService.getDisplayName(player))
            .append(Component.text(": ${packet.message}", NamedTextColor.WHITE))

        StaffUtil.messageStaff(component)
    }
}
