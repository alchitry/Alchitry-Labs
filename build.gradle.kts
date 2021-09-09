import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.5.30"
    antlr
    application
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("res")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("commons-io:commons-io:2.4")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("org.apache.commons:commons-text:1.6")
    implementation("org.apache.commons:commons-compress:1.9")
    implementation("org.usb4java:usb4java:1.3.0")
    implementation("com.fazecast:jSerialComm:2.6.2")
    implementation("org.jdom:jdom2:2.0.6")
    implementation("de.brudaswen.kotlinx.coroutines:kotlinx-coroutines-swt:1.0.0")

    antlr("org.antlr:antlr4:4.9.2")

    implementation(
        files(
            "lib/swt-linux.jar",
            "lib/yad2xxJava-1.0.jar",
        )
    )
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}