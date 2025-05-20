package io.goorm.domain;

public enum MemberRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    public String getValue() {
        return value;
    }

    MemberRole(String value) {
        this.value = value;
    }
}
