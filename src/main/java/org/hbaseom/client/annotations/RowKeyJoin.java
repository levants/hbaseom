package org.hbaseom.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When one entity is joined with One To One join to other (distinct!) entity by
 * keys (HBase row keys)
 * 
 * @author levan
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RowKeyJoin {

	Class<?> target();

	HFetchType fetch() default HFetchType.LAZY;

	HCascadeType[] cascade() default { HCascadeType.ALL };
}
