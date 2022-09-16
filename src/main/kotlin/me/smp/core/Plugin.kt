package me.smp.core


import me.smp.core.chat.ChatCommands
import me.smp.core.chat.ChatListener
import me.smp.core.chat.ChatState
import me.smp.core.chat.ChatStateArgumentProvider
import me.smp.core.punishment.PunishmentCommands
import me.smp.core.punishment.PunishmentListener
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.*
import me.smp.core.teleport.TeleportListener
import me.smp.core.teleport.TeleportRepository
import me.smp.core.warp.Warp
import me.smp.core.warp.WarpArgumentProvider
import me.smp.core.warp.WarpCommands
import me.smp.core.warp.WarpRepository
import me.vaperion.blade.Blade
import me.vaperion.blade.bukkit.BladeBukkitPlatform
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import java.util.logging.Level

class Plugin : JavaPlugin() {

    private val koinApp = startKoin {
        modules(MODULE)
    }

    override fun onEnable() {
        logger.log(Level.INFO, "siema eniu")

        System.setProperty(
            "org.litote.mongo.test.mapping.service", "org.litote.kmongo.pojo.PojoClassMappingTypeService"
        )


        val warpRepository: WarpRepository = koinApp.koin.get()
        warpRepository.loadCache()

        server.pluginManager.registerEvents(RankListener(), this)
        server.pluginManager.registerEvents(PunishmentListener(), this)
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(CacheListener(), this)
        server.pluginManager.registerEvents(TeleportListener(), this)

        val blade = Blade.forPlatform(BladeBukkitPlatform(this)).bind {
            it.bind(ChatState::class.java, ChatStateArgumentProvider)
            it.bind(PlayerMetadata::class.java, PlayerMetadataArgumentProvider)
            it.bind(Rank::class.java, RankArgumentProvider)
            it.bind(Duration::class.java, DurationArgumentProvider)
            it.bind(Warp::class.java, WarpArgumentProvider)
        }.config {
            it.fallbackPrefix = "core"
        }.build()

        blade.register(PunishmentCommands)
        blade.register(RankCommands)
        blade.register(ChatCommands)
        blade.register(WarpCommands)
    }

    override fun onDisable() {
        val warpRepository: WarpRepository = koinApp.koin.get()
        val punishmentRepository: PunishmentRepository = koinApp.koin.get()
        val rankRepository: RankRepository = koinApp.koin.get()
        val teleportRepository: TeleportRepository = koinApp.koin.get()

        teleportRepository.flushCache()
        warpRepository.flushCache()
        punishmentRepository.flushCache()
        rankRepository.flushCache()
    }
}
