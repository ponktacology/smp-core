package me.smp.core.staff.freeze

import dev.triumphteam.gui.components.InteractionModifier
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import me.smp.core.ItemBuilder
import me.smp.core.TaskDispatcher
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FreezeGUI :
    Gui(
        3,
        "Freeze",
        setOf(
            InteractionModifier.PREVENT_ITEM_PLACE,
            InteractionModifier.PREVENT_ITEM_TAKE,
            InteractionModifier.PREVENT_ITEM_SWAP,
            InteractionModifier.PREVENT_ITEM_DROP,
            InteractionModifier.PREVENT_OTHER_ACTIONS
        )
    ),
    KoinComponent {

    private val freezeService: FreezeService by inject()

    init {
        setItem(
            13,
            GuiItem(
                ItemBuilder(Material.BOOK)
                    .name(Component.text("YOU ARE FROZEN!", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .lore(
                        Component.empty(),
                        Component.text("You got frozen by a staff player.", NamedTextColor.GRAY),
                        Component.text("Please join our discord server dc.traphouse.gg", NamedTextColor.GRAY),
                        Component.text("And join the #i-got-frozen channel.", NamedTextColor.GRAY),
                        Component.text("Disconnecting will result in a permanent ban.", NamedTextColor.GRAY),
                        Component.text("You have 5 minutes to connect.", NamedTextColor.RED)
                    )
                    .build()
            )
        )
        setCloseGuiAction { event ->
            val player = event.player as Player
            if (freezeService.isFrozen(player)) {
                TaskDispatcher.dispatchLater({ open(player) }, 1L)
            }
        }
    }
}
