package com.wizeup.android.model;

public class Language {
    private String originalName;
    private String key;
    private String nameVer;

    public Language(String originalName, String key, String name) {
        this.originalName = originalName;
        this.key = key;
        this.nameVer = name;
    }

    public String getName() {
        return nameVer;
    }

    public void setName(String name) {
        this.nameVer = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
