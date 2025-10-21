plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "yo.soft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion: String by project
val tgbotapiVersion: String by project
val ktormVersion: String by project
val postgresVersion: String by project
val kodeinVersion: String by project
val typesafeVersion: String by project
val kotlinJsonVersion: String by project
val logbackVersion: String by project
val liquibaseVersion: String by project
val mockkVersion: String by project

dependencies {
    // WEB Client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    // BOT
    implementation("dev.inmo:tgbotapi:$tgbotapiVersion")
    // DB
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    // DI
    implementation("org.kodein.di:kodein-di:$kodeinVersion")
    implementation("com.typesafe:config:$typesafeVersion")
    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinJsonVersion")
    // LOG
    implementation("ch.qos.logback:logback-classic:$logbackVersion") // SLF4J + Logback
    // https://mvnrepository.com/artifact/org.liquibase/liquibase-maven-plugin
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    // TEST
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:$mockkVersion")
    // BOM
    implementation(platform("io.kriptal.ethers:ethers-bom:1.4.4"))
    // KT ETH
    implementation("io.kriptal.ethers:ethers-abi")
    implementation("io.kriptal.ethers:ethers-core")
    implementation("io.kriptal.ethers:ethers-providers")
    implementation("io.kriptal.ethers:ethers-signers")
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runDev") {
    group = "application"
    description = "Run the application with the 'local' profile"
    mainClass = application.mainClass
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf(
        "-Dprofile=local"
    )
}

kotlin {
    jvmToolchain(17)
}