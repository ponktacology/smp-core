package me.smp.core.rank

import me.smp.core.Duration
import me.smp.core.duration
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.util.UUID


object Grants : Table<Grant>("grants") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = uuid("player").bindTo { it.player }
    val rank = enum<Rank>("rank").bindTo { it.rank }
    val addedAt = long("added_at").bindTo { it.addedAt }
    val issuer = uuid("issuer").bindTo { it.issuer }
    val reason = varchar("reason").bindTo { it.reason }
    val duration = duration("duration").bindTo { it.duration }
    val removed = boolean("removed").bindTo { it.removed }
    val removedAt = long("removed_at").bindTo { it.removedAt }
    val remover = uuid("remover").bindTo { it.remover }
    val removeReason = varchar("remove_reason").bindTo { it.removeReason }
}

interface Grant : Entity<Grant> {
    companion object : Entity.Factory<Grant>()

    val id: Int
    var player: UUID
    var rank: Rank
    var issuer: UUID
    var reason: String
    var duration: Duration
    var addedAt: Long
    var removed: Boolean
    var removedAt: Long?
    var remover: UUID?
    var removeReason: String?

    fun isActive() = !(removed || (!duration.isPermanent() && duration + addedAt < System.currentTimeMillis()))
}