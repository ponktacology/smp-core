package me.smp.core

import me.smp.core.assistance.AssistanceService
import me.smp.core.chat.ChatService
import me.smp.core.chat.staff.StaffChatService
import me.smp.core.cooldown.CooldownRepository
import me.smp.core.cooldown.CooldownService
import me.smp.core.name.NameRepository
import me.smp.core.name.NameService
import me.smp.core.pm.PrivateMessageRepository
import me.smp.core.pm.PrivateMessageService
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.punishment.PunishmentService
import me.smp.core.rank.RankRepository
import me.smp.core.rank.RankService
import me.smp.core.scoreboard.ScoreboardRepository
import me.smp.core.scoreboard.ScoreboardService
import me.smp.shared.ConnectionProvider
import me.smp.shared.network.NetworkRepository
import me.smp.shared.network.NetworkService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module

val MODULE = module {
    single(null, true) {
        ConnectionProvider.database("jdbc:postgresql://localhost:54320/smp", "smp", "1234")
    }
    single(null, true) {
        ConnectionProvider.network("redis://localhost:6379")
    }
    single { JavaPlugin.getPlugin(Plugin::class.java) }
    single { Bukkit.getServer().logger }
    single { ChatService() }
    single { RankRepository() }
    single { RankService() }
    single { PunishmentRepository() }
    single { PunishmentService() }
    single { NameRepository() }
    single { NameService() }
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
}
