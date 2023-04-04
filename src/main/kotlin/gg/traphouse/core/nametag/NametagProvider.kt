package gg.traphouse.core.nametag

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.entity.Player

abstract class NametagProvider(val name: String, val weight: Int) {
    abstract fun fetchNametag(var1: Player, var2: Player): NametagInfo

    protected class DefaultNametagProvider : NametagProvider("Default Provider", 0) {
        override fun fetchNametag(toRefresh: Player, refreshFor: Player): NametagInfo {
            return createNametag(Component.empty(), Component.empty(), ChatColor.WHITE)
        }
    }

    companion object {
        fun createNametag(prefix: Component, suffix: Component, color: ChatColor): NametagInfo {
            return FrozenNametagHandler.getOrCreate(prefix, suffix, color)
        }
    }
}
