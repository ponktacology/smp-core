package me.smp.core

import dev.triumphteam.gui.guis.GuiItem
import net.kyori.adventure.text.Component
import org.bukkit.Material

object GUIUtil {
    val FILLER_ITEM = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).build())
}