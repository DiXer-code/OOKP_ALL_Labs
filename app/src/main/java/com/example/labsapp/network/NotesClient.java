package com.example.labsapp.network;

import java.io.*;
import java.net.Socket;

public class NotesClient {

    private final String host;
    private final int port;

    public NotesClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String send(String message) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true)) {

            out.println(message);
            return in.readLine();

        } catch (Exception e) {
            return "Помилка з'єднання.";
        }
    }
}