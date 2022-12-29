package me.smp.core.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class ChatService {

    private val chatSettings = ChatSettings()

    fun chatState() = chatSettings.state

    fun updateState(state: ChatState) {
        chatSettings.state = state

        when (state) {
            ChatState.DISABLED -> Bukkit.broadcast(Component.text("Chat is now disabled.", NamedTextColor.RED))
            ChatState.DONATOR_ONLY -> Bukkit.broadcast(
                Component.text(
                    "Chat is now enabled only for donators.",
                    NamedTextColor.RED
                )
            )

            else -> {}
        }
    }

    fun broadcast(message: Component, raw: Boolean = false) {
        val prefix = if (raw) Component.empty() else Component.empty().append(Component.text("[Alert] ", NamedTextColor.RED))
        Bukkit.broadcast(prefix.append(message))
    }
}
