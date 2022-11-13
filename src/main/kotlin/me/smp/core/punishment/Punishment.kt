package me.smp.core.punishment

import me.smp.core.Manageable
import me.smp.core.duration
import org.ktorm.entity.Entity
import org.ktorm.schema.*

object Punishments : Table<Punishment>("punishments") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val address = varchar("address").bindTo { it.address }
    val type = enum<Punishment.Type>("type").bindTo { it.type }
    val addedAt = long("added_at").bindTo { it.addedAt }
    val issuer = uuid("issuer").bindTo { it.issuer }
    val reason = varchar("reason").bindTo { it.reason }
    val duration = duration("duration").bindTo { it.duration }
    val removed = boolean("removed").bindTo { it.removed }
    val removedAt = long("removed_at").bindTo { it.removedAt }
    val remover = uuid("remover").bindTo { it.remover }
    val removeReason = varchar("remove_reason").bindTo { it.removeReason }
}

interface Punishment : Entity<Punishment>, Manageable {
    companion object : Entity.Factory<Punishment>()

    var type: Type
    var address: String?

    enum class Type(
        val addFormat: String,
        val removeFormat: String
    ) {
        BAN("banned", "unbanned"),
        MUTE("muted", "unmuted"),
        KICK("kicked", "")
    }
}
