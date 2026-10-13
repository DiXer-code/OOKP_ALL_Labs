package com.example.labsapp.strategy;

public class ComplexDivision implements DivisionStrategy {
    @Override
    public String divide(String aStr, String bStr) {
        try {
            // Парсимо a+bi
            double[] a = parseComplex(aStr); // [real, imag]
            double[] b = parseComplex(bStr);

            double c = b[0];
            double d = b[1];

            if (c*c + d*d == 0) return "Помилка: ділення на 0";

            // Формула ділення: (ac+bd)/(c^2+d^2) + (bc-ad)/(c^2+d^2)i
            double denom = c * c + d * d;
            double realPart = (a[0] * c + a[1] * d) / denom;
            double imagPart = (a[1] * c - a[0] * d) / denom;

            return String.format("%.2f + %.2fi", realPart, imagPart);
        } catch (Exception e) {
            return "Формат: a+bi (напр. 5+2i)";
        }
    }

    // Простий парсер для "5+2i" або "3-4i"
    private double[] parseComplex(String s) {
        s = s.replace("i", "").replace(" ", "");
        int lastSign = Math.max(s.lastIndexOf('+'), s.lastIndexOf('-'));

        if (lastSign <= 0) { // Тільки дійсне число або "5i"
            return new double[]{Double.parseDouble(s), 0};
        }

        double real = Double.parseDouble(s.substring(0, lastSign));
        double imag = Double.parseDouble(s.substring(lastSign));
        return new double[]{real, imag};
    }
}