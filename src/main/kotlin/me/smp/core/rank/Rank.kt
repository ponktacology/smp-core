package me.smp.core.rank

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class Rank(val power: Int, val color: TextColor) {

    CONSOLE(9999, NamedTextColor.DARK_RED),
    HEAD_ADMIN(999, NamedTextColor.DARK_RED),
    ADMIN(990, NamedTextColor.RED),
    MODERATOR(980, NamedTextColor.GREEN),
    HELPER(970, NamedTextColor.BLUE),
    MVIP(13, NamedTextColor.LIGHT_PURPLE),
    SVIP(12, NamedTextColor.YELLOW),
    VIP(11, NamedTextColor.GOLD),
    DEFAULT(0, NamedTextColor.GRAY);

    fun isSuperior(rank: Rank) = power > rank.power

    fun isStaff() = when (this) {
        MVIP, SVIP, VIP, DEFAULT -> false
        else -> true
    }

    fun isDonator() = when (this) {
        MVIP, SVIP, VIP -> true
        else -> false
    }

    operator fun Rank.compareTo(rank: Rank): Int {
        return this.power.compareTo(rank.power)
    }
}