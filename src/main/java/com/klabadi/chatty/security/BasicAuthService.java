package com.klabadi.chatty.security;

import com.klabadi.chatty.exception.AuthException;
import com.klabadi.chatty.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicAuthService implements AuthService {
    private final Map<String, String> userCredentials = new ConcurrentHashMap<>();

    @Override
    public User authenticate(BufferedReader input, PrintWriter output) throws AuthException {
        try {
            output.println("Enter username:");
            String username = input.readLine().trim();

            if (userCredentials.containsKey(username)) {
                return handleExistingUser(username, input, output);
            }
            return handleNewUser(username, input, output);

        } catch (IOException e) {
            throw new AuthException("Authentication failed: " + e.getMessage());
        }
    }

    private User handleExistingUser(String username, BufferedReader input, PrintWriter output)
            throws IOException, AuthException {

        output.println("Enter password:");
        String password = input.readLine();

        if (password != null && password.equals(userCredentials.get(username))) {
            return new User(username);
        }
        throw new AuthException("Invalid credentials");
    }

    private User handleNewUser(String username, BufferedReader input, PrintWriter output)
            throws IOException {

        output.println("Set password for new account:");
        String password = input.readLine();
        userCredentials.put(username, password);
        return new User(username);
    }
}