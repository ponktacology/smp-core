package test

import gg.traphouse.core.cooldown.*
import gg.traphouse.shared.ConnectionProvider
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.State
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 20)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 20)
@Fork(2)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class Test {

    private val database = ConnectionProvider.database("localhost", 5432, "smp", "smp", "1234")

    private val cooldownsByType = HashMap<String, CooldownType>()
    private val Database.cooldowns get() = this.sequenceOf(CooldownsTable)

    init {
        repeat(50) {
            addCooldown(ThreadLocalRandom.current().nextLong().toString())
        }
    }

    fun addCooldown(name: String) {
        cooldownsByType[name] = object : CooldownType {
            override val type: String
                get() = name
            override val duration: Long
                get() = TimeUnit.MILLISECONDS.convert(
                    abs(ThreadLocalRandom.current().nextLong()),
                    TimeUnit.MILLISECONDS
                )

        }
    }

    @Benchmark()
    fun test1() {
        val uuid = UUID.randomUUID()
        val cooldowns = PlayerCooldowns()
        val loadedCooldowns = database.cooldowns.filter { it.player eq uuid }.associateBy({ it.type }, { it })

        loadedCooldowns.values.forEach { cooldown ->
            val type = cooldownsByType[cooldown.type] ?: let {
                database.cooldowns.removeIf { cooldown.type eq it.type }
                return@forEach
            }

            cooldowns.register(cooldown.toDomain(type))
        }

        cooldownsByType.values.forEach { type ->
            if (loadedCooldowns.containsKey(type.type)) return@forEach

            val remoteCooldown = RemoteCooldown {
                this.player = uuid
                this.type = type.type
                this.resetAt = System.currentTimeMillis()
            }

            database.cooldowns.add(remoteCooldown)
            cooldowns.register(remoteCooldown.toDomain(type))
        }
    }

    @Benchmark
    fun test2() {
        val uuid = UUID.randomUUID()
        val cooldowns = PlayerCooldowns()
        val loadedCooldowns = database.cooldowns.filter { it.player eq uuid }.toList()

        loadedCooldowns.forEach { cooldown ->
            val type = cooldownsByType[cooldown.type] ?: let {
                database.cooldowns.removeIf { cooldown.type eq it.type }
                return@forEach
            }
            cooldowns.register(cooldown.toDomain(type))
        }

        cooldownsByType.values.forEach { type ->
            if (loadedCooldowns.any { it.type == type.type }) return@forEach

            val remoteCooldown = RemoteCooldown {
                this.player = uuid
                this.type = type.type
                this.resetAt = System.currentTimeMillis()
            }

            database.cooldowns.add(remoteCooldown)
            cooldowns.register(remoteCooldown.toDomain(type))
        }
    }
}