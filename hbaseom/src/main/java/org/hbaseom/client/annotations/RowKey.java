package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines major field for composite or single row key in HBase
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RowKey {
    String[] composion() default { "String", "Long" };

    int value() default 0;

    boolean reverse() default false;

    int size() default 0;

    boolean nullable() default false;
}
