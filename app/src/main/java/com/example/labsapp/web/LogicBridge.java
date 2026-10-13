package com.example.labsapp.web;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.labsapp.MainActivity;
import com.example.labsapp.logic.LogicSolver;

public class LogicBridge {

    private final Context context;
    private final LogicSolver solver = new LogicSolver();

    public LogicBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void startVoiceInput() {
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.runOnUiThread(activity::startSpeechToTextFromJs);
        }
    }

    @JavascriptInterface
    public String solve(String a, String b, String c) {
        return solver.solve(a, b, c);
    }

    @JavascriptInterface
    public void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    @JavascriptInterface
    public void sendLab5Result(String result) {
        Log.d("LogicBridge", "Результат з JS: " + result);
    }
}