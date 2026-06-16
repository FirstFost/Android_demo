package com.example.myapplication;

public class AppModel {

    public enum Type { SQUARE, RECT }

    private String name;
    private int iconResId;
    private Type type;

    public AppModel(String name, int iconResId, Type type) {
        this.name = name;
        this.iconResId = iconResId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Type getType() {
        return type;
    }
}
