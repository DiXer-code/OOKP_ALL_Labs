package com.example.labsapp.logic;

public class Knight implements Person {

    @Override
    public PersonRole getRole() {
        return PersonRole.KNIGHT;
    }

    @Override
    public boolean isStatementTrue(boolean statement) {
        return statement;
    }
}
//Я брехун
//Це неправда
//Я звичайна людина