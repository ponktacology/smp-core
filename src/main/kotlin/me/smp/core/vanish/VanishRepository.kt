package me.smp.core.vanish

import me.smp.core.UUIDCache
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class VanishRepository : UUIDCache, KoinComponent {

    private val database: Database by inject()
    private val Database.vanishSettings get() = this.sequenceOf(VanishSettingsTable)
    private val cache = ConcurrentHashMap<UUID, VanishSettings>()

    override fun loadCache(uuid: UUID) {
        database.vanishSettings.find { it.player eq uuid }?.let {
            cache[uuid] = it
        }
    }

    fun getByOnlinePlayer(player: Player): VanishSettings {
        return cache.computeIfAbsent(player.uniqueId) {
            VanishSettings {
                this.player = player.uniqueId
                this.enabled = false
            }.also {
                database.vanishSettings.add(it)
            }
        }
    }

    override fun flushCache(uuid: UUID) {
        cache.remove(uuid)
    }

    override fun verifyCache(uuid: UUID): Boolean {
        return true
    }

    fun flushCache() {
        cache.clear()
    }
}
