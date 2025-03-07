import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GreetServer {
    private static final int PORT = 8888;
    private static final Map<String, String> users = new ConcurrentHashMap<>();
    private static final Map<String, ClientHandler> activeClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                try {
                    serverSocket.setSoTimeout(10000);
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    handler.start();
                } catch (SocketTimeoutException e) {
                    if(activeClients.isEmpty()) {
                        System.out.println("Timeout: no client connected after 10 seconds. Disconnecting....");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String tmp = in.readLine();
                boolean error = true;
                while(error) {
                    try {
                        handleAuthentication();
                        error = false;
                    } catch (AuthException e) {
                        out.println(e.getMessage());
                    }
                }
                handleMessages();
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleAuthentication() throws IOException {
            out.println("Please enter your username:");
            username = in.readLine();
            if (username == null) return;
            username = username.trim();

            if (users.containsKey(username)) {
                if(activeClients.containsKey(username)){
                    throw new AuthException("Anothe session from this user is active");
                }
                while (true) {
                    out.println("Enter password:");
                    String password = in.readLine();
                    if (password != null && users.get(username).equals(password)) {
                        out.println("Login successful!");
                        break;
                    } else {
                        out.println("Invalid password. Try again.");
                    }
                }
            } else {
                out.println("New user! Please set your password:");
                String password = in.readLine();
                if (password != null) {
                    users.put(username, password);
                    out.println("Registration successful!");
                }
            }

            activeClients.put(username, this);
            broadcastMessage("SERVER", username + " has joined the chat!");
        }

        private void handleMessages() throws IOException {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/quit")) {
                    break;
                }
                broadcastMessage(username, message);
            }
        }

        private void broadcastMessage(String sender, String message) {
            String formattedMessage = sender + ": " + message;
            System.out.println(formattedMessage);
            for (Map.Entry<String, ClientHandler> entry : activeClients.entrySet()) {
                if(!entry.getKey().equals(sender))
                    entry.getValue().out.println(formattedMessage);
            }
        }

        private void cleanup() {
            if (username != null) {
                activeClients.remove(username);
                broadcastMessage("SERVER", username + " has left the chat!");
            }
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
