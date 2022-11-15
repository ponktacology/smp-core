package me.smp.core

import me.smp.shared.Duration
import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage

object DurationArgumentProvider : ArgumentProvider<Duration> {

    override fun provide(ctx: Context, arg: Argument): Duration? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null

        try {
            return Duration(if ("perm".equals(name, true)) -1 else java.time.Duration.parse("PT$name").toMillis())
        } catch (e: Exception) {
            throw BladeExitMessage("Invalid duration format.")
        }
    }
}
