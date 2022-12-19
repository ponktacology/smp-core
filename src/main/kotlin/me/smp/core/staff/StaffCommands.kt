package me.smp.core.staff

import me.smp.core.ComponentHelper
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffCommands : KoinComponent {

    private val staffService: StaffService by inject()

    @Command(value = ["vanish", "v"])
    @Permission("core.vanish")
    fun vanish(@Sender sender: Player) {
        val vanish = staffService.toggleVanish(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "hidden.", vanish))
    }

    @Command("god")
    @Permission("core.god")
    fun god(@Sender sender: Player) {
        val god =staffService.toggleGod(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "invincible.", god))
    }

    @Command("fly")
    @Permission("core.fly")
    fun fly(@Sender sender: Player) {
        val fly = staffService.toggleFly(sender)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "flying.", fly))
    }
}
