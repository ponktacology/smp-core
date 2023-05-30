package gg.traphouse.core.player

import gg.traphouse.core.Task
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerNotFoundInCacheException(val player: Player? = null) :
    IllegalStateException("${player?.name} player not online") {

    init {
        player?.let {
            if (Bukkit.isPrimaryThread()) {
                kick(it)
            } else Task.sync {
                kick(it)
            }
        }
    }

    private fun kick(player: Player) {
        player.kick(
            Component.text(
                "Wystąpił błąd w trakcie cachowania twoich danych.",
                NamedTextColor.RED
            )
        )
    }
}
