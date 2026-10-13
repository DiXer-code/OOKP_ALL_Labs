package com.example.labsapp.strategy;

public class RealDivision implements DivisionStrategy {
    @Override
    public String divide(String a, String b) {
        try {
            double num1 = Double.parseDouble(a);
            double num2 = Double.parseDouble(b);
            if (num2 == 0) return "Помилка: ділення на 0";
            return String.valueOf(num1 / num2);
        } catch (NumberFormatException e) {
            return "Помилка: введіть числа";
        }
    }
}

