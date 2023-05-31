package gg.traphouse.core.staff

import gg.traphouse.core.ComponentHelper.sendStateComponent
import gg.traphouse.core.util.SenderUtil.sendNoPermission
import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.ParseQuotes
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object StaffCommands : KoinComponent {

    private val staffService: StaffService by inject()

    @Command("sudo")
    @Permission("core.sudo")
    @ParseQuotes
    fun sudo(
        @Sender sender: CommandSender,
        @Text @Name("command") command: String,
        @Optional @Name("player") player: Player?
    ) {
        player?.let {
            Bukkit.dispatchCommand(it, command)
            return
        }

        Bukkit.getOnlinePlayers().forEach { Bukkit.dispatchCommand(it, command) }
    }

    @Command("chat")
    @Permission("core.chat")
    @ParseQuotes
    fun chat(
        @Sender sender: CommandSender,
        @Text @Name("message") command: String,
        @Optional @Name("player") player: Player?
    ) {
        player?.let {
            it.chat(command)
            return
        }

        Bukkit.getOnlinePlayers().forEach { it.chat(command) }
    }


    @Command("vanish", "v")
    @Permission("core.vanish")
    @Description("Zmienia tryb vanish")
    fun vanish(@Sender sender: Player, @Optional player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.vanish.other")) {
                sender.sendNoPermission()
                return
            }

            val vanish = staffService.toggleVanish(it)
            sender.sendStateComponent("Tryb vanish gracza ${it.name} został %s.", vanish)
            it.sendStateComponent("Tryb vanish został %s przez gracza ${sender.name}.", vanish)
            return
        }

        val vanish = staffService.toggleVanish(sender)
        sender.sendStateComponent("Tryb vanish został %s.", vanish)
    }

    @Command("god", "g")
    @Permission("core.god")
    @Description("Zmienia tryb god")
    fun god(@Sender sender: Player, @Optional @Name("player") player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.god.other")) {
                sender.sendNoPermission()
                return
            }

            val god = staffService.toggleGod(it)
            sender.sendStateComponent("Tryb god gracza ${it.name} został %s.", god)
            it.sendStateComponent("Tryb god został %s przez gracza ${sender.name}.", god)
            return
        }

        val god = staffService.toggleGod(sender)
        sender.sendStateComponent("Tryb god został %s.", god)
    }

    @Command("fly", "f")
    @Permission("core.fly")
    @Description("Zmienia tryb file")
    fun fly(@Sender sender: Player, @Optional @Name("player") player: Player?) {
        player?.let {
            if (!sender.hasPermission("core.fly.other")) {
                sender.sendNoPermission()
                return
            }

            val fly = staffService.toggleFly(it)
            sender.sendStateComponent("Tryb latania gracza ${it.name} został %s.", fly)
            it.sendStateComponent("Tryb latania został %s przez gracza ${sender.name}.", fly)
            return
        }

        val fly = staffService.toggleFly(sender)
        sender.sendStateComponent("Tryb latania został %s.", fly)
    }
}
