import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public String sendMessage(String msg) {
        try {
            out.println(msg);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return null;
        }
    }

    public void stopConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 8888);

        while (true) {
            System.out.print("Me: ");
            String message = scanner.nextLine();
            String response = client.sendMessage(message);
            if (".".equalsIgnoreCase(message)) {
                System.out.println("Closing connection...");
                client.stopConnection();
                break;
            }
            System.out.println("Server: " + response);
        }

        scanner.close();
    }
}
