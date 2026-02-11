pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://server.bbkr.space/artifactory/libs-release/") }
        gradlePluginPortal()
    }
}

rootProject.name = "AirExtra"
