import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.0"
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.github.trueddd"
version = "1.0"
val pluginName = "race-setter-${version}.jar"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api-plugin:2.7.1")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set(pluginName)
        mergeServiceFiles()
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClass.get()
                )
            )
        }
    }
}

task("buildAndPrepare") {
    dependsOn("shadowJar")
    copy {
        from("$buildDir/libs/$pluginName")
        into("$projectDir/jars")
    }
}