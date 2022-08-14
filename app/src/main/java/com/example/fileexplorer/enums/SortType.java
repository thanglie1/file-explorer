package com.example.fileexplorer.enums;

import java.util.HashMap;
import java.util.Map;

public enum SortType {
    NAME(0),
    DATE(1),
    TYPE(2),
    SIZE(3);

    private int value;
    private static Map map = new HashMap<>();

    private SortType(int value) {
        this.value = value;
    }

    static {
        for (SortType pageType : SortType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static SortType valueOf(int pageType) {
        return (SortType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
