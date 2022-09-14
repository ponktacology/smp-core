package me.smp.core.rank

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RankListener : Listener, KoinComponent {

    private val rankService: RankService by inject()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChatMessage(event: AsyncChatEvent) {
        val rank = rankService.getByOnlinePlayer(event.player)

        event.renderer(ChatRenderer.viewerUnaware { _, sourceDisplayName, message ->
            Component.empty()
                .append(
                    Component.text(
                        if (rank == Rank.DEFAULT) "" else "${rank.name} ",
                        rank.color,
                        TextDecoration.BOLD
                    )
                )
                .append(sourceDisplayName.color(NamedTextColor.WHITE))
                .append(Component.text(": ", NamedTextColor.WHITE).append(message))
        })
    }
}