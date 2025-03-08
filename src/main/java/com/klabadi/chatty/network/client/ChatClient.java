package com.klabadi.chatty.network.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;

    public boolean startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void stopConnection() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void startMessageReader() {
        new Thread(() -> {
            try {
                String message;
                while (running && (message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("Connection lost: " + e.getMessage());
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        if (client.startConnection("127.0.0.1", 8888)) {
            // Start message reader thread
            client.startMessageReader();

            // Handle user input
            Scanner scanner = new Scanner(System.in);
            while (client.running) {
                String message = scanner.nextLine();
                if ("/quit".equalsIgnoreCase(message)) {
                    client.sendMessage(message);
                    client.stopConnection();
                    break;
                }
                client.sendMessage(message);
            }

            scanner.close();
        }
    }
}