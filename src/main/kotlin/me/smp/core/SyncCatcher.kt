package me.smp.core

import org.bukkit.Bukkit

object SyncCatcher {

    fun verify() {
        if (Bukkit.isPrimaryThread()) throw IllegalStateException("This method can't be called from main thread")
    }
}