package me.smp.core.rank

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import me.smp.shared.network.NetworkHandler
import me.smp.shared.network.NetworkListener
import net.kyori.adventure.text.Component
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
                    .append(rankService.getFullDisplayName(event.player))
                    .append(Component.text(": ", NamedTextColor.WHITE).append(message))
            }
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        rankRepository.recalculatePermissions(event.player)
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
