plugins {
    id("java-library")
    id("polocloud.common")
    alias(libs.plugins.lombok)
}

dependencies {
    compileOnlyApi(libs.annotations)
}

tasks.named("jar", Jar::class) {
    archiveFileName.set("polocloud-api-${version}.jar")
    from(sourceSets.main.get().output)
}