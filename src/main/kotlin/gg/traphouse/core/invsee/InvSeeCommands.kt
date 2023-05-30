package gg.traphouse.core.invsee

import gg.traphouse.core.util.SenderUtil.sendNoPermission
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.koin.core.component.KoinComponent

object InvSeeCommands : KoinComponent {

    @Command("invsee")
    @Description("Otwiera ekwipunek gracza")
    @Permission("core.invsee")
    fun invSee(@Sender sender: Player, @Name("player") target: Player) {
        sender.openInventory(target.inventory)
    }

    @Command("enderchest", "ec")
    @Description("Otwiera enderchest gracza")
    @Permission("core.enderchest")
    fun enderchest(
        @Sender sender: Player,
        @Name("player") @Optional("self")
        target: Player
    ) {
        if (sender != target && !sender.hasPermission("core.enderchest.other")) {
            sender.sendNoPermission()
            return
        }
        sender.openInventory(target.enderChest)
    }

    @Command("workbench", "crafting", "craft", "wb")
    @Description("Otwiera workbench")
    @Permission("core.workbench")
    fun workbench(@Sender sender: Player) {
        sender.openInventory(Bukkit.createInventory(sender, InventoryType.WORKBENCH))
    }

    @Command("anvil")
    @Description("Otwiera kowadło")
    @Permission("core.anvil")
    fun anvil(@Sender sender: Player) {
        sender.openInventory(Bukkit.createInventory(sender, InventoryType.ANVIL))
    }
}
