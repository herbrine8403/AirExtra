plugins {
    java
    id("fabric-loom") version "1.15"
    `maven-publish`
}

version = "1.0.0"
group = "com.airextra"

base {
    archivesName = "AirExtra"
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.100.1+1.21.11")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}"}
    }
}