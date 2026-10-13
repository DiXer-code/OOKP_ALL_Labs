package com.example.labsapp.logic;

public enum PersonRole {
    KNIGHT("Лицар"),
    LIAR("Брехун"),
    NORMAL("Звичайна людина");

    private final String displayName;

    PersonRole(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}