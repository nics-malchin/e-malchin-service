package com.nics.e_malchin_service.Entity;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum LivestockType {
    HORSE(1, "Адуу"),
    CATTLE(2, "Үхэр"),
    CAMEL(3, "Тэмээ"),
    SHEEP(4, "Хонь"),
    GOAT(5, "Ямаа");

    private final int code;
    private final String name;

    LivestockType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LivestockType fromCode(int code) {
        return Arrays.stream(values())
                .filter(t -> t.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid type code: " + code));
    }

    public static List<LivestockType> getAll() {
        return Arrays.asList(LivestockType.values());
    }

}
