plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
}

// 从环境变量或默认值获取版本标识（原 git rev-parse）
val git: String = versionBanner()
// 从环境变量或默认值获取构建者（原 git config user.name）
val builder: String = builder()

ext["git_version"] = git
ext["builder"] = builder

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

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

/**
 * 获取版本标识（原 git rev-parse --short=8 HEAD）
 * 优先使用环境变量：
 *   - GIT_COMMIT_SHORT（自定义）
 *   - GITHUB_SHA（GitHub Actions 自动提供）取其前8位
 * 若都未设置，则返回 "Unknown"
 */
fun versionBanner(): String {
    return System.getenv("GIT_COMMIT_SHORT")
        ?: System.getenv("GITHUB_SHA")?.take(8)
        ?: "Unknown"
}

/**
 * 获取构建者名称（原 git config user.name）
 * 优先使用环境变量：
 *   - GIT_USER_NAME（自定义）
 *   - GITHUB_ACTOR（GitHub Actions 的触发者用户名）
 * 若都未设置，则返回 "Unknown"
 */
fun builder(): String {
    return System.getenv("GIT_USER_NAME")
        ?: System.getenv("GITHUB_ACTOR")
        ?: "Unknown"
}
