package me.smp.core.rank

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor

enum class Rank(
    val displayName: String,
    val power: Int,
    val color: TextColor,
    val nameTagColor: ChatColor,
    vararg val decorations: TextDecoration
) {

    CONSOLE("Console", 9999, NamedTextColor.DARK_RED, ChatColor.DARK_RED, TextDecoration.ITALIC),
    HEAD_ADMIN(
        "Head-Admin",
        999,
        NamedTextColor.RED,
        ChatColor.RED,
        TextDecoration.ITALIC
    ),
    ADMIN("Admin", 990, NamedTextColor.RED, ChatColor.RED),
    MODERATOR("Mod", 980, NamedTextColor.GREEN, ChatColor.GREEN),
    HELPER(
        "Helper",
        970,
        NamedTextColor.BLUE,
        ChatColor.BLUE,
        TextDecoration.ITALIC
    ), // kingpin, pimp, trapper, dealer
    MEDIA("Media", 23, NamedTextColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE),
    KINGPIN("Kingpin", 13, NamedTextColor.GOLD, ChatColor.BLUE),
    PIMP(
        "Pimp",
        12,
        NamedTextColor.GOLD,
        ChatColor.GOLD
    ),
    TRAPPER("Trapper", 11, NamedTextColor.RED, ChatColor.RED),
    DEALER("Dealer", 10, NamedTextColor.GREEN, ChatColor.GREEN),
    DEFAULT("Default", 0, NamedTextColor.GRAY, ChatColor.WHITE);

    fun getPrefix() = if (this == DEFAULT) Component.empty() else Component.empty()
        .append(Component.text("${this.displayName} ", this.color, TextDecoration.BOLD))

    fun isSuperior(rank: Rank) = power > rank.power

    fun isStaff() = when (this) {
        KINGPIN, PIMP, DEALER, TRAPPER, DEFAULT -> false
        else -> true
    }

    fun isDonator() = when (this) {
        KINGPIN, PIMP, DEALER, TRAPPER, TRAPPER -> true
        else -> false
    }
}
