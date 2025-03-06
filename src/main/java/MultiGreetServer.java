import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiGreetServer {
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            pool = Executors.newFixedThreadPool(10); // Limita a 10 client simultanei
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new EchoClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) serverSocket.close();
            if (pool != null) pool.shutdown();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    private static class EchoClientHandler implements Runnable {
        private Socket clientSocket;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println("Echo: " + inputLine);
                }
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
                System.out.println("Client disconnected.");
            }
        }
    }

    public static void main(String[] args) {
        MultiGreetServer server = new MultiGreetServer();
        server.start(8888);
    }
}
