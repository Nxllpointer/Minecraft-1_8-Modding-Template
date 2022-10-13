import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import gg.essential.gradle.util.noServerRunConfigs

plugins {
    java
    kotlin("jvm") version "1.7.20"
    id("gg.essential.loom") version "0.10.0.+" // Essential architectury-loom
    id("gg.essential.defaults") version "0.1.16" // Defaults for essential architectury-loom
    id("com.github.johnrengelman.shadow") version "7.1.2" // Shadow plugin to add dependencies to the jar
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
}

@Suppress
dependencies {
    implementation("org.spongepowered:mixin:0.7.11-SNAPSHOT") // Include mixin at compile and runtime
    shadow("org.spongepowered:mixin:0.7.11-SNAPSHOT") // Include mixins in the JAR

    // Include kotlin in the JAR
    shadow("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
    shadow("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
}

group = "com.example.examplemod"
version = "1.0.0"

loom {
    noServerRunConfigs()

    runs {
        getByName("client") {
            programArgs(
                "--tweakClass", "org.spongepowered.asm.launch.MixinTweaker",
                "--mixin", "examplemod.mixins.json"
            )
        }
    }

    forge {
        mixinConfigs("examplemod.mixins.json")
    }

    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("examplemod.refmap.json")
    }
}

// mcmod.info wont work without this
sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/kotlin/main"))
}

// Minecraft 1.8.9 runs on Java 8
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
kotlin.jvmToolchain(jdkVersion = 8)
tasks.compileKotlin.get().kotlinOptions {
    jvmTarget = "1.8"
    languageVersion = "1.7"
}

tasks {

    // Replace the MOD_VERSION placeholder with the actual mod version
    processResources {
        inputs.property("MOD_VERSION", version)

        filesMatching("mcmod.info") {
            expand("MOD_VERSION" to project.version)
        }
    }

    // This task will move all libraries to a custom package to avoid dependency conflicts
    val relocateShadowJar by creating(ConfigureShadowRelocation::class) {
        target = shadowJar.get()
        prefix = "${project.group}.libs"
    }

    // Specify your JAR settings here. ShadowJAR will inherit them
    jar {
        enabled = false // Disable the jar task because remapJar unnecessarily calls it. We replace jar with shadowJar

        manifest.attributes(
            // Mixins are loaded on startup using a tweaker
            "TweakClass" to "${relocateShadowJar.prefix}.org.spongepowered.asm.launch.MixinTweaker",
        )
    }

    // This task will include all libraries that use shadow("com.example:mylibrary:1.2.3") in the JAR
    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
        mergeServiceFiles() // Relocate the service files (Mixins wont work without this)

        archiveClassifier.set("mapped")
        destinationDirectory.set(temporaryDir)

        dependsOn(relocateShadowJar)
        finalizedBy(remapJar)
    }

    // This task changes the readable names back to unreadable names that minecraft can understand
    remapJar {
        input.set(shadowJar.get().archiveFile) // Use shadowJar output
    }

}



