package com.klabadi.chatty.network.server;

import com.klabadi.chatty.network.server.handler.ClientHandler;
import com.klabadi.chatty.security.AuthService;
import com.klabadi.chatty.security.BasicAuthService;
import com.klabadi.chatty.util.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

public class ChatServer {
    private final int port;
    private final AuthService authService;
    private final ConcurrentMap<String, ClientHandler> activeClients;
    private final ExecutorService threadPool;
    private volatile boolean isRunning;

    public ChatServer(int port) {
        this.port = port;
        this.authService = new BasicAuthService();
        this.activeClients = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
        this.isRunning = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port,50, InetAddress.getByName("0.0.0.0"))) {
            serverSocket.setSoTimeout(5000); // 5 second accept timeout
            Logger.info("Chat server started on port " + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewConnection(clientSocket);
                } catch (SocketTimeoutException e) {
                    // Timeout is normal, just loop again
                } catch (IOException e) {
                    if (isRunning) {
                        Logger.error("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Server startup failed: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void handleNewConnection(Socket clientSocket) {
        ClientHandler handler = new ClientHandler(
                clientSocket,
                authService,
                activeClients
        );
        threadPool.submit(handler);
        Logger.info("New client connection from: " + clientSocket.getInetAddress());
    }

    public void shutdown() {
        isRunning = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            activeClients.clear();
            Logger.info("Server shutdown complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8888);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}