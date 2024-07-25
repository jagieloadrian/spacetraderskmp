plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    id("org.openapi.generator") version "7.7.0"
}

group = "com.anjo"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //swagger-ui
    implementation("org.springdoc:springdoc-openapi-starter-common:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")

    //kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    //database
    runtimeOnly("org.postgresql:postgresql")

    //test - spring
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    //test - kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")

    //test-coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    //test - containers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:mockserver")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")

    //test - platform
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
    jvmToolchain(17)
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

val generatedResourcesDir = "${layout.buildDirectory.get().asFile}/generated-resources"
kotlin.sourceSets["main"].kotlin.srcDir("$generatedResourcesDir/src/main/kotlin")

openApiGenerate {
    id = "SpaceTradersService"
    inputSpec = "$rootDir/src/main/resources/specification/SpaceTraders.yml"
    outputDir = generatedResourcesDir
    generatorName = "kotlin"
    packageName = "com.anjo.spacetradersservice.generated"
    configOptions.putAll(
            mapOf(
                    "serializationLibrary" to "jackson",
                    "useSpringBoot3" to "true",
                    "library" to "jvm-spring-webclient",
                    "dateLibrary" to "java8",
                    "reactive" to "true"
            )
    )
}