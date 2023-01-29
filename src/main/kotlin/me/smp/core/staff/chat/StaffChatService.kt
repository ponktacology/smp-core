package me.smp.core.staff.chat

import me.smp.core.TaskDispatcher
import me.smp.shared.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffChatService : KoinComponent {

    private val networkService: NetworkService by inject()

    fun message(sender: Player, message: String) =
        TaskDispatcher.dispatchAsync { networkService.publish(PacketStaffChat(sender.uniqueId, message)) }
}
