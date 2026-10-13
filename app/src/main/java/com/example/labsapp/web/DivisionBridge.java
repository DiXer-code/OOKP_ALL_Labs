package com.example.labsapp.web;

import android.webkit.JavascriptInterface;
import com.example.labsapp.strategy.ComplexDivision;
import com.example.labsapp.strategy.DivisionContext;
import com.example.labsapp.strategy.FractionDivision;
import com.example.labsapp.strategy.RealDivision;

public class DivisionBridge {

    @JavascriptInterface
    public String divide(String a, String b, String type) {
        DivisionContext context = new DivisionContext();

        switch (type) {
            case "Звичайні дроби":
                context.setStrategy(new FractionDivision());
                break;
            case "Комплексні числа":
                context.setStrategy(new ComplexDivision());
                break;
            default: // "Дійсні числа"
                context.setStrategy(new RealDivision());
                break;
        }

        return context.execute(a, b);
    }
}