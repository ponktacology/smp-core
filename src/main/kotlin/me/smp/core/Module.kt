package me.smp.core


import me.smp.core.chat.ChatService
import me.smp.core.name.NameService
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.punishment.PunishmentService
import me.smp.core.rank.RankRepository
import me.smp.core.rank.RankService
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
    single { RankService() }
    single { RankRepository() }
    single { ChatService() }
    single { PunishmentService() }
    single { PunishmentRepository() }
    single { NameService() }
}