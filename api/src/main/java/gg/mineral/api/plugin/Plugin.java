package gg.mineral.api.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    String name();

    String author();

    String version() default "";
}
