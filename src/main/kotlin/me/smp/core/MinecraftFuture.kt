package me.smp.core

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Consumer

object MinecraftFuture {

    fun <R> CompletableFuture<R>.thenAcceptMain(consumer: Consumer<R>): CompletableFuture<Void> {
        return this.thenAccept { TaskDispatcher.dispatch { consumer.accept(it) } }
    }
}