package me.smp.core.invsee

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object InvSeeCommands : KoinComponent {

    private val invSeeService: InvSeeService by inject()

    @Command("invsee")
    @Description("View and edit player's inventory")
    @Permission("core.invsee")
    fun invSee(@Sender sender: Player, @Name("player") target: Player) {
        invSeeService.showInventory(sender, target)
    }
}