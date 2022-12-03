package me.smp.core.invsee

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.koin.core.component.KoinComponent

object InvSeeCommands : KoinComponent {

    @Command("invsee")
    @Description("View and edit player's inventory")
    @Permission("core.invsee")
    fun invSee(@Sender sender: Player, @Name("player") target: Player) {
        sender.openInventory(target.inventory)
    }

    @Command("enderchest")
    @Description("View and edit player's enderchest")
    @Permission("core.enderchest")
    fun enderchest(
        @Sender sender: Player,
        @Name("player") @Optional("self")
        target: Player
    ) {
        if (sender != target && !sender.hasPermission("core.enderchest.staff")) {
            sender.sendMessage(Component.text("You can only view your own enderchest.", NamedTextColor.RED))
            return
        }
        sender.openInventory(target.enderChest)
    }

    @Command(value = ["workbench", "crafting"])
    @Description("Open workbench")
    @Permission("core.workbench")
    fun workbench(@Sender sender: Player) {
        sender.openInventory(Bukkit.createInventory(sender, InventoryType.WORKBENCH))
    }
}
