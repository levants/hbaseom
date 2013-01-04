package org.hbaseom.client.meta.cache;

import static org.hbaseom.client.meta.reflect.Reflector.checkKey;
import static org.hbaseom.client.meta.reflect.Reflector.chekOnFamilySide;
import static org.hbaseom.client.meta.reflect.Reflector.getColumnMethods;
import static org.hbaseom.client.meta.reflect.Reflector.getHColumnFields;
import static org.hbaseom.client.meta.reflect.Reflector.getKeyField;
import static org.hbaseom.client.meta.reflect.Reflector.getKeyFieldByJoinColumns;
import static org.hbaseom.client.meta.reflect.Reflector.getQualifierMethods;
import static org.hbaseom.client.meta.reflect.Reflector.getReflect;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.hbaseom.client.HBaseOM;
import org.hbaseom.client.annotations.HTable;
import org.hbaseom.client.meta.EntityDescriptor;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.translators.util.KeySize;

/**
 * Loads meta information about entities in cache to cache
 * 
 * @author levan
 * 
 */
public final class EntityLoader {

	private EntityLoader() {
		throw new AssertionError("Can not initialize object");
	}

	/**
	 * Loads entity meta information ({@link MetaEntity}) in cache as
	 * ConcurrentMap<{@link Class},{@link MetaEntity}>
	 * 
	 * @param descriptor
	 * @param hBaseOM
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public static void loadEntity(EntityDescriptor descriptor, HBaseOM hBaseOM) {
		Class<?> entityClass = descriptor.getEntityClass();
		MetaEntity metaEntity = new MetaEntity();
		metaEntity.setTableName(entityClass.getAnnotation(HTable.class).name());
		metaEntity.setTableNameBt(metaEntity.getTableName().getBytes());
		metaEntity.setKeyFields(getKeyField(entityClass));
		metaEntity.setFieldMethods(getReflect(entityClass));
		metaEntity.setQualifierMethods(getQualifierMethods(metaEntity
				.getFieldMethods()));
		metaEntity.setColumnMethods(getColumnMethods(metaEntity
				.getFieldMethods()));
		metaEntity.setEntityClass(entityClass);
		metaEntity.sethJoinColumnMethods(getKeyFieldByJoinColumns(entityClass));
		metaEntity.setHasFamilySideJoin(chekOnFamilySide(metaEntity));
		metaEntity.sethColumnFields(getHColumnFields(entityClass));
		checkKey(metaEntity);

		if (descriptor.getMaxVersions() > 0) {
			metaEntity.setMaxVersions(descriptor.getMaxVersions());
		}
		if (descriptor.getGetMaxVersions() > 0) {
			metaEntity.setGetMaxVersions(descriptor.getGetMaxVersions());
		}
		if (descriptor.getScanMaxVersions() > 0) {
			metaEntity.setScanMaxVersions(descriptor.getScanMaxVersions());
		}
		if (descriptor.getScanCache() > 0) {
			metaEntity.setScanCache(descriptor.getScanCache());
		}

		KeySize keySize = new KeySize(metaEntity);
		keySize.setKeySize();

		hBaseOM.getMetaEntities().put(entityClass, metaEntity);
	}

	/**
	 * Loads {@link Collection} of entity meta information
	 * {@link EntityDescriptor} container classes in cache
	 * 
	 * @param entityClasses
	 * @param hBaseOM
	 */
	public static void setEntities(Collection<EntityDescriptor> entityClasses,
			HBaseOM hBaseOM) {
		Map<String, Class<?>> entites = hBaseOM.getEntities();
		Class<?> entityClass;
		for (EntityDescriptor descriptor : entityClasses) {
			entityClass = descriptor.getEntityClass();
			entites.put(entityClass.getName(), entityClass);
			entites.put(entityClass.getSimpleName(), entityClass);
		}
	}
}
