package com.qa.baseApi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EndPoints {
    WEATHER("/weather");

    private final String value;

    EndPoints(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
