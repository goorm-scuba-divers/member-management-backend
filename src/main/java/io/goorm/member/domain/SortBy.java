package io.goorm.member.domain;

public enum SortBy {
    CREATED_AT("createdAt"),
    MODIFIED_AT("modifiedAt"),
    USERNAME("username");

    private final String value;

    public String getValue() {
        return value;
    }

    SortBy(String value) {
        this.value = value;
    }
}
