
plugins {
    id("java-library")
    id("polocloud.common")
    alias(libs.plugins.lombok)
}

dependencies {
    implementation(project(":polocloud-api")) // Projektabhängigkeit hinzufügen
    compileOnlyApi(libs.annotations)
}

tasks.jar {
    dependsOn(":polocloud-api:jar")

    doFirst {
        val apiJar = project(":polocloud-api").tasks.findByName("jar")
            ?: throw IllegalStateException(":polocloud-api:jar task not found!")
    }

    from(project(":polocloud-api").tasks.getByPath(":polocloud-api:jar").outputs.files)

    manifest {
        attributes("Main-Class" to "dev.httpmarco.polocloud.launcher.PolocloudBoot")
    }

    archiveFileName.set("polocloud-launcher-${version}.jar")
}

