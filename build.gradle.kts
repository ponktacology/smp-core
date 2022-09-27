import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "me.smp.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.17.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("io.insert-koin:koin-core:3.2.1")
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")
    implementation("fr.mrmicky:FastInv:3.0.3")
    implementation("com.github.vaperion.blade:bukkit:3.0.0")
    implementation("io.lettuce:lettuce-core:6.2.0.RELEASE")
    implementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation(kotlin("test"))
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
            groupId = "me.smp.core"
            artifactId = "core"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}
