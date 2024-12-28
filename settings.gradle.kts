pluginManagement {
    includeBuild("polocloud-gradle-structure")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "polocloud"

include("polocloud-api")
include("polocloud-bom")
include("polocloud-common")
include("polocloud-daemon")
include("polocloud-installer")
include("polocloud-launcher")
include("polocloud-node")