package com.example.ex4springgaldrimer1.enums;

public enum CommentStatus {
    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;

    CommentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}