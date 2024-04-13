package com.net.common.enums;

public enum RoleEnum {
    USER("user"),
    ADMIN("administrator"),
    SUPER("super");



    String name;
    RoleEnum(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
