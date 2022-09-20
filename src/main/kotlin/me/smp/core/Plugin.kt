package me.smp.core


import me.smp.core.chat.ChatCommands
import me.smp.core.chat.ChatListener
import me.smp.core.chat.ChatState
import me.smp.core.chat.ChatStateArgumentProvider
import me.smp.core.pm.PrivateMessageCommands
import me.smp.core.punishment.PunishmentCommands
import me.smp.core.punishment.PunishmentListener
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.*
import me.vaperion.blade.Blade
import me.vaperion.blade.bukkit.BladeBukkitPlatform
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import java.util.logging.Level

class Plugin : JavaPlugin() {

    companion object {
        val koinApp = startKoin {
            modules(MODULE)
        }

        lateinit var blade: Blade
    }

    override fun onEnable() {
        logger.log(Level.INFO, "siema eniu")

        System.setProperty(
            "org.litote.mongo.test.mapping.service", "org.litote.kmongo.pojo.PojoClassMappingTypeService"
        )

        server.pluginManager.registerEvents(RankListener(), this)
        server.pluginManager.registerEvents(PunishmentListener(), this)
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(CacheListener(), this)

        blade = Blade.forPlatform(BladeBukkitPlatform(this)).bind {
            it.bind(ChatState::class.java, ChatStateArgumentProvider)
            it.bind(PlayerMetadata::class.java, PlayerMetadataArgumentProvider)
            it.bind(Rank::class.java, RankArgumentProvider)
            it.bind(Duration::class.java, DurationArgumentProvider)
        }.config {
            it.fallbackPrefix = "core"
            it.isOverrideCommands = true
        }.build()

        blade.register(PunishmentCommands)
        blade.register(RankCommands)
        blade.register(ChatCommands)
        blade.register(PrivateMessageCommands)
    }

    override fun onDisable() {
        val punishmentRepository: PunishmentRepository = koinApp.koin.get()
        val rankRepository: RankRepository = koinApp.koin.get()
        punishmentRepository.flushCache()
        rankRepository.flushCache()
    }
}
