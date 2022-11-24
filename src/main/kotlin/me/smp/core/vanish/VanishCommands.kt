package me.smp.core.vanish

import me.smp.core.ComponentHelper
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object VanishCommands : KoinComponent {

    private val vanishService: VanishService by inject()

    @Command(value = ["vanish", "v"])
    @Permission("core.vanish")
    @Async
    fun vanish(@Sender sender: Player) {
        val vanishSettings = vanishService.getByOnlinePlayer(sender)
        vanishService.updateVanish(sender, !vanishSettings.enabled)
        sender.sendMessage(ComponentHelper.createBoolean("You are", "hidden.", vanishSettings.enabled))
    }
}
