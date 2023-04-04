package gg.traphouse.core.pm

import java.util.UUID

class IgnoredPlayers {

    private val ignoredPlayers = HashSet<UUID>()

    fun ignore(uuid: UUID) {
        ignoredPlayers.add(uuid)
    }

    fun isIgnoring(uuid: UUID) = ignoredPlayers.contains(uuid)

    fun unIgnore(uuid: UUID) {
        ignoredPlayers.remove(uuid)
    }
}