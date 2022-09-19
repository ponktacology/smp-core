package me.smp.core.teleport

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class TeleportRepository {

    private val cache = ConcurrentHashMap<UUID, DelayedTeleport>()

    fun teleport(player: Player, location: Location, delayInSeconds: Int) {
        cache[player.uniqueId] = DelayedTeleport(player.uniqueId, location, delayInSeconds) { cache.remove(it) }
    }

    fun isTeleporting(player: Player) = cache.containsKey(player.uniqueId)
    fun cancelTeleport(player: Player) = cache[player.uniqueId]?.cancel()

    fun flushCache() = cache.clear()
}