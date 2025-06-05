package io.goorm.config.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
public enum EnvironmentConstants {
    PRODUCTION(Constants.PROD),
    LOCAL(Constants.LOCAL),
    ;
    private String value;

    public String getValue() {
        return value;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Constants {
        public static final String PROD = "prod";
        public static final String LOCAL = "local";
    }


}
