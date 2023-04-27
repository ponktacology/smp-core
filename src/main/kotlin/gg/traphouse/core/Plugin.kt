package gg.traphouse.core

import gg.traphouse.core.chat.ChatCommands
import gg.traphouse.core.chat.ChatListener
import gg.traphouse.core.chat.ChatState
import gg.traphouse.core.chat.ChatStateArgumentProvider
import gg.traphouse.core.cooldown.CooldownListener
import gg.traphouse.core.cooldown.CooldownRepository
import gg.traphouse.core.cooldown.CoreCooldowns
import gg.traphouse.core.invsee.InvSeeCommands
import gg.traphouse.core.nametag.CoreNameTagProvider
import gg.traphouse.core.nametag.NameTagHandler
import gg.traphouse.core.player.PlayerCommands
import gg.traphouse.core.player.PlayerContainer
import gg.traphouse.core.player.PlayerContainerArgumentProvider
import gg.traphouse.core.pm.PrivateMessageCommands
import gg.traphouse.core.protection.ProtectionCommands
import gg.traphouse.core.protection.WorldArgumentProvider
import gg.traphouse.core.punishment.PunishmentCommands
import gg.traphouse.core.punishment.PunishmentListener
import gg.traphouse.core.punishment.PunishmentRepository
import gg.traphouse.core.rank.*
import gg.traphouse.core.scoreboard.ScoreboardService
import gg.traphouse.core.staff.StaffCommands
import gg.traphouse.core.staff.StaffSettingsListener
import gg.traphouse.core.staff.StaffSettingsRepository
import gg.traphouse.core.staff.VanishDisplayTask
import gg.traphouse.core.staff.assistance.AssistanceCommands
import gg.traphouse.core.staff.assistance.AssistanceListener
import gg.traphouse.core.staff.chat.StaffChatCommands
import gg.traphouse.core.staff.chat.StaffChatListener
import gg.traphouse.core.staff.freeze.FreezeCommands
import gg.traphouse.core.staff.freeze.FreezeListener
import gg.traphouse.core.staff.freeze.FreezeRepository
import gg.traphouse.shared.Duration
import gg.traphouse.shared.network.NetworkRepository
import me.vaperion.blade.Blade
import me.vaperion.blade.bukkit.BladeBukkitPlatform
import me.vaperion.blade.platform.TabCompleter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
        val chatListener = ChatListener()

        server.pluginManager.registerEvents(rankListener, this)
        server.pluginManager.registerEvents(punishmentListener, this)
        server.pluginManager.registerEvents(chatListener, this)
        server.pluginManager.registerEvents(CacheListener(), this)
        server.pluginManager.registerEvents(BenchmarkListener(), this)
        server.pluginManager.registerEvents(CooldownListener(), this)
        server.pluginManager.registerEvents(freezeListener, this)
        server.pluginManager.registerEvents(StaffSettingsListener(), this)

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
        networkRepository.registerListener(chatListener)
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

        NameTagHandler.init(this)
        NameTagHandler.registerProvider(CoreNameTagProvider())

        TaskDispatcher.runRepeatingAsync(VanishDisplayTask(), 10L)

        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            it.kick(
                Component.text("Server is restarting!", NamedTextColor.RED)
                    .append(Component.newline())
                    .append(Component.text("Please rejoin in a few minutes.", NamedTextColor.YELLOW))
            )
        }
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
