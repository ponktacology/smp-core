package me.smp.core

import me.smp.core.name.NameService
import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PlayerMetadataArgumentProvider : ArgumentProvider<PlayerMetadata>, KoinComponent {

    private val nameService: NameService by inject()

    override fun provide(ctx: Context, arg: Argument): PlayerMetadata? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        //TODO: Fetch from db
        nameService.getByName(name)?.let {
            return PlayerMetadata(it, name)
        }

        throw BladeExitMessage("Player not found.")
    }

    override fun suggest(context: Context, argument: Argument): List<String> {
        return Bukkit.getOnlinePlayers().filter { it.name.startsWith(argument.string, true) }.map { it.name }.toList()
    }
}