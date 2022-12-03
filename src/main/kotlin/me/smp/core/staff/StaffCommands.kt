package me.smp.core.staff

import me.smp.core.ComponentHelper
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffCommands : KoinComponent {

    private val staffService: StaffService by inject()

    @Command(value = ["vanish", "v"])
    @Permission("core.vanish")
    @Async
    fun vanish(@Sender sender: Player) {
        val staffSettings = staffService.getByPlayer(sender)
        staffService.updateVanish(sender, !staffSettings.vanish)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "hidden.", staffSettings.vanish))
    }

    @Command("god")
    @Permission("core.god")
    @Async
    fun god(@Sender sender: Player) {
        val staffSettings = staffService.getByPlayer(sender)
        staffService.updateGod(sender, !staffSettings.god)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "invincible.", staffSettings.god))
    }

    @Command("fly")
    @Permission("core.fly")
    @Async
    fun fly(@Sender sender: Player) {
        val staffSettings = staffService.getByPlayer(sender)
        staffService.updateFly(sender, !staffSettings.fly)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "flying.", staffSettings.fly))
    }
}
