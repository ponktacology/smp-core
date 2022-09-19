package me.smp.core.warp

import com.google.common.collect.ImmutableList
import me.smp.core.SyncCatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.forEach
import org.ktorm.entity.sequenceOf
import java.util.concurrent.ConcurrentHashMap

internal class WarpRepository : KoinComponent {

    private val database: Database by inject()
    private val Database.warps get() = this.sequenceOf(Warps)
    private val cache = ConcurrentHashMap<String, Warp>()

    fun loadCache() {
        database.warps.forEach {
            cache[it.name.uppercase()] = it
        }
    }

    fun flushCache() {
        cache.clear()
    }

    fun getByName(name: String): Warp? {
        return cache[name.uppercase()]
    }

    fun createWarp(warp: Warp): Boolean {
        SyncCatcher.verify()
        return database.warps.add(warp).also {
            cache[warp.name.uppercase()] = warp
        } == 1
    }

    fun removeWarp(warp: Warp) {
        SyncCatcher.verify()
        database.delete(Warps) { it.id eq warp.id }
        cache.remove(warp.name.uppercase())
    }

    fun update(warp: Warp) = warp.flushChanges()

    fun getAll() = ImmutableList.copyOf(cache.values)
}