package gg.traphouse.core.nametag

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor

data class NameTagInfo(var namePrefix: String, var prefix: Component, var suffix: Component, var color: ChatColor)