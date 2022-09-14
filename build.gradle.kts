import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}


group = "me.smp.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.17.1-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("io.insert-koin:koin-core:3.2.1")

    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")

    implementation("org.mongodb:mongodb-driver-sync:4.7.1")

    implementation("com.github.vaperion.blade:bukkit:3.0.0")

    testImplementation(kotlin("test"))
}

tasks.shadowJar {
     relocate("com.google", "me.smp.core.com.google")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn("shadowJar")

    doLast {
        println("Build successful!")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}