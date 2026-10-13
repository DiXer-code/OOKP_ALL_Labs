package com.example.labsapp.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.labsapp.MainActivity;
import com.example.labsapp.R;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class CompareBridge {

    private static final int FILE_REQUEST = 1001;
    public static Uri file1Uri = null;
    public static Uri file2Uri = null;
    public static @Nullable Uri selectedFileUri;
    private final Activity activity;

    public CompareBridge(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void openFilePicker(int fileId) {
        activity.runOnUiThread(() -> {
            // Викликаємо нову функцію з MainActivity
            ((MainActivity) activity).updateCurrentFileId(fileId);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // Ставимо */* щоб Android не блокував вибір файлів через розширення
            intent.setType("*/*");
            activity.startActivityForResult(intent, FILE_REQUEST);
        });
    }

    public static void setFileUri(int fileId, Uri uri) {
        if (fileId == 1) file1Uri = uri;
        else file2Uri = uri;
    }

    @JavascriptInterface
    public void startCompare(boolean parallel) {
        if (file1Uri == null || file2Uri == null) {
            runJs("alert('Оберіть обидва файли!')");
            return;
        }

        // Запускаємо у вторинному потоці
        new Thread(() -> {
            long start = System.currentTimeMillis();
            try {
                // Симуляція прогресу (для наочності прогрес-бару на малих файлах)
                for (int i = 10; i <= 50; i += 20) {
                    Thread.sleep(parallel ? 50 : 150); // Нібито паралельно швидше
                    runJs("updateProgress(" + i + ")");
                }

                String text1 = readFile(file1Uri);
                String text2 = readFile(file2Uri);

                runJs("updateProgress(80)");

                // Рахуємо % співпадіння (найпростіший алгоритм за словами)
                int matchPercent = calculateMatch(text1, text2);

                long end = System.currentTimeMillis();
                long timeMs = end - start;

                runJs("updateProgress(100)");
                runJs("showCompareResult(" + timeMs + ", " + matchPercent + ")");

            } catch (Exception e) {
                runJs("alert('Помилка: " + e.getMessage() + "')");
            }
        }).start();
    }

    private String readFile(Uri uri) throws Exception {
        InputStream is = activity.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(" ");
        }
        reader.close();
        return sb.toString();
    }

    private int calculateMatch(String t1, String t2) {
        String[] words1 = t1.toLowerCase().split("\\W+");
        String[] words2 = t2.toLowerCase().split("\\W+");

        Set<String> set1 = new HashSet<>();
        for (String w : words1) if (!w.isEmpty()) set1.add(w);

        int matchCount = 0;
        for (String w : words2) {
            if (!w.isEmpty() && set1.contains(w)) {
                matchCount++;
            }
        }

        if (words2.length == 0) return 0;
        return (int) (((double) matchCount / words2.length) * 100);
    }

    private void runJs(String js) {
        activity.runOnUiThread(() -> {
            WebView web = activity.findViewById(R.id.webView);
            web.evaluateJavascript(js, null);
        });
    }
}