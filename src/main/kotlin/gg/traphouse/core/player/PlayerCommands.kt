package gg.traphouse.core.player

import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

object PlayerCommands {

    @Command("ping")
    @Description("Check player's ping")
    fun ping(@Sender sender: Player, @Optional("self") target: Player) {
        sender.sendMessage(
            Component.text("${target.name}'s ping: ", NamedTextColor.YELLOW)
                .append(Component.text(target.ping, NamedTextColor.GRAY))
        )
    }
}