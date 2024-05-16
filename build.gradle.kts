plugins {
    kotlin("jvm") version "1.9.21"
}

group = "de.jannis-kramer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.librepdf:openpdf:2.0.2")
    implementation("org.jsoup:jsoup:1.17.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
