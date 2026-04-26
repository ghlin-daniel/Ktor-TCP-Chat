package com.guanhaolin

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap


data class Client(
  val name: String,
  val receiveChannel: ByteReadChannel,
  val writeChannel: ByteWriteChannel,
)

private const val host: String = "localhost"
private const val port: Int = 8080

private val clients = ConcurrentHashMap<Int, Client>()

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
  runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).tcp().bind(host, port)
    println("Server is listening at ${serverSocket.localAddress}")
    while (true) {
      val socket = serverSocket.accept()
      println("Server: Accepted ${socket.remoteAddress}")
      launch {
        onNewConnection(socket)
      }
    }
  }
}

private suspend fun onNewConnection(socket: Socket) {
  val receiveChannel = socket.openReadChannel()
  val sendChannel = socket.openWriteChannel(autoFlush = true)
  sendChannel.writeStringUtf8("Server: Welcome! What's your name?\n")
  val name = receiveChannel.readLine() ?: return
  sendChannel.writeStringUtf8("Server: Hi, $name\n")
  val client = Client(name, receiveChannel, sendChannel)
  clients[client.hashCode()] = client

  try {
    while (true) {
      val message = receiveChannel.readLine() ?: throw IllegalStateException("$name left")
      println("Received $message from ${socket.remoteAddress}")
      broadcast(client, message)
    }
  } catch (e: Throwable) {
    println("Server: Error ${e.message}")
    withContext(Dispatchers.IO) {
      socket.close()
      println("Server: ${client.name} left")
      clients.remove(client.hashCode())
    }
  }
}

private suspend fun broadcast(sender: Client, message: String) {
  println("Broadcasting $message")
  clients.forEach { (clientKey, client) ->
    if (clientKey != sender.hashCode()) {
      client.writeChannel.writeString("${sender.name}: $message\n")
      println("Sent $message to ${client.name}")
    }
  }
}
