package gg.traphouse.core.staff

import gg.traphouse.core.ComponentHelper
import gg.traphouse.core.util.SenderUtil.sendError
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffCommands : KoinComponent {

    private val staffService: StaffService by inject()

    @Command("vanish", "v")
    @Permission("core.vanish")
    @Description("Toggles hidden mode")
    fun vanish(@Sender sender: Player, @Optional player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.vanish.other")) {
                sender.sendError("No permission.")
                return
            }

            val vanish = staffService.toggleVanish(it)
            sender.sendMessage(ComponentHelper.createBoolean("${it.name} is", "hidden.", vanish))
            it.sendMessage(ComponentHelper.createBoolean("You are", "hidden. Toggled by ${sender.name}.", vanish))
            return
        }
        val vanish = staffService.toggleVanish(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "hidden.", vanish))
    }

    @Command("god", "g")
    @Permission("core.god")
    @Description("Toggles god mode")
    fun god(@Sender sender: Player, @Optional player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.god.other")) {
                sender.sendError("No permission.")
                return
            }

            val god = staffService.toggleGod(it)
            sender.sendMessage(ComponentHelper.createBoolean("${it.name} is", "invincible.", god))
            it.sendMessage(ComponentHelper.createBoolean("You are", "invincible. Toggled by ${sender.name}.", god))
            return
        }

        val god = staffService.toggleGod(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "invincible.", god))
    }

    @Command("fly", "f")
    @Permission("core.fly")
    @Description("Toggles flight mode")
    fun fly(@Sender sender: Player, @Optional player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.fly.other")) {
                sender.sendError("No permission.")
                return
            }

            val fly = staffService.toggleFly(it)
            sender.sendMessage(ComponentHelper.createBoolean("${it.name} is", "flying.", fly))
            it.sendMessage(ComponentHelper.createBoolean("You are", "flying. Toggled by ${sender.name}.", fly))
            return
        }

        val fly = staffService.toggleFly(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "flying.", fly))
    }
}
