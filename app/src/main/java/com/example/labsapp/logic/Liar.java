package com.example.labsapp.logic;

public class Liar implements Person {

    @Override
    public PersonRole getRole() {
        return PersonRole.LIAR;
    }

    @Override
    public boolean isStatementTrue(boolean statement) {
        return !statement;
    }
}