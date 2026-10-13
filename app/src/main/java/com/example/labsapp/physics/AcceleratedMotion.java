package com.example.labsapp.physics;

public class AcceleratedMotion implements PhysicsTask {

    private final double v0;
    private final double a;
    private final double t;

    public AcceleratedMotion(double v0, double a, double t) {
        this.v0 = v0;
        this.a = a;
        this.t = t;
    }

    @Override
    public String getDescription() {
        return "Рівноприскорений рух (v = v0 + a·t)";
    }

    @Override
    public double calculate() {
        return v0 + a * t;
    }
}




