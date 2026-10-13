package com.example.labsapp.logic;

public class NormalPerson implements Person {

    @Override
    public PersonRole getRole() {
        return PersonRole.NORMAL;
    }

    @Override
    public boolean isStatementTrue(boolean statement) {
        return statement;
    }
}