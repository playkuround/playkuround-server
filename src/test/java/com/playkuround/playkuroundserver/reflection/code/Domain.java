package com.playkuround.playkuroundserver.reflection.code;

public class Domain {

    private String name;
    private int age;

    private Domain() {
    }

    private Domain(String name) {
        this.name = name;
    }

    public Domain(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
