package me.smp.core.rank

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

enum class Rank(
    val displayName: String, val power: Int, val color: TextColor, vararg val decorations: TextDecoration
) {

    CONSOLE("Console", 9999, NamedTextColor.DARK_RED, TextDecoration.ITALIC),
    HEAD_ADMIN(
        "Head-Admin",
        999,
        NamedTextColor.RED,
        TextDecoration.ITALIC
    ),
    ADMIN("Admin", 990, NamedTextColor.RED),
    MODERATOR("Mod", 980, NamedTextColor.GREEN), HELPER(
        "Helper",
        970,
        NamedTextColor.BLUE,
        TextDecoration.ITALIC
    ),
    MEDIA("Media", 23, NamedTextColor.LIGHT_PURPLE),
    MVIP("MVIP", 13, NamedTextColor.BLUE), SVIP(
        "SVIP",
        12,
        NamedTextColor.GOLD
    ),
    VIP("VIP", 11, NamedTextColor.YELLOW),
    DEFAULT("Default", 0, NamedTextColor.GRAY);

    fun getPrefix() = if (this == DEFAULT) Component.empty() else Component.empty()
        .append(Component.text("${this.displayName} ", this.color, TextDecoration.BOLD))

    fun isSuperior(rank: Rank) = power > rank.power

    fun isStaff() = when (this) {
        MVIP, SVIP, VIP, DEFAULT -> false
        else -> true
    }

    fun isDonator() = when (this) {
        MVIP, SVIP, VIP -> true
        else -> false
    }
}