

pluginManagement {
    includeBuild("polocloud-gradle-structure")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "polocloud"

include(
    "polocloud-api",
    "polocloud-bom",
    "polocloud-common",
    "polocloud-daemon",
    "polocloud-installer",
    "polocloud-launcher",
    "polocloud-node",
    "polocloud-components",
    "polocloud-components:component-terminal"
)
findProject(":polocloud-components:component-terminal")?.name = "component-terminal"
