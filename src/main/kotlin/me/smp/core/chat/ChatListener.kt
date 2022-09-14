package me.smp.core.chat

import io.papermc.paper.event.player.AsyncChatEvent
import me.smp.core.rank.RankService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatListener : Listener, KoinComponent {

    private val rankService: RankService by inject()
    private val chatService: ChatService by inject()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player
        val rank = rankService.getByOnlinePlayer(player)
        val cancelled = when (chatService.chatState()) {
            ChatState.DISABLED -> {
                if (!rank.isStaff()) {
                    player.sendMessage(Component.text("Chat is currently disabled.", NamedTextColor.RED))
                    true
                } else false
            }
            ChatState.DONATOR_ONLY -> {
                if (!(rank.isStaff() || rank.isDonator())) {
                    player.sendMessage(
                        Component.text(
                            "Chat is currently only available to donators.",
                            NamedTextColor.RED
                        )
                    )
                    true
                } else false
            }
            else -> false
        }
        event.isCancelled = cancelled
    }
}