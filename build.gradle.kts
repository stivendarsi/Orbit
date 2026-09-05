plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("io.lettuce:lettuce-core:7.6.0.RELEASE")
    compileOnly("com.nexomc:nexo:1.27.0")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.2.0")
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("net.luckperms:api:5.5")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        destinationDirectory.set(file("D:/Minecraft/servers/network/survival_folia/plugins"))
      //  destinationDirectory.set(file("D:/Minecraft/servers/26.2/plugins"))
        archiveClassifier.set("")

        relocate("io.lettuce", "me.stivendarsi.libs.lettuce")
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
