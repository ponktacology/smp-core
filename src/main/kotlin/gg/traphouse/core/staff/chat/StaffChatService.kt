package gg.traphouse.core.staff.chat

import gg.traphouse.core.TaskDispatcher
import gg.traphouse.shared.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffChatService : KoinComponent {

    private val networkService: NetworkService by inject()

    fun message(sender: Player, message: String) =
        TaskDispatcher.dispatchAsync { networkService.publish(PacketStaffChat(sender.uniqueId, message)) }
}