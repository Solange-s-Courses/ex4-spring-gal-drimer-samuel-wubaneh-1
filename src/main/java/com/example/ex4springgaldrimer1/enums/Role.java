package com.example.ex4springgaldrimer1.enums;

public enum Role {
    CUSTOMER("Customer"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Spring Security requires ROLE_ prefix for authorities
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}