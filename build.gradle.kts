import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.6.10"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.usb4java:usb4java:1.3.0")
    implementation("com.fazecast:jSerialComm:2.8.5")
    implementation("org.jdom:jdom2:2.0.6.1")
    implementation("de.brudaswen.kotlinx.coroutines:kotlinx-coroutines-swt:1.0.0")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")

    antlr("org.antlr:antlr4:4.9.3")

    implementation(
        files(
            "lib/swt-linux.jar",
            "lib/yad2xxJava-1.0.jar",
        )
    )
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}