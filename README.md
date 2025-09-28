# JavaFX ChatApp

This project is a simple **chat application** built using **Java Sockets** for networking and **JavaFX** for the user interface.  

## What the project does

- The **server** (`ChatServer`) runs on a port and waits for clients to connect.
- Each **client** (`ChatClientUI`) opens a chat window where users can send and receive messages.
- Messages from one client are **broadcast** to all connected clients through the server.
- The server uses **multithreading** so it can handle multiple clients at the same time.
- If a client stays **idle for 10 seconds**, it is automatically disconnected.

## What happens when you run it

1. Start the **server** → it begins listening for incoming client connections.
2. Run one or more **clients** → each client window connects to the server.
3. When you type a message in the client window:
   - The message goes to the server.
   - The server sends it to **all clients** (including the sender).
4. If a client does not send anything for 10 seconds, it disconnects automatically.
5. The server keeps running and accepts new clients until it is stopped.
