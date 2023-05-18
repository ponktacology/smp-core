package gg.traphouse.core.nametag

import org.bukkit.entity.Player

abstract class NameTagProvider(val name: String, val weight: Int) {
    abstract fun modifyNameTag(previous: NameTagInfo, viewer: Player, viewed: Player)
}
