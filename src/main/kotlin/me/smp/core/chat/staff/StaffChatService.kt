package me.smp.core.chat.staff

import me.smp.core.network.NetworkService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StaffChatService : KoinComponent {

    private val networkService: NetworkService by inject()

    fun message(sender: Player, message: String) {
        networkService.publish(PacketStaffChat(sender.uniqueId, message))
    }
}
