package com.guanhaolin

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


data class Client(
  val name: String,
  val receiveChannel: ByteReadChannel,
  val writeChannel: ByteWriteChannel,
)

private const val host: String = "localhost"
private const val port: Int = 8080

fun main() {
  val clients = HashMap<Socket, Client>()
  runBlocking {
    SelectorManager(Dispatchers.IO).use { selectorManager ->
      val serverSocket = aSocket(selectorManager).tcp().bind(host, port)
      println("Server is listening at ${serverSocket.localAddress}")
      while (true) {
        val socket = serverSocket.accept()
        println("Server: Accepted ${socket.remoteAddress}")
        launch {
          onNewConnection(socket, clients)
        }
      }
    }
  }
}

private suspend fun onNewConnection(socket: Socket, clients: HashMap<Socket, Client>) {
  val receiveChannel = socket.openReadChannel()
  val sendChannel = socket.openWriteChannel(autoFlush = true)
  sendChannel.writeStringUtf8("Server: Welcome! What's your name?\n")
  val name = receiveChannel.readLine()?.trim()?.takeIf { it.isNotBlank() } ?: return
  sendChannel.writeStringUtf8("Server: Hi, $name\n")
  val client = Client(name, receiveChannel, sendChannel)
  clients[socket] = client

  try {
    while (true) {
      val message = receiveChannel.readLine() ?: throw IllegalStateException("$name left")
      if (message.isBlank()) continue
      println("Received $message from ${socket.remoteAddress}")
      broadcast(client, message, clients)
    }
  } catch (e: Exception) {
    println("Server: Error ${e.message}")
    clients.remove(socket)
    withContext(Dispatchers.IO) { socket.close() }
    println("Server: ${client.name} left")
  }
}

private suspend fun broadcast(sender: Client, message: String, clients: HashMap<Socket, Client>) {
  println("Broadcasting $message")
  clients.values.toList().forEach { client ->
    if (client != sender) {
      client.writeChannel.writeString("${sender.name}: $message\n")
      println("Sent $message to ${client.name}")
    }
  }
}
