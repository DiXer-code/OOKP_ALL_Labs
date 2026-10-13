package com.example.labsapp.web;

import android.webkit.JavascriptInterface;
import com.example.labsapp.network.NotesClient;

public class NetworkBridge {

    private final NotesClient client =
            new NotesClient("127.0.0.1", 5000);

    @JavascriptInterface
    public String addNote(String text) {
        return client.send("ADD:" + text);
    }

    @JavascriptInterface
    public String getNotes() {
        return client.send("GET");
    }

    @JavascriptInterface
    public String deleteNote(String index) {
        return client.send("DELETE:" + index);
    }

    @JavascriptInterface
    public String clearNotes() {
        return client.send("CLEAR");
    }
}