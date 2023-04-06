package com.apandroidcenter.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartFileType {
    String type() default "";
    String fileName() default "";
}
