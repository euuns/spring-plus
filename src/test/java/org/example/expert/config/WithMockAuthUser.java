package org.example.expert.config;

import org.example.expert.domain.user.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMockAuthUser {

    long userId();

    String email();

    UserRole role() default UserRole.ROLE_USER;

    String nickname();

}