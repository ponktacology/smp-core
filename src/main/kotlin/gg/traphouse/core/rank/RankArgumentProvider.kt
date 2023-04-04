package gg.traphouse.core.rank

import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage

object RankArgumentProvider : ArgumentProvider<Rank> {

    override fun provide(ctx: Context, arg: Argument): Rank? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        try {
            return Rank.valueOf(name.uppercase())
        } catch (e: Exception) {
            throw BladeExitMessage("Rank not found.")
        }
    }

    override fun suggest(context: Context, argument: Argument): List<String> {
        return Rank.values().filter { it.name.startsWith(argument.string, true) }.map { it.name }.toList()
    }
}
