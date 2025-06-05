package io.goorm.config.environment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UrlConstants {
    PROD_SERVER_URL("https://dkloepar8paxh.cloudfront.net"),
    LOCAL_SERVER_URL("http://localhost:8080");

    private String value;

    public String getValue() {
        return value;
    }
}
