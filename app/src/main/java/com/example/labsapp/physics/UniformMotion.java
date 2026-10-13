package com.example.labsapp.physics;

public class UniformMotion implements PhysicsTask {

    private final double distance;
    private final double time;

    public UniformMotion(double distance, double time) {
        this.distance = distance;
        this.time = time;
    }

    @Override
    public String getDescription() {
        return "Рівномірний рух (v = s / t)";
    }

    @Override
    public double calculate() {
        return distance / time;
    }
}


