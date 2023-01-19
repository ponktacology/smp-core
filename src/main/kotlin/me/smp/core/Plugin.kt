package me.smp.core

import me.smp.core.chat.ChatCommands
import me.smp.core.chat.ChatListener
import me.smp.core.chat.ChatState
import me.smp.core.chat.ChatStateArgumentProvider
import me.smp.core.cooldown.CooldownListener
import me.smp.core.cooldown.CooldownRepository
import me.smp.core.cooldown.CoreCooldowns
import me.smp.core.invsee.InvSeeCommands
import me.smp.core.nametag.CoreNameTagProvider
import me.smp.core.nametag.FrozenNametagHandler
import me.smp.core.player.PlayerCommands
import me.smp.core.player.PlayerContainer
import me.smp.core.player.PlayerContainerArgumentProvider
import me.smp.core.pm.PrivateMessageCommands
import me.smp.core.protection.ProtectionCommands
import me.smp.core.protection.WorldArgumentProvider
import me.smp.core.punishment.PunishmentCommands
import me.smp.core.punishment.PunishmentListener
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.rank.*
import me.smp.core.scoreboard.ScoreboardService
import me.smp.core.staff.StaffCommands
import me.smp.core.staff.StaffSettingsListener
import me.smp.core.staff.StaffSettingsRepository
import me.smp.core.staff.VanishDisplayTask
import me.smp.core.staff.assistance.AssistanceCommands
import me.smp.core.staff.assistance.AssistanceListener
import me.smp.core.staff.chat.StaffChatCommands
import me.smp.core.staff.chat.StaffChatListener
import me.smp.core.staff.freeze.FreezeCommands
import me.smp.core.staff.freeze.FreezeListener
import me.smp.core.staff.freeze.FreezeRepository
import me.smp.shared.Duration
import me.smp.shared.network.NetworkRepository
import me.vaperion.blade.Blade
import me.vaperion.blade.bukkit.BladeBukkitPlatform
import me.vaperion.blade.platform.TabCompleter
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin

class Plugin : JavaPlugin() {

    companion object {
        val koinApp = startKoin {
            modules(MODULE)
        }

        lateinit var blade: Blade
    }

    override fun onEnable() {
        val punishmentListener = PunishmentListener()
        val rankListener = RankListener()
        val freezeListener = FreezeListener()

        server.pluginManager.registerEvents(rankListener, this)
        server.pluginManager.registerEvents(punishmentListener, this)
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(CacheListener(), this)
        server.pluginManager.registerEvents(BenchmarkListener(), this)
        server.pluginManager.registerEvents(CooldownListener(), this)
        server.pluginManager.registerEvents(freezeListener, this)
        server.pluginManager.registerEvents(StaffSettingsListener(), this)
        server.pluginManager.registerEvents(VersionNoticeListener(), this)

        blade = Blade.forPlatform(BladeBukkitPlatform(this)).bind {
            it.bind(ChatState::class.java, ChatStateArgumentProvider)
            it.bind(PlayerContainer::class.java, PlayerContainerArgumentProvider)
            it.bind(Rank::class.java, RankArgumentProvider)
            it.bind(Duration::class.java, DurationArgumentProvider)
            it.bind(World::class.java, WorldArgumentProvider)
        }.config {
            it.fallbackPrefix = "core"
            it.isOverrideCommands = true
            it.tabCompleter = TabCompleter.Default()
        }.build()

        val networkRepository: NetworkRepository = koinApp.koin.get()
        networkRepository.registerListener(StaffChatListener())
        networkRepository.registerListener(AssistanceListener())
        networkRepository.registerListener(punishmentListener)
        networkRepository.registerListener(rankListener)
        networkRepository.registerListener(freezeListener)
        networkRepository.startListening()

        val cooldownRepository: CooldownRepository = koinApp.koin.get()
        CoreCooldowns.values().forEach { cooldownRepository.registerPersistentCooldown(it) }

        val scoreboardService: ScoreboardService = koinApp.koin.get()
        scoreboardService.start()

        blade.register(PunishmentCommands)
        blade.register(RankCommands)
        blade.register(ChatCommands)
        blade.register(PrivateMessageCommands)
        blade.register(StaffChatCommands)
        blade.register(AssistanceCommands)
        blade.register(InvSeeCommands)
        blade.register(FreezeCommands)
        blade.register(StaffCommands)
        blade.register(ProtectionCommands)
        blade.register(PlayerCommands)

        FrozenNametagHandler.init(this)
        FrozenNametagHandler.registerProvider(CoreNameTagProvider())

        TaskDispatcher.runRepeatingAsync(VanishDisplayTask(), 10L)

        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        }
    }

    override fun onDisable() {
        val punishmentRepository: PunishmentRepository = koinApp.koin.get()
        val rankRepository: RankRepository = koinApp.koin.get()
        val cooldownRepository: CooldownRepository = koinApp.koin.get()
        val freezeRepository: FreezeRepository = koinApp.koin.get()
        val staffSettingsRepository: StaffSettingsRepository = koinApp.koin.get()
        cooldownRepository.flushCache()
        punishmentRepository.flushCache()
        rankRepository.flushCache()
        freezeRepository.flushCache()
        staffSettingsRepository.flushCache()
    }
}
