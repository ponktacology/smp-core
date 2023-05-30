package gg.traphouse.core.util

import org.bukkit.Location

object LocationUtil {
    fun hasChanged(from: Location, to: Location) = from.toBlockKey() != to.toBlockKey() //CHANGE
}
