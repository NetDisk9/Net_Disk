package com.net.common.enums;

public enum RoleEnum {
    USER("user",1292314917579428904L),
    ADMIN("administrator",1481929310323883495L),
    SUPER("super",1234481759230567269L);



    String name;
    Long roleId;
    RoleEnum(String name, Long roleId){
        this.name=name;
        this.roleId=roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
