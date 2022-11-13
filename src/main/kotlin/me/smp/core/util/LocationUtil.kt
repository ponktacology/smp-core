package me.smp.core.util

import org.bukkit.Location

object LocationUtil {
    fun hasChanged(from: Location, to: Location): Boolean {
        return from.world != to.world || from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ
    }
}
