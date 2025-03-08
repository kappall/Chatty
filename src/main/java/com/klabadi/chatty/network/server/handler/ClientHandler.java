package com.klabadi.chatty.network.server.handler;

import com.klabadi.chatty.exception.AuthException;
import com.klabadi.chatty.model.User;
import com.klabadi.chatty.security.AuthService;
import com.klabadi.chatty.util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler extends Thread implements Runnable {
    private final Socket clientSocket;
    private final AuthService authService;
    private final ConcurrentMap<String, ClientHandler> activeClients;
    private User currentUser;
    private BufferedReader input;
    private PrintWriter output;

    public ClientHandler(Socket clientSocket,
                         AuthService authService,
                         ConcurrentMap<String, ClientHandler> activeClients) {
        this.clientSocket = clientSocket;
        this.authService = authService;
        this.activeClients = activeClients;
        Logger.info("New client connection: " + clientSocket.getInetAddress());
    }

    @Override
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
             BufferedReader reader = new BufferedReader(isr);
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            this.input = reader;
            this.output = writer;

            authenticateClient();
            handleClientMessages();

        } catch (IOException e) {
            Logger.error("Client connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void authenticateClient() throws IOException {
        try {
            output.println("AUTH_REQUEST");
            currentUser = authService.authenticate(input, output);
            activeClients.put(currentUser.getUsername(), this);
            broadcastSystemMessage(currentUser.getUsername() + " has joined the chat");
        } catch (AuthException e) {
            output.println("AUTH_ERROR: " + e.getMessage());
            throw new IOException("Authentication failed", e);
        }
    }

    private void handleClientMessages() throws IOException {
        String message;
        while ((message = input.readLine()) != null) {
            if ("/quit".equalsIgnoreCase(message)) {
                break;
            }
            broadcastUserMessage(message);
        }
    }

    private void broadcastUserMessage(String message) {
        String formatted = String.format("[%s]: %s", currentUser.getUsername(), message);
        activeClients.forEach((username, handler) -> {
            if (!username.equals(currentUser.getUsername())) {
                handler.sendMessage(formatted);
            }
        });
        Logger.info(formatted);
    }

    private void broadcastSystemMessage(String message) {
        String formatted = "[SYSTEM]: " + message;
        activeClients.values().forEach(handler -> handler.sendMessage(formatted));
        Logger.info(formatted);
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    private void cleanup() {
        if (currentUser != null) {
            activeClients.remove(currentUser.getUsername());
            broadcastSystemMessage(currentUser.getUsername() + " has left the chat");
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            Logger.error("Error closing client socket: " + e.getMessage());
        }
    }
}