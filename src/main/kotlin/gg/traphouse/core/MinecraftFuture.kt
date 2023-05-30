package gg.traphouse.core

import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object MinecraftFuture {

    fun <R> CompletableFuture<R>.thenAcceptMain(consumer: Consumer<R>): CompletableFuture<Void> {
        return this.thenAccept { Task.sync { consumer.accept(it) } }
    }
}