package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines column family name to save join keys in column side join
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HJoinFamily {

    String name();

    boolean isKeyOnly() default false;
}
