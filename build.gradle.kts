import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    kotlin("plugin.jpa") version "1.5.10"
    kotlin("plugin.allopen") version "1.5.10"

    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

if (!JavaVersion.current().isJava11Compatible) {
    error(
        """
        =======================================================
        RUN WITH JAVA 11
        =======================================================
    """.trimIndent()
    )
}

group = "io.rafaeljpc.spring.data.rest.test"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.+")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.+")
    implementation("org.springdoc:springdoc-openapi-data-rest:1.5.+")
    implementation("com.h2database:h2:1.4.+")
    implementation("io.github.microutils:kotlin-logging-jvm:2.+")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-data-rest")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.+")
    testImplementation("com.ninja-squad:springmockk:3.+")
    testImplementation("io.mockk:mockk:1.+")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.+")

    testImplementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.registering(JavaCompile::class) {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.encoding = "UTF-8"
    options.compilerArgs = listOf("-Xlint:unchecked", "-Xlint:deprecation")
}
