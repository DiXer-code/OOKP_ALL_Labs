package com.example.labsapp.strategy;

public class FractionDivision implements DivisionStrategy {
    @Override
    public String divide(String a, String b) {
        try {
            // Парсимо "1/2" -> [1, 2]
            int[] f1 = parseFraction(a);
            int[] f2 = parseFraction(b);

            if (f2[0] == 0) return "Помилка: ділення на 0";

            // Ділення дробів: перевертаємо другий дріб і множимо
            // (a/b) / (c/d) = (a*d) / (b*c)
            int numerator = f1[0] * f2[1];
            int denominator = f1[1] * f2[0];

            return simplify(numerator, denominator);
        } catch (Exception e) {
            return "Формат має бути: 1/2";
        }
    }

    private int[] parseFraction(String s) {
        if (!s.contains("/")) return new int[]{Integer.parseInt(s), 1};
        String[] parts = s.split("/");
        return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
    }

    // Скорочення дробу (НСД)
    private String simplify(int num, int den) {
        int gcd = gcd(Math.abs(num), Math.abs(den));
        return (num / gcd) + "/" + (den / gcd);
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}