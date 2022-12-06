package me.smp.core

import com.comphenix.protocol.ProtocolLibrary
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.TitlePart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class VersionNoticeListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        println("VERSION ${ProtocolLibrary.getProtocolManager().getProtocolVersion(player)}")
        if (ProtocolLibrary.getProtocolManager().getProtocolVersion(player) < 755) {
            player.sendTitlePart(
                TitlePart.TITLE,
                Component.text("You are running a minecraft version older than 1.17!", NamedTextColor.DARK_RED)
            )
            player.sendTitlePart(
                TitlePart.SUBTITLE,
                Component.text(
                    "You may experience unexpected issues, we recommend using 1.17+ versions.",
                    NamedTextColor.RED
                )
            )
        }
    }
}