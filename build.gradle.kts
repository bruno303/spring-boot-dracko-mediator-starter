import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
    id("maven-publish")
}

val projectVersion = '1.1.0'

group = "com.bso"
version = projectVersion

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    implementation(libs.dracko.mediator.core)
    implementation(springLibs.starter)
    testImplementation(springLibs.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}
