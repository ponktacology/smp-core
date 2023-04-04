package gg.traphouse.core.protection

import me.vaperion.blade.argument.Argument
import me.vaperion.blade.argument.ArgumentProvider
import me.vaperion.blade.context.Context
import me.vaperion.blade.exception.BladeExitMessage
import org.bukkit.Bukkit
import org.bukkit.World

object WorldArgumentProvider : ArgumentProvider<World> {

    override fun provide(ctx: Context, arg: Argument): World? {
        val name = arg.string
        if (name.isNullOrEmpty()) return null
        return Bukkit.getWorld(name) ?: throw BladeExitMessage("Invalid duration format.")
    }
}
