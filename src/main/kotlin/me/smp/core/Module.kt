package me.smp.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.lettuce.core.RedisClient
import me.smp.core.assistance.AssistanceService
import me.smp.core.chat.ChatService
import me.smp.core.chat.staff.StaffChatService
import me.smp.core.cooldown.CooldownRepository
import me.smp.core.cooldown.CooldownService
import me.smp.core.name.NameRepository
import me.smp.core.name.NameService
import me.smp.core.network.NetworkRepository
import me.smp.core.network.NetworkService
import me.smp.core.pm.PrivateMessageRepository
import me.smp.core.pm.PrivateMessageService
import me.smp.core.punishment.PunishmentRepository
import me.smp.core.punishment.PunishmentService
import me.smp.core.rank.RankRepository
import me.smp.core.rank.RankService
import me.smp.core.scoreboard.ScoreboardRepository
import me.smp.core.scoreboard.ScoreboardService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.postgresql.PostgreSqlDialect

val MODULE = module {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:54320/smp"
        username = "smp"
        password = "1234"
        driverClassName = "org.postgresql.Driver"
        addDataSourceProperty("cachePrepStmts", "true")
        addDataSourceProperty("prepStmtCacheSize", "350")
        addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        addDataSourceProperty("useServerPrepStmts", true)
    }
    val dataSource = HikariDataSource(config)
    single(null, true) {
        Database.connect(dataSource, PostgreSqlDialect(), ConsoleLogger(LogLevel.DEBUG))
    }
    single(null, true) {
        RedisClient.create("redis://localhost:6379")
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
