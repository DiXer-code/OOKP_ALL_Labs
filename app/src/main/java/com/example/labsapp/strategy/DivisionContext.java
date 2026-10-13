package com.example.labsapp.strategy;

public class DivisionContext {

    private DivisionStrategy strategy;

    public void setStrategy(DivisionStrategy strategy) {
        this.strategy = strategy;
    }

    public String execute(String a, String b) {
        return strategy.divide(a, b);
    }
}
