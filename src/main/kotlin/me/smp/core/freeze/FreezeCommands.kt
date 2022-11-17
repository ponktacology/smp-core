package me.smp.core.freeze

import me.smp.core.Console
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FreezeCommands : KoinComponent {

    private val freezeService: FreezeService by inject()

    @Command("freeze")
    fun freeze(@Sender sender: CommandSender, @Name("player") player: Player) {
        freezeService.freeze(if (sender is Player) sender.uniqueId else Console.UUID, player)
    }

    @Command("unfreeze")
    fun unfreeze(@Sender sender: CommandSender, @Name("player") player: Player) {
        freezeService.unFreeze(player)
    }
}
