package com.custom.spring.security.springsecuritybasics.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.custom.spring.security.springsecuritybasics.security.ApplicationUserPermission.*;

/**
 * we defined User Roles and User Permissions that each role can have
 * we need to define a contract between these two classes and hence we aggregate ApplicationUserPermissions
 * in the ApplicationUserRole class and define permission to each set of roles
 */
public enum ApplicationUserRole {

    ADMIN {
        @Override
        public Set<ApplicationUserPermission> normal() {
            return new HashSet<>();
        }
    },
    STUDENT {
        @Override
        public Set<ApplicationUserPermission> normal() {
            Set<ApplicationUserPermission> adminPermissions = new HashSet<>(
                    Arrays.asList(COURSE_READ, COURSE_WRITE, STUDENT_READ, STUDENT_WRITE));

            return adminPermissions;
        }
    };

    public abstract Set<ApplicationUserPermission> normal();

}
