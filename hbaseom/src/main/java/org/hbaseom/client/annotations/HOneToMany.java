package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines client side joins in HBase for GET PUT SCAN use
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HOneToMany {

    Class<?> target();

    HJoinColumn[] joinColumns() default {};

    String hJoinTable() default "";

    String hJoinEntity() default "";

    String[] firstMatch() default {};

    HFetchType fetch() default HFetchType.LAZY;

    HCascadeType[] cascade() default { HCascadeType.ALL };

    boolean isColumnSide() default false;
}
