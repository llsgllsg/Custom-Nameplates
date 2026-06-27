plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
}

// 从环境变量或默认值获取版本标识
val git: String = versionBanner()
val builder: String = builder()

ext["git_version"] = git
ext["builder"] = builder

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    // 统一设置为 Java 21
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
    }

    tasks.processResources {
        filteringCharset = "UTF-8"

        filesMatching(listOf("custom-nameplates.properties")) {
            expand(rootProject.properties)
        }

        filesMatching(listOf("*.yml", "*/*.yml")) {
            expand(
                Pair("project_version", rootProject.properties["project_version"]!!),
                Pair("config_version", rootProject.properties["config_version"]!!)
            )
        }
    }
}

fun versionBanner(): String {
    return System.getenv("GIT_COMMIT_SHORT")
        ?: System.getenv("GITHUB_SHA")?.take(8)
        ?: "Unknown"
}

fun builder(): String {
    return System.getenv("GIT_USER_NAME")
        ?: System.getenv("GITHUB_ACTOR")
        ?: "Unknown"
}
