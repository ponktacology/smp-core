
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("org.jetbrains.kotlinx.benchmark") version "0.4.8"
    id("me.champeau.jmh") version "0.6.8"
    kotlin("plugin.allopen") version "1.8.21"
}

description = "core"
group = "gg.traphouse"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.mattstudios.me/artifactory/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:+")
    implementation("dev.triumphteam:triumph-gui:+")
    implementation("gg.traphouse:shared:+")
    implementation("io.insert-koin:koin-core:+")
    implementation("org.ktorm:ktorm-core:+")
    implementation("org.ktorm:ktorm-support-postgresql:+")
    implementation("com.zaxxer:HikariCP:+")
    implementation("me.vaperion.blade:bukkit:+")
    implementation("io.lettuce:lettuce-core:+")
    implementation("fr.mrmicky:fastboard:+")
    implementation("org.postgresql:postgresql:+")
    compileOnly("com.comphenix.protocol:ProtocolLib:+")

    testImplementation("org.openjdk.jmh:jmh-core:1.35")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.35")
    testImplementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")
    testImplementation("dev.triumphteam:triumph-gui:+")
    testImplementation("gg.traphouse:shared:+")
    testImplementation("io.insert-koin:koin-core:+")
    testImplementation("org.ktorm:ktorm-core:+")
    testImplementation("org.ktorm:ktorm-support-postgresql:+")
    testImplementation("com.zaxxer:HikariCP:+")
    testImplementation("me.vaperion.blade:bukkit:+")
    testImplementation("io.lettuce:lettuce-core:+")
    testImplementation("fr.mrmicky:fastboard:+")
    testImplementation(kotlin("test"))
    testImplementation("org.postgresql:postgresql:+")
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

task<Exec>("copy_to_instance") {
    dependsOn("build")
    commandLine("sh", "copy_to_directory.sh")
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.withType<KotlinCompile> {
    this.kotlinOptions.languageVersion = "2.0"
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "gg.traphouse"
            artifactId = "core"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}
