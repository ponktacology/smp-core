package gg.traphouse.core.util

import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import gg.traphouse.core.ItemBuilder
import gg.traphouse.core.TaskDispatcher
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object GUIUtil {

    val FILLER_ITEM = GuiItem(ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(Component.empty()).build())

    fun PaginatedGui.previousButton() =
        GuiItem(
            ItemBuilder(Material.PLAYER_HEAD).skullFromURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=")
                .name(Component.text("Back", NamedTextColor.GRAY)).build()
        ) { previous() }

    fun PaginatedGui.nextButton() = GuiItem(
        ItemBuilder(Material.PLAYER_HEAD).skullFromURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19")
            .name(Component.text("Next", NamedTextColor.GRAY)).build()
    ) { next() }

    fun title(title: String) = ChatColor.RED.toString() + ChatColor.BOLD + title

    fun Gui.openSync(player: Player) = TaskDispatcher.dispatch { this.open(player) }
}