package me.boblocker.core.annotation;

import io.javalin.plugin.openapi.annotations.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String path() default "/";
    HttpMethod method() default HttpMethod.GET;
}
