package gg.traphouse.core.pm

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import java.util.*

fun PrivateMessageSettings.toRemote() = RemotePrivateMessagesSettings {
    this.id = this@toRemote.id
    this.player = this@toRemote.uuid
    this.enabled = this@toRemote.enabled
}

fun RemotePrivateMessagesSettings.toDomain() = PrivateMessageSettings(this.id, this.player, this.enabled)

object PrivateMessagesSettingsTable : Table<RemotePrivateMessagesSettings>("pm_settings") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val enabled = boolean("enabled").bindTo { it.enabled }
}

interface RemotePrivateMessagesSettings : Entity<RemotePrivateMessagesSettings> {
    companion object : Entity.Factory<RemotePrivateMessagesSettings>()

    var id: Int
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
