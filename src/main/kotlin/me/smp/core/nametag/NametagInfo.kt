package me.smp.core.nametag

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor

class NametagInfo(val name: String, val prefix: Component, val suffix: Component, val color: ChatColor) {
    val teamAddPacket: ScoreboardTeamPacketMod

    init {
        teamAddPacket = ScoreboardTeamPacketMod(name, prefix, suffix, color, ArrayList(), 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NametagInfo

        if (name != other.name) return false
        if (prefix != other.prefix) return false
        if (suffix != other.suffix) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + prefix.hashCode()
        result = 31 * result + suffix.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}
