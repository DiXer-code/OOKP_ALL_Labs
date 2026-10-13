package com.example.labsapp.logic;

public class PersonFactory {

    public static Person create(PersonRole role) {
        switch (role) {
            case KNIGHT: return new Knight();
            case LIAR: return new Liar();
            case NORMAL: return new NormalPerson();
            default: throw new IllegalArgumentException();
        }
    }
}