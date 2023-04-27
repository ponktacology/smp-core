package gg.traphouse.core.nametag

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor

data class NametagInfo(val prefix: Component, val suffix: Component, val color: ChatColor) {
    fun prefix(prefix: Component) = this.copy(prefix = prefix)
    fun suffix(suffix: Component) = this.copy(suffix = suffix)
    fun color(color: ChatColor) = this.copy(color = color)
}
