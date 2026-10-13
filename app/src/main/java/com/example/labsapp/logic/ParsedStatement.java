package com.example.labsapp.logic;

public class ParsedStatement {

    public static final int SELF = -1;

    private final StatementType type;
    private final int targetPersonIndex;

    public ParsedStatement(StatementType type, int targetPersonIndex) {
        this.type = type;
        this.targetPersonIndex = targetPersonIndex;
    }

    public StatementType getType() {
        return type;
    }

    public int getTargetPersonIndex() {
        return targetPersonIndex;
    }
}