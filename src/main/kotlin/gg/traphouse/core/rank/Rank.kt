package gg.traphouse.core.rank

import gg.traphouse.core.util.StringUtil
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
        ChatColor.GRAY
    ),
    OWNER(
        "OWNER",
        999,
        setOf("core.testperm"),
        emptySet<Rank>(),
        NamedTextColor.DARK_RED,
        ChatColor.GRAY
    ),
    ADMIN("ADMIN", 990, emptySet<String>(), emptySet<Rank>(), NamedTextColor.RED, ChatColor.GRAY),
    MODERATOR("MOD", 980, setOf("core.vanish"), emptySet<Rank>(), NamedTextColor.GREEN, ChatColor.GRAY),
    TRAINEE(
        "TRAINEE",
        970,
        emptySet<String>(),
        emptySet<Rank>(),
        NamedTextColor.BLUE,
        ChatColor.GRAY
    ), // kingpin, pimp, trapper, dealer
    MEDIA("MEDIA", 23, emptySet<String>(), emptySet<Rank>(), NamedTextColor.LIGHT_PURPLE, ChatColor.GRAY),
    KING("KING", 13, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GOLD, ChatColor.GRAY),
    PIMP("PIMP", 12, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GOLD, ChatColor.GRAY),
    OG("OG", 11, emptySet<String>(), emptySet<Rank>(), NamedTextColor.RED, ChatColor.GRAY),
    DEALER("DEALER", 10, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GREEN, ChatColor.GRAY),
    DEFAULT("DEFAULT", 0, emptySet<String>(), emptySet<Rank>(), NamedTextColor.GRAY, ChatColor.GRAY);

    val permissions: Set<String>
        get() {
            val allPermissions = mutableSetOf<String>()
            allPermissions.addAll(_permissions)
            inheritance.forEach { allPermissions.addAll(it.permissions) }
            return allPermissions
        }

    fun displayName() = Component.text(StringUtil.customFont("${this.displayName}"), this.color)

    fun getPrefix() = if (this == DEFAULT) Component.empty() else Component.empty()
        .append(displayName())

    fun isSuperior(rank: Rank) = power > rank.power

    fun isStaff() = when (this) {
        KING, PIMP, DEALER, OG, DEFAULT -> false
        else -> true
    }

    fun isDonator() = when (this) {
        KING, PIMP, DEALER, OG -> true
        else -> false
    }

}
