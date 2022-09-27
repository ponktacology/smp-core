package me.smp.core

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayerNotFoundInCacheException(val player: Player? = null) : IllegalStateException("player not online") {

    init {
        player?.let {
            if (Bukkit.isPrimaryThread()) {
                it.kick(Component.text("Couldn't load your profile data correctly."))
            } else TaskDispatcher.dispatch { it.kick(Component.text("Couldn't load your profile data correctly.")) }
        }
    }
}