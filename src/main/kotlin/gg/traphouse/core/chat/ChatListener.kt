package gg.traphouse.core.chat

import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.StaffUtil
import gg.traphouse.shared.network.NetworkHandler
import gg.traphouse.shared.network.NetworkListener
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatListener : Listener, NetworkListener, KoinComponent {

    private val rankService: RankService by inject()
    private val chatService: ChatService by inject()
    private val chatFilter: ChatFilter by inject()

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player
        val rank = rankService.getByPlayer(player)

        if (rank.isStaff()) return
        if (when (chatService.chatState()) {
                ChatState.DISABLED -> {
                    player.sendMessage(Component.text("Chat is currently disabled.", NamedTextColor.RED))
                    true
                }

                ChatState.DONATOR_ONLY -> {
                    if (!rank.isDonator()) {
                        player.sendMessage(
                            Component.text(
                                "Chat is currently only available to donators.",
                                NamedTextColor.RED
                            )
                        )
                        true
                    }
                    false
                }

                else -> false
            }
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onChatEvent(event: AsyncChatEvent) {
        val player = event.player
        val rank = rankService.getByPlayer(player)
        val text = (event.message() as TextComponent).content()

        if (!rank.isStaff() && chatFilter.isFiltered(text)) {
            event.viewers().removeIf { it is Player && it != player }
            StaffUtil.messageStaff(
                Component
                    .empty()
                    .append(Component.text("[Filtered] ", NamedTextColor.RED))
                    .append(
                        event.renderer()
                            .render(event.player, event.player.displayName(), event.message(), event.player)
                    )
            )
        }
    }

    @NetworkHandler
    fun on(packet: PacketBroadcast) {
        Bukkit.broadcast(packet.message)
    }
}
