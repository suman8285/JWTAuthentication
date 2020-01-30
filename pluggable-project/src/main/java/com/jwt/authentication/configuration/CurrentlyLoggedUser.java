package com.jwt.authentication.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

//@Target({ElementType.PARAMETER, ElementType.TYPE})
//@Retention(RetentionPolicy.RUNTIME)
//@AuthenticationPrincipal
public @interface CurrentlyLoggedUser {
}
