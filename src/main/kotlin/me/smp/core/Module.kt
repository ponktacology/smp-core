package me.smp.core


import me.smp.core.chat.ChatService
import me.smp.core.name.NameService
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.punishment.PunishmentService
import me.smp.core.rank.RankRepository
import me.smp.core.rank.RankService
import me.smp.core.teleport.TeleportRepository
import me.smp.core.teleport.TeleportService
import me.smp.core.warp.WarpRepository
import me.smp.core.warp.WarpService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect


val MODULE = module {

    single(null, true) {
        Database.connect(
            url = "jdbc:postgresql://localhost:54320/smp",
            driver = "org.postgresql.Driver",
            user = "smp",
            password = "1234",
            dialect = PostgreSqlDialect()
        )
    }
    single { JavaPlugin.getPlugin(Plugin::class.java) }
    single { Bukkit.getServer().logger }
    single { ChatService() }
    single { RankRepository() }
    single { RankService() }
    single { PunishmentRepository() }
    single { PunishmentService() }
    single { NameService() }
    single { WarpRepository() }
    single { WarpService() }
    single { TeleportRepository() }
    single { TeleportService() }
}