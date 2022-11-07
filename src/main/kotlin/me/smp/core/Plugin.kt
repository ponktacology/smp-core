package me.smp.core


import me.smp.core.assistance.AssistanceCommands
import me.smp.core.assistance.AssistanceListener
import me.smp.core.chat.ChatCommands
import me.smp.core.chat.ChatListener
import me.smp.core.chat.ChatState
import me.smp.core.chat.ChatStateArgumentProvider
import me.smp.core.chat.staff.StaffChatCommands
import me.smp.core.chat.staff.StaffChatListener
import me.smp.core.cooldown.CooldownListener
import me.smp.core.cooldown.CooldownRepository
import me.smp.core.cooldown.Cooldowns
import me.smp.core.invsee.InvSeeCommands
import me.smp.core.nametag.NameTagListener
import me.smp.core.network.NetworkRepository
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

        val punishmentListener = PunishmentListener()
        val rankListener = RankListener()

        server.pluginManager.registerEvents(rankListener, this)
        server.pluginManager.registerEvents(punishmentListener, this)
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(CacheListener(), this)
        server.pluginManager.registerEvents(BenchmarkListener(), this)
        server.pluginManager.registerEvents(NameTagListener(), this)
        server.pluginManager.registerEvents(CooldownListener(), this)

        blade = Blade.forPlatform(BladeBukkitPlatform(this)).bind {
            it.bind(ChatState::class.java, ChatStateArgumentProvider)
            it.bind(PlayerContainer::class.java, PlayerContainerArgumentProvider)
            it.bind(Rank::class.java, RankArgumentProvider)
            it.bind(Duration::class.java, DurationArgumentProvider)
        }.config {
            it.fallbackPrefix = "core"
            it.isOverrideCommands = true
        }.build()

        val networkRepository: NetworkRepository = koinApp.koin.get()
        networkRepository.registerListener(StaffChatListener())
        networkRepository.registerListener(AssistanceListener())
        networkRepository.registerListener(punishmentListener)
        networkRepository.registerListener(rankListener)
        networkRepository.startListening()

        val cooldownRepository: CooldownRepository = koinApp.koin.get()
        Cooldowns.values().forEach { cooldownRepository.registerPersistentCooldown(it) }

        blade.register(PunishmentCommands)
        blade.register(RankCommands)
        blade.register(ChatCommands)
        blade.register(PrivateMessageCommands)
        blade.register(StaffChatCommands)
        blade.register(AssistanceCommands)
        blade.register(InvSeeCommands)

    }

    override fun onDisable() {
        val punishmentRepository: PunishmentRepository = koinApp.koin.get()
        val rankRepository: RankRepository = koinApp.koin.get()
        val cooldownRepository: CooldownRepository = koinApp.koin.get()
        cooldownRepository.flushCache()
        punishmentRepository.flushCache()
        rankRepository.flushCache()
    }
}
