# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin/JVM project (`ktor-tcp-chat`) intended to implement a TCP chat server/client using Ktor. Currently in early scaffolding — only the Gradle build and a placeholder `Main.kt` exist.

- Group: `com.guanhaolin`
- JVM toolchain: 21
- Kotlin: 2.3.20

## Build & Run

```bash
./gradlew build          # compile + test
./gradlew run            # run Main.kt (add application plugin first)
./gradlew test           # run all tests
./gradlew test --tests "com.guanhaolin.SomeTest.methodName"  # single test
```

## Architecture

Source lives under `src/main/kotlin/` in package `com.guanhaolin`. Tests go in `src/test/kotlin/` with the same package.

The project does not yet have the Ktor dependency wired up. When adding it, the expected dependencies are:
- `io.ktor:ktor-server-core` / `ktor-server-netty` for the server
- `io.ktor:ktor-network` for raw TCP socket support
- `io.ktor:ktor-client-core` / `ktor-client-cio` for the client side

Apply the `application` Gradle plugin and set `mainClass` to `com.guanhaolin.MainKt` to enable `./gradlew run`.
