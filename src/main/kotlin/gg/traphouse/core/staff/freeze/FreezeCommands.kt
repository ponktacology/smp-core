package gg.traphouse.core.staff.freeze

import gg.traphouse.core.Console
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FreezeCommands : KoinComponent {

    private val freezeService: FreezeService by inject()

    @Command("freeze")
    @Description("Freeze a player")
    @Permission("core.freeze")
    fun freeze(@Sender sender: CommandSender, @Name("player") player: Player) {
        freezeService.freeze(if (sender is Player) sender.uniqueId else Console.UUID, player)
    }

    @Command("unfreeze")
    @Description("Unfreeze a player")
    @Permission("core.unfreeze")
    fun unfreeze(@Sender sender: CommandSender, @Name("player") player: Player) {
        freezeService.unFreeze(player)
    }
}
