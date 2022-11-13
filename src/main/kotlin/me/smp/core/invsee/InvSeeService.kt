package me.smp.core.invsee

import org.bukkit.entity.Player

class InvSeeService {

    fun showInventory(viewer: Player, player: Player) = InvSeeGUI(player).open(viewer)
}
