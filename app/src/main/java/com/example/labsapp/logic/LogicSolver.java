package com.example.labsapp.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogicSolver {

    public String solve(String aText, String bText, String cText) {
        aText = safeTrim(aText);
        bText = safeTrim(bText);
        cText = safeTrim(cText);

        if (aText.isEmpty() || bText.isEmpty() || cText.isEmpty()) {
            return "❌ Помилка: введіть коректні дані для всіх трьох осіб.";
        }

        ParseResult parsedA = parseStatement(aText, 0);
        ParseResult parsedB = parseStatement(bText, 1);
        ParseResult parsedC = parseStatement(cText, 2);

        if (!parsedA.valid || !parsedB.valid || !parsedC.valid) {
            StringBuilder error = new StringBuilder("❌ Помилка: введіть коректні висловлювання.\n\n");
            error.append("Підтримувані формати прикладів:\n");
            error.append("• Я звичайна людина\n");
            error.append("• Я не є звичайною людиною\n");
            error.append("• Я лицар\n");
            error.append("• Я не лицар\n");
            error.append("• Я брехун\n");
            error.append("• Я не брехун\n");
            error.append("• Це правда\n");
            error.append("• Це неправда\n");
            error.append("• А каже правду\n");
            error.append("• С бреше");
            return error.toString();
        }

        ParsedStatement[] statements = new ParsedStatement[]{
                parsedA.statement,
                parsedB.statement,
                parsedC.statement
        };

        PersonRole[] allRoles = {PersonRole.KNIGHT, PersonRole.LIAR, PersonRole.NORMAL};
        List<PersonRole[]> validSolutions = new ArrayList<>();

        for (PersonRole[] roles : getPermutations(allRoles)) {
            if (isAssignmentValid(statements, roles)) {
                validSolutions.add(roles);
            }
        }

        if (validSolutions.isEmpty()) {
            return "❌ Помилка: для введених даних немає жодного коректного розв'язку.";
        }

        if (validSolutions.size() > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("⚠️ Для введених даних існує кілька можливих розв'язків.\n");
            sb.append("Недостатньо інформації для однозначної відповіді.\n\n");

            for (int i = 0; i < validSolutions.size(); i++) {
                PersonRole[] roles = validSolutions.get(i);
                sb.append("Варіант ").append(i + 1).append(":\n");
                sb.append("A — ").append(roles[0]).append("\n");
                sb.append("B — ").append(roles[1]).append("\n");
                sb.append("C — ").append(roles[2]).append("\n\n");
            }

            return sb.toString().trim();
        }

        PersonRole[] result = validSolutions.get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("✅ Знайдено єдиний правильний розв'язок.\n\n");
        sb.append("A — ").append(result[0]).append("\n");
        sb.append("B — ").append(result[1]).append("\n");
        sb.append("C — ").append(result[2]).append("\n\n");

        sb.append("Логічний висновок:\n");
        sb.append("• Кожна роль використана рівно один раз.\n");
        sb.append("• Лицар завжди каже правду.\n");
        sb.append("• Брехун завжди каже неправду.\n");
        sb.append("• Звичайна людина може сказати як правду, так і неправду.\n");

        return sb.toString();
    }

    private boolean isAssignmentValid(ParsedStatement[] statements, PersonRole[] roles) {
        for (int i = 0; i < 3; i++) {
            Boolean factualTruth = evaluateFactualTruth(i, statements, roles, new boolean[3]);
            if (factualTruth == null) {
                return false;
            }

            PersonRole speakerRole = roles[i];

            if (speakerRole == PersonRole.KNIGHT && !factualTruth) {
                return false;
            }

            if (speakerRole == PersonRole.LIAR && factualTruth) {
                return false;
            }
        }
        return true;
    }

    private Boolean evaluateFactualTruth(int speakerIndex,
                                         ParsedStatement[] statements,
                                         PersonRole[] roles,
                                         boolean[] visiting) {

        ParsedStatement statement = statements[speakerIndex];
        int target = statement.getTargetPersonIndex() == ParsedStatement.SELF
                ? speakerIndex
                : statement.getTargetPersonIndex();

        switch (statement.getType()) {
            case IS_NORMAL:
                return roles[target] == PersonRole.NORMAL;

            case IS_NOT_NORMAL:
                return roles[target] != PersonRole.NORMAL;

            case IS_KNIGHT:
                return roles[target] == PersonRole.KNIGHT;

            case IS_NOT_KNIGHT:
                return roles[target] != PersonRole.KNIGHT;

            case IS_LIAR:
                return roles[target] == PersonRole.LIAR;

            case IS_NOT_LIAR:
                return roles[target] != PersonRole.LIAR;

            case STATEMENT_TRUE:
                if (visiting[target]) {
                    return null;
                }
                visiting[target] = true;
                Boolean nestedTrue = evaluateFactualTruth(target, statements, roles, visiting);
                visiting[target] = false;
                return nestedTrue;

            case STATEMENT_FALSE:
                if (visiting[target]) {
                    return null;
                }
                visiting[target] = true;
                Boolean nestedFalse = evaluateFactualTruth(target, statements, roles, visiting);
                visiting[target] = false;
                return nestedFalse == null ? null : !nestedFalse;

            default:
                return null;
        }
    }

    private ParseResult parseStatement(String originalText, int speakerIndex) {
        String text = normalize(originalText);

        if (text.isEmpty()) {
            return ParseResult.invalid();
        }

        int target = detectTargetPerson(text, speakerIndex);

        // правда / неправда
        if (containsAny(text, "це правда", "це істина", "це вірно", "це правдиве")) {
            int ref = detectReferencePerson(text, speakerIndex);
            return ref == Integer.MIN_VALUE
                    ? ParseResult.invalid()
                    : ParseResult.valid(new ParsedStatement(StatementType.STATEMENT_TRUE, ref));
        }

        if (containsAny(text, "це неправда", "це хиба", "це невірно", "це неправдиве")) {
            int ref = detectReferencePerson(text, speakerIndex);
            return ref == Integer.MIN_VALUE
                    ? ParseResult.invalid()
                    : ParseResult.valid(new ParsedStatement(StatementType.STATEMENT_FALSE, ref));
        }

        if (containsAny(text, "каже правду", "говорить правду", "висловлювання правдиве", "правдиве висловлювання")) {
            int ref = detectReferencePerson(text, speakerIndex);
            return ref == Integer.MIN_VALUE
                    ? ParseResult.invalid()
                    : ParseResult.valid(new ParsedStatement(StatementType.STATEMENT_TRUE, ref));
        }

        if (containsAny(text, "бреше", "каже неправду", "говорить неправду", "висловлювання неправдиве")) {
            int ref = detectReferencePerson(text, speakerIndex);
            return ref == Integer.MIN_VALUE
                    ? ParseResult.invalid()
                    : ParseResult.valid(new ParsedStatement(StatementType.STATEMENT_FALSE, ref));
        }

        // ролі
        if (text.contains("не звичайн")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_NOT_NORMAL, target));
        }

        if (text.contains("звичайн")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_NORMAL, target));
        }

        if (text.contains("не лицар")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_NOT_KNIGHT, target));
        }

        if (text.contains("лицар")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_KNIGHT, target));
        }

        if (text.contains("не брехун")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_NOT_LIAR, target));
        }

        if (text.contains("брехун")) {
            return ParseResult.valid(new ParsedStatement(StatementType.IS_LIAR, target));
        }

        return ParseResult.invalid();
    }

    private int detectTargetPerson(String text, int speakerIndex) {
        if (containsAny(text, "я ", " я", "я є", "я не", "i am", "i'm")) {
            return ParsedStatement.SELF;
        }

        if (mentionsPersonA(text)) return 0;
        if (mentionsPersonB(text)) return 1;
        if (mentionsPersonC(text)) return 2;

        return ParsedStatement.SELF;
    }

    private int detectReferencePerson(String text, int speakerIndex) {
        if (mentionsPersonA(text)) return 0;
        if (mentionsPersonB(text)) return 1;
        if (mentionsPersonC(text)) return 2;

        // Для фраз типу "Це правда" без явного імені:
        // вважаємо, що людина посилається на попереднє висловлювання.
        if (speakerIndex > 0) {
            return speakerIndex - 1;
        }

        return Integer.MIN_VALUE;
    }

    private boolean mentionsPersonA(String text) {
        return containsAny(text,
                "особа а", "персона а", "а каже", "а бреше", "а лицар", "а звичайна", "а не");
    }

    private boolean mentionsPersonB(String text) {
        return containsAny(text,
                "особа в", "персона в", "в каже", "в бреше", "в лицар", "в звичайна", "в не",
                "особа b", "персона b", "b says", "b is");
    }

    private boolean mentionsPersonC(String text) {
        return containsAny(text,
                "особа с", "персона с", "с каже", "с бреше", "с лицар", "с звичайна", "с не",
                "особа c", "персона c", "c says", "c is");
    }

    private String normalize(String text) {
        String result = safeTrim(text).toLowerCase(Locale.ROOT);
        result = result.replace("’", "'")
                .replace("`", "'")
                .replace("ё", "е")
                .replace("ґ", "г")
                .replaceAll("[\"“”«»]", "")
                .replaceAll("[.!?,;:()]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return result;
    }

    private boolean containsAny(String text, String... variants) {
        for (String variant : variants) {
            if (text.contains(variant)) {
                return true;
            }
        }
        return false;
    }

    private String safeTrim(String text) {
        return text == null ? "" : text.trim();
    }

    private List<PersonRole[]> getPermutations(PersonRole[] roles) {
        List<PersonRole[]> list = new ArrayList<>();
        list.add(new PersonRole[]{roles[0], roles[1], roles[2]});
        list.add(new PersonRole[]{roles[0], roles[2], roles[1]});
        list.add(new PersonRole[]{roles[1], roles[0], roles[2]});
        list.add(new PersonRole[]{roles[1], roles[2], roles[0]});
        list.add(new PersonRole[]{roles[2], roles[0], roles[1]});
        list.add(new PersonRole[]{roles[2], roles[1], roles[0]});
        return list;
    }

    private static class ParseResult {
        private final boolean valid;
        private final ParsedStatement statement;

        private ParseResult(boolean valid, ParsedStatement statement) {
            this.valid = valid;
            this.statement = statement;
        }

        static ParseResult valid(ParsedStatement statement) {
            return new ParseResult(true, statement);
        }

        static ParseResult invalid() {
            return new ParseResult(false, null);
        }
    }
}