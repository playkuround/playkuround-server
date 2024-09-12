package com.playkuround.playkuroundserver.learning.reflection;

import lombok.Getter;

@Getter
class Domain {

    private int age;
    private String name;

    private Domain() {
    }

    private Domain(String name) {
        this.name = name;
    }

    public Domain(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
