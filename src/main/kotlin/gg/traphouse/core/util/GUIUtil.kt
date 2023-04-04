package gg.traphouse.core.util

import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import gg.traphouse.core.ItemBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Material

object GUIUtil {
    val FILLER_ITEM = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).build())

    fun PaginatedGui.previousButton() = GuiItem(ItemBuilder(Material.PAPER).name(Component.text("Previous")).build()) { previous() }
    fun PaginatedGui.nextButton() = GuiItem(ItemBuilder(Material.PAPER).name(Component.text("Next")).build()) { next() }
}