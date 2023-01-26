package me.smp.core.pm

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import java.util.*

object PrivateMessagesSettings : Table<PrivateMessageSettings>("pm_settings") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val enabled = boolean("enabled").bindTo { it.enabled }
}

interface PrivateMessageSettings : Entity<PrivateMessageSettings> {
    companion object : Entity.Factory<PrivateMessageSettings>()
    val id: Int
    var player: UUID
    var enabled: Boolean
}

object IgnoredPlayersTable : Table<RemoteIgnoredPlayer>("pm_ignored") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val ignored = uuid("ignored").bindTo { it.ignored }
}

interface RemoteIgnoredPlayer : Entity<RemoteIgnoredPlayer> {
    companion object : Entity.Factory<RemoteIgnoredPlayer>()
    val id: Int
    var player: UUID
    var ignored: UUID
}
