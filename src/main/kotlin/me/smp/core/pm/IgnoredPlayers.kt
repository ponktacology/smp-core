package me.smp.core.pm

import java.util.UUID

class IgnoredPlayers {

    private val ignoredPlayers = HashSet<UUID>()
    private val newlyIgnored = HashSet<UUID>()

    fun ignore(uuid: UUID) {
        newlyIgnored.add(uuid)
    }

    fun isIgnoring(uuid: UUID) = ignoredPlayers.contains(uuid)

    fun unIgnore(uuid: UUID) {
        ignoredPlayers.remove(uuid)
        newlyIgnored.remove(uuid)
    }
}