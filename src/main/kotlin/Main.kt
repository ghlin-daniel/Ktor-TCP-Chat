package com.guanhaolin

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val host: String = "localhost"
private const val port: Int = 8080

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
  runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).tcp().bind(host, port)
    println("Server is listening at ${serverSocket.localAddress}")
    while (true) {
      val socket = serverSocket.accept()
      println("Accepted ${socket.remoteAddress}")
      launch {
        val receiveChannel = socket.openReadChannel()
        val sendChannel = socket.openWriteChannel(autoFlush = true)
        sendChannel.writeStringUtf8("Server: Welcome!\n")
        try {
          while (true) {
            val message = receiveChannel.readLine()
            println("Received $message from ${socket.remoteAddress}")
            sendChannel.writeStringUtf8("$message\n")
          }
        } catch (e: Throwable) {
          socket.close()
        }
      }
    }
  }
}