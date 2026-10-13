package com.example.labsapp.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NotesServer implements Runnable {

    private final int port;
    private final NotesManager manager = new NotesManager();

    public NotesServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server started on port " + port);

            while (true) {
                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(
                        client.getOutputStream(), true);

                String request = in.readLine();

                if (request != null) {
                    if (request.startsWith("ADD:")) {
                        String note = request.substring(4);
                        // Делегуємо роботу менеджеру
                        out.println(manager.addNote(note));

                    } else if (request.equals("GET")) {
                        // Отримуємо всі нотатки одним рядком з <br>
                        out.println(manager.getAll());

                    } else if (request.startsWith("DELETE:")) {
                        try {
                            int index = Integer.parseInt(request.substring(7));
                            out.println(manager.deleteNote(index));
                        } catch (NumberFormatException e) {
                            out.println("Помилка: введіть коректну цифру");
                        }

                    } else if (request.equals("CLEAR")) {
                        out.println(manager.clearAll());
                    }
                }

                client.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
