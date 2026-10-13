package com.example.labsapp.web;

import android.webkit.JavascriptInterface;
import java.util.Locale;

// ==========================================
// 1. IMPLEMENTATION (Матеріали)
// ==========================================
interface RoofingMaterial {
    String getName();
    String getUnit();
    String calculateQuantity(double area);
}

class RollMaterial implements RoofingMaterial {
    public String getName() { return "Рулонний (Руберойд)"; }
    public String getUnit() { return "рулонів"; }
    public String calculateQuantity(double area) {
        int rolls = (int) Math.ceil(area / 10.0);
        return String.valueOf(rolls);
    }
}

class TileMaterial implements RoofingMaterial {
    public String getName() { return "Черепиця"; }
    public String getUnit() { return "штук"; }
    public String calculateQuantity(double area) {
        int pieces = (int) Math.ceil(area * 12.0);
        return String.valueOf(pieces);
    }
}

class SheetMaterial implements RoofingMaterial {
    public String getName() { return "Шифер/Профнастил"; }
    public String getUnit() { return "листів"; }
    public String calculateQuantity(double area) {
        int sheets = (int) Math.ceil(area / 1.5);
        return String.valueOf(sheets);
    }
}

class MasticMaterial implements RoofingMaterial {
    public String getName() { return "Мастика"; }
    public String getUnit() { return "відер"; }
    public String calculateQuantity(double area) {
        int buckets = (int) Math.ceil(area / 5.0);
        return String.valueOf(buckets);
    }
}

class FilmMaterial implements RoofingMaterial {
    public String getName() { return "Плівка"; }
    public String getUnit() { return "упаковок"; }
    public String calculateQuantity(double area) {
        int packs = (int) Math.ceil(area / 50.0);
        return String.valueOf(packs);
    }
}

// ==========================================
// 2. ABSTRACTION (Дахи)
// ==========================================
abstract class Roof {
    protected RoofingMaterial material;

    public Roof(RoofingMaterial material) {
        this.material = material;
    }

    public abstract String calculateReport(double width, double length, double height);

    protected String formatResult(String roofName, double area) {
        String quantity = material.calculateQuantity(area);

        return String.format(Locale.US,
                "📋 <b>Звіт розрахунку:</b><br>" +
                        "🏠 Тип даху: <b>%s</b><br>" +
                        "📐 Площа: <b>%.2f м²</b><br>" +
                        "🧱 Матеріал: <b>%s</b><br>" +
                        "---------------------------<br>" +
                        "✅ <b>Вам потрібно: %s %s</b>",
                roofName, area, material.getName(), quantity, material.getUnit()
        );
    }
}

class SingleSlopeRoof extends Roof {
    public SingleSlopeRoof(RoofingMaterial material) { super(material); }

    @Override
    public String calculateReport(double width, double length, double height) {
        double slopeLength = Math.sqrt(width * width + height * height);
        double area = slopeLength * length;
        return formatResult("Односхилий", area); // Тепер працює
    }
}

class GableRoof extends Roof {
    public GableRoof(RoofingMaterial material) { super(material); }

    @Override
    public String calculateReport(double width, double length, double height) {
        double halfWidth = width / 2.0;
        double slopeLength = Math.sqrt(halfWidth * halfWidth + height * height);
        double area = 2 * (slopeLength * length);
        return formatResult("Двосхилий", area); // Тепер працює
    }
}

class HipRoof extends Roof {
    public HipRoof(RoofingMaterial material) { super(material); }

    @Override
    public String calculateReport(double width, double length, double height) {
        double sideArea1 = 0.5 * width * Math.sqrt(Math.pow(length / 2.0, 2) + Math.pow(height, 2));
        double sideArea2 = 0.5 * length * Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height, 2));
        double area = 2 * (sideArea1 + sideArea2);
        return formatResult("Чотирисхилий", area); // Тепер працює
    }
}

// ==========================================
// 3. BRIDGE INTERFACE (Для WebView)
// ==========================================
public class RoofingBridge {

    @JavascriptInterface
    public String calculateRoof(String roofType, String materialType, String wStr, String lStr, String hStr) {
        try {
            double width = parseInput(wStr);
            double length = parseInput(lStr);
            double height = parseInput(hStr);

            // Вибір матеріалу
            RoofingMaterial material;
            switch (materialType) {
                case "ROLL": material = new RollMaterial(); break;
                case "TILE": material = new TileMaterial(); break;
                case "SHEET": material = new SheetMaterial(); break;
                case "MASTIC": material = new MasticMaterial(); break;
                case "FILM": material = new FilmMaterial(); break;
                default: material = new RollMaterial(); break;
            }

            // Вибір даху
            Roof roof;
            switch (roofType) {
                case "SINGLE": roof = new SingleSlopeRoof(material); break;
                case "GABLE": roof = new GableRoof(material); break;
                case "HIP": roof = new HipRoof(material); break;
                default: roof = new SingleSlopeRoof(material); break;
            }

            return roof.calculateReport(width, length, height);

        } catch (Exception e) {
            return "<span style='color:red'>❌ Помилка: " + e.getMessage() + "</span>";
        }
    }

    private double parseInput(String val) {
        if (val == null || val.trim().isEmpty()) throw new IllegalArgumentException("Пусте поле!");
        double d = Double.parseDouble(val.replace(",", "."));
        if (d <= 0) throw new IllegalArgumentException("Має бути > 0");
        return d;
    }
}