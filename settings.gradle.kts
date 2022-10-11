pluginManagement {
    repositories {
        // StandardD gradle plugin repos
        gradlePluginPortal()
        mavenCentral()

        // Required repositories for the essential-gradle-toolkit
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }
}


rootProject.name = "Mc-1_8-Template"