package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines multi-columns in column family, should be used on map
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HMultiColumnFamily {

    String family();

    boolean preDefined() default false;
}
