package me.smp.core.rank

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor

enum class Rank(
    val displayName: String,
    val power: Int,
    val _permissions: Set<String>,
    val inheritance: Set<Rank>,
    val color: TextColor,
    val nameTagColor: ChatColor,
    vararg val decorations: TextDecoration
) : Comparable<Rank> {

    CONSOLE(
        "CONSOLE",
        9999,
        emptySet<String>(),
        emptySet<Rank>(),
        NamedTextColor.DARK_RED,
        ChatColor.DARK_RED,
        TextDecoration.ITALIC
    ),
    HEAD_ADMIN(
        "HEAD-ADMIN",
        999,
        emptySet<String>(),
        emptySet<Rank>(),
        NamedTextColor.RED,
        ChatColor.RED,
        TextDecoration.ITALIC
    ),
    ADMIN("ADMIN", 990, emptySet<String>(), emptySet<Rank>(), NamedTextColor.RED, ChatColor.RED),
    MODERATOR("MOD", 980, setOf("core.vanish"), emptySet<Rank>(), NamedTextColor.GREEN, ChatColor.GREEN),
    TRAINEE(
        "TRAINEE",
        970,
        emptySet<String>(),
        emptySet<Rank>(),
        NamedTextColor.BLUE,
        ChatColor.BLUE,
        TextDecoration.ITALIC
    ), // kingpin, pimp, trapper, dealer
    MEDIA("MEDIA", 23, emptySet<String>(), emptySet<Rank>(), NamedTextColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE),
    KINGPIN("KINGPIN", 13, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GOLD, ChatColor.BLUE),
    PIMP(
        "PIMP",
        12,
        emptySet<String>(),
        emptySet<Rank>(),
        NamedTextColor.GOLD,
        ChatColor.GOLD
    ),
    TRAPPER("TRAPPER", 11, emptySet<String>(), emptySet<Rank>(), NamedTextColor.RED, ChatColor.RED),
    DEALER("DEALER", 10, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GREEN, ChatColor.GREEN),
    DEFAULT("DEFAULT", 0, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GRAY, ChatColor.WHITE);

    val permissions: Set<String>
        get() {
            val allPermissions = mutableSetOf<String>()
            allPermissions.addAll(_permissions)
            inheritance.forEach { allPermissions.addAll(it.permissions) }
            return allPermissions
        }

    fun getPrefix() = if (this == DEFAULT) Component.empty() else Component.empty()
        .append(Component.text("${this.displayName} ", this.color, TextDecoration.BOLD))

    fun isSuperior(rank: Rank) = power > rank.power

    fun isStaff() = when (this) {
        KINGPIN, PIMP, DEALER, TRAPPER, DEFAULT -> false
        else -> true
    }

    fun isDonator() = when (this) {
        KINGPIN, PIMP, DEALER, TRAPPER -> true
        else -> false
    }

}
