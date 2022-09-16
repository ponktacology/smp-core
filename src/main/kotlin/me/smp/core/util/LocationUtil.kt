package me.smp.core.util

import org.bukkit.Location

object LocationUtil {
    fun hasChanged(from: Location, to: Location): Boolean {
        return hasChanged(from, to, false)
    }

    fun hasChanged(from: Location, to: Location, ignoreY: Boolean): Boolean {
        return (from.world != to.world || from.blockX != to.blockX || !ignoreY) && from.blockY != to.blockY || from.blockZ != to.blockZ
    }
}