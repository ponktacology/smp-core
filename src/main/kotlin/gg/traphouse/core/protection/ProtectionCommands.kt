package gg.traphouse.core.protection

import me.vaperion.blade.annotation.argument.Name
import me.vaperion.blade.annotation.argument.Optional
import me.vaperion.blade.annotation.argument.Sender
import me.vaperion.blade.annotation.argument.Text
import me.vaperion.blade.annotation.command.Command
import me.vaperion.blade.annotation.command.Description
import me.vaperion.blade.annotation.command.Permission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ProtectionCommands : KoinComponent {

    private val protectionService: ProtectionService by inject()

    @Command("entitiesperchunk")
    @Description("Show chunks with most entities")
    @Permission("core.entitesperchunk")
    fun chunkEntityCount(
        @Sender sender: CommandSender,
        @Name("world") @Optional("world")
        world: World,
        @Name("count") @Optional("10")
        count: Int
    ) {
        protectionService.getChunksByEntityCount(world, count).forEachIndexed { index, mutableEntry ->
            sender.sendMessage("#${index + 1} ${mutableEntry.key.x shl 4} ${mutableEntry.key.z shl 4} ${mutableEntry.value.size}")
        }
    }

    @Command("testperm")
    @Permission("core.testperm")
    fun testperm(@Sender sender: Player, @Name("target") target: Player, @Name("perm") perm: String) {
        sender.sendMessage(Component.text(target.hasPermission(perm)))
    }

    @Command("testcolor")
    @Permission("test.color")
    fun color(@Sender sender: CommandSender, @Text @Name("message") message: String) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message, TagResolver.standard()))
    }
}
