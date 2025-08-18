package com.example.ex4springgaldrimer1.enums;

public enum QuestionType {
    PRODUCT("Product Knowledge"),
    STORE("Store Information"),
    GENERAL("General Knowledge");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}