package gg.traphouse.core.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

class ChatService {

    private val chatSettings = ChatSettings()

    fun chatState() = chatSettings.state

    fun updateState(state: ChatState) {
        chatSettings.state = state

        when (state) {
            ChatState.DISABLED -> Bukkit.broadcast(Component.text("Czat został wyłączony.", NamedTextColor.RED))
            ChatState.ENABLED -> Bukkit.broadcast(Component.text("Czat został włączony.", NamedTextColor.GREEN))
            ChatState.DONATOR_ONLY -> Bukkit.broadcast(
                Component.text(
                    "Czat został włączony tylko dla graczy z rangą.",
                    NamedTextColor.RED
                )
            )
        }
    }

    fun broadcast(message: Component, raw: Boolean = false) {
        val prefix = if (raw) Component.empty() else Component.empty().append(Component.text("[INFO] ", NamedTextColor.RED))
        Bukkit.broadcast(prefix.append(message))
    }
}
