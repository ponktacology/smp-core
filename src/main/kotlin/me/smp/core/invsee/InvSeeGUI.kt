package me.smp.core.invsee

import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class InvSeeGUI(player: Player) : Gui(5, "${player.name}'s inventory", emptySet()) {

    init {
        player.inventory.contents.forEach {
            addItem(GuiItem(it ?: ItemStack(Material.AIR)))
        }

        setCloseGuiAction { event ->
            val viewer = event.player
            if (!viewer.hasPermission("core.invsee.edit")) return@setCloseGuiAction
            if (!player.isOnline) {
                viewer.sendMessage("Player must be online in order to edit his inventory.")
                return@setCloseGuiAction
            }
            player.inventory.setContents(this.inventory.contents.map { it ?: ItemStack(Material.AIR) }.toTypedArray())
        }
    }
}
