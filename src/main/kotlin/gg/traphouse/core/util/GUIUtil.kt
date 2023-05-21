package gg.traphouse.core.util

import dev.triumphteam.gui.components.InteractionModifier
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import gg.traphouse.core.ItemBuilder
import gg.traphouse.core.TaskDispatcher
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object GUIUtil {

    val FILLER_ITEM = GuiItem(ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).build())

    val PREVENT_ALL_INTERACTION = setOf(
        InteractionModifier.PREVENT_ITEM_PLACE,
        InteractionModifier.PREVENT_ITEM_TAKE,
        InteractionModifier.PREVENT_ITEM_SWAP,
        InteractionModifier.PREVENT_ITEM_DROP,
        InteractionModifier.PREVENT_OTHER_ACTIONS
    )

    fun title(title: String) = ChatColor.RED.toString() + ChatColor.BOLD + title

    fun Gui.openSync(player: Player) = TaskDispatcher.dispatch { this.open(player) }
}