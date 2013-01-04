package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HOneToOne {

    Class<?> target();

    HJoinColumn[] joinColumns() default {};

    HFetchType fetch() default HFetchType.LAZY;

    HCascadeType[] cascade() default { HCascadeType.ALL };
}
