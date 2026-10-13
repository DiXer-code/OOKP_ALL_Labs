package com.example.labsapp.physics;

public class DensityTask implements PhysicsTask {

    private final double mass;
    private final double volume;
    private final double temperature; // °C
    private final double pressure;    // Па (умовно)

    public DensityTask(double mass, double volume,
                       double temperature, double pressure) {
        this.mass = mass;
        this.volume = volume;
        this.temperature = temperature;
        this.pressure = pressure;
    }

    @Override
    public String getDescription() {
        return "Густина з урахуванням температури і тиску";
    }

    @Override
    public double calculate() {
        double baseDensity = mass / volume;

        // спрощена модель:
        double tempFactor = 1 - 0.003 * (temperature - 20);
        double pressureFactor = 1 + 0.000001 * (pressure - 101325);

        return baseDensity * tempFactor * pressureFactor;
    }
}





