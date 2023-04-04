package gg.traphouse.core.chat

import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage

object ChatStateArgumentProvider : ArgumentProvider<ChatState> {

    override fun provide(ctx: Context, arg: Argument): ChatState? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        try {
            return ChatState.valueOf(name.uppercase())
        } catch (e: Exception) {
            throw BladeExitMessage("Chat state not found.")
        }
    }

    override fun suggest(context: Context, argument: Argument): List<String> {
        return ChatState.values().filter { it.name.startsWith(argument.string, true) }.map { it.name }.toList()
    }
}
