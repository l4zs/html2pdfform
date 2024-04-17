plugins {
    kotlin("jvm") version "1.9.21"
}

group = "de.jannis-kramer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.itextpdf:itext-core:8.0.3")
    implementation("com.itextpdf:bouncy-castle-adapter:8.0.3")
    implementation("com.itextpdf:html2pdf:5.0.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
