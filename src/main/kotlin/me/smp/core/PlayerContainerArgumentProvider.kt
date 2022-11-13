package me.smp.core

import me.smp.core.name.NameService
import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerContainerArgumentProvider : ArgumentProvider<PlayerContainer>, KoinComponent {

    private val nameService: NameService by inject()

    override fun provide(ctx: Context, arg: Argument): PlayerContainer? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        if (name.equals("me")) {
            if (ctx.sender().sender !is Player) {
                throw BladeExitMessage("Console must specify player.")
            }

            val player = ctx.sender().parseAs(Player::class.java)!!
            return PlayerContainer(player.uniqueId, player.name)
        }

        nameService.getByName(name)?.let {
            return PlayerContainer(it, name)
        }

        throw BladeExitMessage("Player not found.")
    }

    override fun suggest(context: Context, argument: Argument): List<String> {
        return Bukkit.getOnlinePlayers().filter { it.name.startsWith(argument.string, true) }.map { it.name }.toList()
    }
}
