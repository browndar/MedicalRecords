import java.nio.file.Paths

plugins {
    kotlin("jvm") version "1.9.23"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.21"
    //java
}

group = "medrec"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("ch.qos.logback","logback-classic", "1.2.6")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.cloud:google-cloud-aiplatform:3.35.0")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.0")
    testImplementation(kotlin("test"))

    // junit 5
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation ("org.mockito:mockito-inline:3.2.0")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
}

tasks.test {
    useJUnitPlatform()
    // sharing service account creds for ease of use for example. (Using adc)
    environment("GOOGLE_APPLICATION_CREDENTIALS", Paths.get(sourceSets.main.get().output.resourcesDir!!.absolutePath, "medrec-429721-b51b01a2770d.json"))
}
//apply(plugin = "application")

application {

    mainClass = "medrec.MainKt"
  //  classPath
}

val run by tasks.getting(JavaExec::class) {
    mainClass = "medrec.MainKt"
    environment("GOOGLE_APPLICATION_CREDENTIALS", Paths.get(sourceSets.main.get().output.resourcesDir!!.absolutePath, "medrec-429721-b51b01a2770d.json"))
    classpath(sourceSets["main"].runtimeClasspath)
}




kotlin {
    jvmToolchain(21)
}

