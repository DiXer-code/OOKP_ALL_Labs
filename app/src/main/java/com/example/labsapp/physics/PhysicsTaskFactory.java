package com.example.labsapp.physics;

public class PhysicsTaskFactory {

    public static PhysicsTask createTask(String type, double... params) {

        switch (type) {
            case "UNIFORM":
                return new UniformMotion(params[0], params[1]);

            case "ACCELERATED":
                return new AcceleratedMotion(params[0], params[1], params[2]);

            case "DENSITY":
                if (params.length < 4) {
                    throw new IllegalArgumentException(
                            "Для густини потрібно 4 параметри: mass, volume, temperature, pressure"
                    );
                }
                return new DensityTask(
                        params[0],
                        params[1],
                        params[2],
                        params[3]
                );


            default:
                throw new IllegalArgumentException("Невідомий тип задачі");
        }
    }
}




