package gg.traphouse.core.nametag

import org.bukkit.entity.Player

abstract class NametagProvider(val name: String, val weight: Int) {
    abstract fun modifyNameTag(previous: NameTagInfo, viewer: Player, viewed: Player)
}
