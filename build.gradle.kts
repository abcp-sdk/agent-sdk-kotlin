plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
}

group = "com.agent"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.connectrpc:connect-kotlin-okhttp:0.8.0")
    implementation("com.connectrpc:connect-kotlin-google-javalite-ext:0.8.0")
    implementation("com.google.protobuf:protobuf-javalite:4.34.0")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.34.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
