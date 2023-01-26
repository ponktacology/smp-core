package me.smp.core.rank

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class PlayerGrants {

    private val grants = CopyOnWriteArrayList<Grant>()

    fun findPrimary() = grants.filter { it.isActive() }.maxByOrNull { it.rank.power }?.rank ?: Rank.DEFAULT
    fun addAll(grants: Collection<Grant>) {
        this.grants.addAll(grants)
    }

    fun add(grant: Grant) {
        grants.add(grant)
    }

    fun unGrant(rank: Rank, issuer: UUID, reason: String) {
        grants.filter { it.rank == rank && it.isActive() }
            .forEach {
                it.removed = true
                it.issuer = issuer
                it.removedAt = System.currentTimeMillis()
                it.remover = issuer
                it.removeReason = reason
            }
    }

    fun activeGrants() = grants.filter { it.isActive() }.toList()

}