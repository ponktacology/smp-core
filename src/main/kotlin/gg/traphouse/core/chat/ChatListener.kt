package gg.traphouse.core.chat

import gg.traphouse.core.rank.RankService
import gg.traphouse.core.util.StaffUtil
import gg.traphouse.core.util.StaffUtil.isStaff
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatListener : Listener, KoinComponent {

    private val rankService: RankService by inject()
    private val chatService: ChatService by inject()
    private val chatFilter: ChatFilter by inject()

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun on(event: AsyncChatEvent) {
        val player = event.player

        if (player.isStaff()) return
        if (when (chatService.chatState()) {
                ChatState.DISABLED -> {
                    player.sendMessage(Component.text("Czat jest aktualnie wyłączony.", NamedTextColor.RED))
                    true
                }

                ChatState.DONATOR_ONLY -> {
                    val rank = rankService.getByPlayer(player)
                    if (!rank.isDonator()) {
                        player.sendMessage(
                            Component.text(
                                "Czat jest aktualnie włączony tylko dla rang.",
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
        val text = (event.message() as TextComponent).content()

        if (!player.isStaff() && chatFilter.isFiltered(text)) {
            event.viewers().removeIf { it is Player && it != player }
            StaffUtil.messageStaff(
                Component
                    .empty()
                    .append(Component.text("[Filtr] ", NamedTextColor.RED))
                    .append(
                        event.renderer()
                            .render(event.player, event.player.displayName(), event.message(), event.player)
                    )
            )
        }
    }
}
