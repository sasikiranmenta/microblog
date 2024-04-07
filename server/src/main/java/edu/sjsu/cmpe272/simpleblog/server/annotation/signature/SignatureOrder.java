package edu.sjsu.cmpe272.simpleblog.server.annotation.signature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SignatureOrder {
    int order() default 0;

    boolean ignoreIfEmpty() default false;
}
