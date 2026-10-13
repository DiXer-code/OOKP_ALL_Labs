package com.example.labsapp.network;

import java.util.ArrayList;
import java.util.List;

public class NotesManager {

    private final List<String> notes = new ArrayList<>();
    private static final int MAX_NOTES = 6;

    public synchronized String addNote(String text) {
        if (notes.size() >= MAX_NOTES) {
            return "Ліміт 5 нотаток. Видаліть одну.";
        }
        notes.add(text);
        return "Нотатку додано.";
    }

    public synchronized String getAll() {
        if (notes.isEmpty()) {
            return "Нотаток немає.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            // Замінюємо \n на <br>, щоб клієнт отримав усе одним рядком
            sb.append("<b>").append(i).append("</b>: ").append(notes.get(i)).append("<br>");
        }
        return sb.toString();
    }

    public synchronized String deleteNote(int index) {
        if (index < 0 || index >= notes.size()) {
            return "Невірний індекс.";
        }
        notes.remove(index); // Видалення автоматично зсуне всі інші індекси догори!
        return "Нотатку видалено.";
    }

    public synchronized String clearAll() {
        notes.clear();
        return "Усі нотатки видалено.";
    }
}