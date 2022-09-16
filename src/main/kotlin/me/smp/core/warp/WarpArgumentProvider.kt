package me.smp.core.warp

import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object WarpArgumentProvider : ArgumentProvider<Warp>, KoinComponent {

    private val warpService: WarpService by inject()

    override fun provide(ctx: Context, arg: Argument): Warp? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        return warpService.getByName(name) ?: throw BladeExitMessage("Rank not found.")
    }

    override fun suggest(context: Context, argument: Argument): List<String> {
        return warpService.getAll().filter { it.name.startsWith(argument.string, true) }.map { it.name }.toList()
    }
}