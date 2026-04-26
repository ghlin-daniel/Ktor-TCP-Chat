plugins {
  kotlin("jvm") version "2.3.20"
  application
}

group = "com.guanhaolin"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  implementation("io.ktor:ktor-network:3.4.3")
}

application {
  mainClass.set("com.guanhaolin.MainKt")
}

kotlin {
  jvmToolchain(21)
}

tasks.test {
  useJUnitPlatform()
}