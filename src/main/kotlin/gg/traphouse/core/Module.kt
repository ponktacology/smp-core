package gg.traphouse.core

import gg.traphouse.core.chat.ChatFilter
import gg.traphouse.core.chat.ChatService
import gg.traphouse.core.cooldown.CooldownRepository
import gg.traphouse.core.cooldown.CooldownService
import gg.traphouse.core.player.PlayerLookupRepository
import gg.traphouse.core.player.PlayerLookupService
import gg.traphouse.core.pm.PrivateMessageRepository
import gg.traphouse.core.pm.PrivateMessageService
import gg.traphouse.core.protection.ProtectionService
import gg.traphouse.core.punishment.PunishmentRepository
import gg.traphouse.core.punishment.PunishmentService
import gg.traphouse.core.rank.RankRepository
import gg.traphouse.core.rank.RankService
import gg.traphouse.core.scoreboard.ScoreboardRepository
import gg.traphouse.core.scoreboard.ScoreboardService
import gg.traphouse.core.staff.StaffService
import gg.traphouse.core.staff.StaffSettingsRepository
import gg.traphouse.core.staff.assistance.AssistanceService
import gg.traphouse.core.staff.chat.StaffChatService
import gg.traphouse.core.staff.freeze.FreezeRepository
import gg.traphouse.core.staff.freeze.FreezeService
import gg.traphouse.shared.ConnectionProvider
import gg.traphouse.shared.network.NetworkRepository
import gg.traphouse.shared.network.NetworkService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module

val MODULE = module {
    single(null, true) {
        ConnectionProvider.database("127.0.0.1", 5432, "smp", "smp", "1234")
    }
    single(null, true) {
        ConnectionProvider.network("redis://127.0.0.1:6379")
    }
    single { JavaPlugin.getPlugin(Plugin::class.java) as Plugin }
    single { JavaPlugin.getPlugin(Plugin::class.java).config }
    single { Bukkit.getServer().logger }
    single { ChatService() }
    single { RankRepository() }
    single { RankService() }
    single { PunishmentRepository() }
    single { PunishmentService() }
    single { PlayerLookupRepository() }
    single { PlayerLookupService() }
    single { PrivateMessageRepository() }
    single { PrivateMessageService() }
    single { NetworkRepository() }
    single { NetworkService() }
    single { StaffChatService() }
    single { AssistanceService() }
    single { CooldownRepository() }
    single { CooldownService() }
    single { ScoreboardRepository() }
    single { ScoreboardService() }
    single { FreezeService() }
    single { FreezeRepository() }
    single { ChatFilter() }
    single { StaffSettingsRepository() }
    single { StaffService() }
    single { ProtectionService() }
}
