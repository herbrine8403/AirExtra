pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://server.bbkr.space/artifactory/libs-release/") }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "AirExtra"
