package me.smp.core.warp

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Async
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object WarpCommands : KoinComponent {

    private val warpService: WarpService by inject()

    @Command("warp create")
    @Async
    @Permission("core.warp.create")
    @Description("Create a warp")
    fun create(
        @Sender sender: CommandSender,
        @Name("name") name: String,
    ) {
        warpService.add(Warp {
            this.name = name
        })
        sender.sendMessage("Successfully created $name warp.")
    }

    @Command("warp location")
    @Async
    @Permission("core.warp.location")
    @Description("Set location of a warp")
    fun location(
        @Sender sender: Player,
        @Name("warp") warp: Warp,
    ) {
        warp.location = sender.location
        warpService.update(warp)
        sender.sendMessage("Successfully set location of ${warp.name} warp.")
    }

    @Command("warp permission")
    @Async
    @Permission("core.warp.location")
    @Description("Set permission required to use a warp")
    fun permission(
        @Sender sender: CommandSender,
        @Name("warp") warp: Warp,
        @Name("permission") permission: String
    ) {
        warp.permission = permission
        warpService.update(warp)
        sender.sendMessage("Successfully set required permission of ${warp.name} warp.")
    }

    @Command("warp remove")
    @Async
    @Permission("core.warp.remove")
    @Description("Remove a warp")
    fun remove(
        @Sender sender: CommandSender,
        @Name("warp") warp: Warp,
    ) {
        warpService.remove(warp)
        sender.sendMessage("Successfully removed ${warp.name} warp.")
    }

    @Command("warp")
    @Permission
    @Description("Teleport to a warp")
    fun warp(@Sender sender: Player, @Name("warp") warp: Warp) {
        warpService.teleport(sender, warp)
    }
}