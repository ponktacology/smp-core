import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useK2 = true

plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "me.smp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.mattstudios.me/artifactory/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.17.1-R0.1-SNAPSHOT")
    implementation("me.smp:shared:1.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("io.insert-koin:koin-core:3.2.1")
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.github.vaperion.blade:bukkit:3.0.0")
    implementation("dev.triumphteam:triumph-gui:3.1.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("fr.mrmicky:fastboard:1.2.1")
    testImplementation(kotlin("test"))
}

task<Exec>("copy_to_instance") {
    dependsOn("build")
    commandLine("sh", "copy_to_directory.sh")
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.smp"
            artifactId = "core"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}
