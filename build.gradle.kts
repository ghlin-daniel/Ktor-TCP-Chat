plugins {
  kotlin("jvm") version "2.3.20"
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

kotlin {
  jvmToolchain(21)
}

tasks.test {
  useJUnitPlatform()
}