package gg.traphouse.core.chat

import gg.traphouse.shared.network.NetworkService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatService : KoinComponent {

    private val networkService: NetworkService by inject()

    private val chatSettings = ChatSettings()

    fun chatState() = chatSettings.state

    fun updateState(state: ChatState) {
        chatSettings.state = state

        when (state) {
            ChatState.DISABLED -> Bukkit.broadcast(Component.text("Chat is now disabled.", NamedTextColor.RED))
            ChatState.ENABLED -> Bukkit.broadcast(Component.text("Chat is now enabled.", NamedTextColor.GREEN))
            ChatState.DONATOR_ONLY -> Bukkit.broadcast(
                Component.text(
                    "Chat is now enabled only for donators.",
                    NamedTextColor.RED
                )
            )

            else -> {}
        }
    }

    fun broadcast(message: Component, raw: Boolean = false, global: Boolean = false) {
        val prefix = if (raw) Component.empty() else Component.empty().append(Component.text("[Alert] ", NamedTextColor.RED))
        if (global) {
            networkService.publish(PacketBroadcast(message))
            return
        }
        Bukkit.broadcast(prefix.append(message))
    }
}
