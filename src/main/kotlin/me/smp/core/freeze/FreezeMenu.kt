package me.smp.core.freeze

import dev.triumphteam.gui.components.InteractionModifier
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import me.smp.core.TaskDispatcher
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FreezeMenu :
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
        setItem(13, GuiItem(ItemStack(Material.BOOK)))

        setCloseGuiAction { event ->
            val player = event.player as Player
            if (freezeService.isFrozen(player)) {
                TaskDispatcher.dispatchLater({ open(player) }, 1L)
            }
        }
    }
}
