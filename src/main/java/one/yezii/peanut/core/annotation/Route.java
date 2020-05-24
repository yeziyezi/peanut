package one.yezii.peanut.core.annotation;


import one.yezii.peanut.core.constant.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String value() default "";

    HttpMethod[] method() default {HttpMethod.GET, HttpMethod.POST};//all uppercase http method
}
