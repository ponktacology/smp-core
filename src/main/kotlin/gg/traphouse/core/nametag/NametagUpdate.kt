package gg.traphouse.core.nametag

import org.bukkit.entity.Player
import java.util.*

internal class NametagUpdate(val toRefresh: UUID, var refreshFor: UUID? = null) {

    constructor(toRefresh: Player) : this(toRefresh.uniqueId)

    constructor(toRefresh: Player, refreshFor: Player) : this(toRefresh.uniqueId, refreshFor.uniqueId)

}
