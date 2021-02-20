package com.custom.spring.security.springsecuritybasics.security;

import lombok.Getter;

@Getter
public enum ApplicationUserPermission {

    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write");

    private final String permissions;


    ApplicationUserPermission(String permissions) {
        this.permissions = permissions;
    }


}
