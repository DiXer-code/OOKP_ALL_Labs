package com.example.labsapp.web;

import android.webkit.JavascriptInterface;
import com.example.labsapp.physics.PhysicsTask;
import com.example.labsapp.physics.PhysicsTaskFactory;

public class PhysicsBridge {

    @JavascriptInterface
    public String calculate(String type, String p1, String p2, String p3, String p4) {
        try {
            double v1 = p1.isEmpty() ? 0.0 : Double.parseDouble(p1);
            double v2 = p2.isEmpty() ? 0.0 : Double.parseDouble(p2);
            double v3 = p3.isEmpty() ? 0.0 : Double.parseDouble(p3);
            double v4 = p4.isEmpty() ? 0.0 : Double.parseDouble(p4);

            PhysicsTask task = PhysicsTaskFactory.createTask(type, v1, v2, v3, v4);

            return String.valueOf(task.calculate());

        } catch (NumberFormatException e) {
            return "Помилка: введіть коректні числа";
        } catch (Exception e) {
            return "Помилка: " + e.getMessage();
        }
    }
}