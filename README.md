# ktor-tcp-chat

A minimal TCP chat server built with [Ktor](https://ktor.io/) and Kotlin coroutines.

When a client connects, the server asks for a name and then relays every subsequent message to all other connected clients.

## Requirements

- JDK 21+

## Run

```bash
./gradlew run
```

The server listens on `localhost:8080` by default. Connect with any TCP client, e.g.:

```bash
nc localhost 8080
```

## Stack

| Library | Version | Role |
|---|---|---|
| Kotlin | 2.3.20 | Language |
| Ktor Network (`ktor-network`) | 3.4.3 | TCP socket primitives |
| kotlinx.coroutines | 1.10.2 | Async I/O, per-client coroutines |

## How it works

Each accepted connection is handled in its own coroutine (`launch`). After the client provides a name, the server enters a read loop and calls `broadcast()` on every message, writing it to all other connected clients.

Clients are tracked in a `HashMap<Socket, Client>`. On disconnect, the client is removed from the map before the socket is closed, ensuring concurrent `broadcast()` calls never write to a closing socket. `broadcast()` snapshots the map with `toList()` before iterating to avoid modification during suspension.

## License

[MIT](LICENSE)
