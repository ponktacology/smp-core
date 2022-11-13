package me.smp.core

class Duration(private var millis: Long) {

    fun isPermanent() = millis < 0

    operator fun plus(increment: Long): Long {
        return millis + increment
    }

    operator fun plus(increment: Duration): Long {
        return millis + increment.millis
    }

    companion object {
        val PERMANENT = Duration(-1)
    }

    fun toMillis() = millis
}
