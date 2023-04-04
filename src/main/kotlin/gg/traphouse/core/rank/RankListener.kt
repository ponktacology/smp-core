package gg.traphouse.core.rank

import gg.traphouse.shared.network.NetworkHandler
import gg.traphouse.shared.network.NetworkListener
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RankListener : Listener, KoinComponent, NetworkListener {

    private val rankService: RankService by inject()
    private val rankRepository: RankRepository by inject()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChatMessage(event: AsyncChatEvent) {
        event.renderer(
            ChatRenderer.viewerUnaware { _, _, message ->
                Component.empty()
                    .append(
                        rankService.getFullDisplayName(event.player).hoverEvent(
                            HoverEvent.hoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.text("Click to send a private message to ${event.player.name}", NamedTextColor.YELLOW)
                            )
                        )
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg ${event.player.name} "))
                    )
                    .append(Component.text(": ", NamedTextColor.WHITE).append(message))
            }
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        rankRepository.recalculatePermissions(player)
        player.playerListName(Component.empty().append(rankService.getFullDisplayName(player)))
    }

    @NetworkHandler
    fun onGrant(packet: PacketGrant) {
        rankRepository.grant(packet.player, packet.grantId)
    }

    @NetworkHandler
    fun onUngrant(packet: PacketUngrant) {
        rankRepository.unGrant(packet.player, packet.rank, packet.issuer, packet.reason)
    }
}
