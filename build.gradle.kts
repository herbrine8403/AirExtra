buildscript {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://server.bbkr.space/artifactory/libs-release/") }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // 将 fabric-loom 作为 classpath 依赖来加载（如果需要其它版本，请修改此处）
        classpath("net.fabricmc:fabric-loom:1.3")
    }
}

// 通过 buildscript classpath 加载后以 apply 方式使用插件（避免 plugins {} 解析失败）
apply(plugin = "fabric-loom")

plugins {
    java
    `maven-publish`
}

base {
    archivesName = "AirExtra"
}

loom {
    accessWidenerPath = file("src/main/resources/airextra.accesswidener")
}

repositories {
    mavenLocal()
    maven { url = uri("https://maven.fabricmc.net/") }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.100.1+1.21.11")
}

version = "1.0.0"
group = "com.airextra"

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
