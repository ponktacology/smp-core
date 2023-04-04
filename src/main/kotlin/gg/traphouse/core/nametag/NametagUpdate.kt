package gg.traphouse.core.nametag

import org.bukkit.entity.Player

internal class NametagUpdate {
    val toRefresh: String
    var refreshFor: String? = null
        private set

    constructor(toRefresh: Player) {
        this.toRefresh = toRefresh.name
    }

    constructor(toRefresh: Player, refreshFor: Player) {
        this.toRefresh = toRefresh.name
        this.refreshFor = refreshFor.name
    }
}
