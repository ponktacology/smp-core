package gg.traphouse.core.protection

import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity

class ProtectionService {

    fun getChunksByEntityCount(world: World, amount: Int): List<MutableMap.MutableEntry<Chunk, MutableSet<Entity>>> {
        val entities = mutableMapOf<Chunk, MutableSet<Entity>>()
        world.entities.forEach { entities.computeIfAbsent(it.chunk) { mutableSetOf() }.add(it) }
        return entities.entries.sortedByDescending { it.value.size }.take(amount)
    }
}
