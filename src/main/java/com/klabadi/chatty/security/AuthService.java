package com.klabadi.chatty.security;

import com.klabadi.chatty.exception.AuthException;
import com.klabadi.chatty.model.User;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface AuthService {
    User authenticate(BufferedReader input, PrintWriter output) throws AuthException;
}