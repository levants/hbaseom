package org.hbaseom.client.translators.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hbaseom.client.HBaseOM;
import org.hbaseom.client.annotations.HOneToMany;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.meta.reflect.FieldMethod;
import org.hbaseom.client.meta.reflect.Reflector;

/**
 * To translate from {@link Byte} array to entity {@link Object}s for OneTo
 * family side joins
 * 
 * @author Levan
 * 
 */
public class JoinDeserializer {

	private HBaseOM hBaseOM;

	private int cursor;

	public JoinDeserializer(HBaseOM hBaseOM) {
		this.hBaseOM = hBaseOM;
	}

	private Map<Field, FieldMethod> getFieldMethods(Class<?> entityClass) {
		MetaEntity metaEntity = hBaseOM.getMetaEntities().get(entityClass);
		return metaEntity.getFieldMethods();
	}

	public Set<Field> getFields(Map<Field, FieldMethod> fieldMethods) {
		return fieldMethods.keySet();
	}

	// TODO Change method body
	public Field[] getFields(Class<?> entityClass) {
		Map<Field, FieldMethod> fieldMethods = getFieldMethods(entityClass);
		Set<Field> fields = getFields(fieldMethods);
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Checks if {@link Class} type is {@link Collection}
	 * 
	 * @param type
	 * @return boolean
	 */
	private boolean checkForCollection(Class<?> type) {
		return CollectionTypes.checkAproprietedType(type);
	}

	/**
	 * Checks if {@link Class} type is another entity
	 * 
	 * @param type
	 * @return boolean
	 */
	private boolean checkForEntity(Class<?> type) {
		return hBaseOM.getMetaEntities().containsKey(type);
	}

	/**
	 * Fills {@link Collection} of entities for {@link HOneToMany} family side
	 * join
	 * 
	 * @param data
	 * @param type
	 * @param entityClass
	 * @return {@link Collection}
	 */
	private Collection<?> fillCollection(byte[] data, Class<?> type,
			Class<?> entityClass) {
		int size = data[cursor];
		Collection<Object> entities = CollectionTypes
				.getAproprietedInstance(type);
		if (size > 0) {
			for (int i = 0; i < size; i++, cursor++) {
				Object entity = translateToEntity(entityClass, data);
				if (entity != null) {
					entities.add(entity);
				}
			}
		}

		return entities;
	}

	/**
	 * Fills passed {@link Field} in entity with data red from {@link Byte}
	 * array from cursor to size by protocol
	 * 
	 * @param data
	 * @param cursor
	 * @param field
	 * @param entity
	 * @return int
	 */
	private void fillData(byte[] data, Field field, Object entity) {
		Class<?> type = field.getType();
		Object value;
		if (checkForEntity(type)) {
			value = translateToEntity(type, data);
			Reflector.setValue(entity, field, value);
		} else if (checkForCollection(type)) {
			Class<?> collectionEntity = field.getAnnotation(HOneToMany.class)
					.target();
			value = fillCollection(data, type, collectionEntity);
			Reflector.setValue(entity, field, value);
		} else if (data[cursor] == 1) {

			String typeName = type.getSimpleName();
			int size = Translator.getSize(typeName);
			byte[] fieldData = new byte[size];
			System.arraycopy(data, 0, fieldData, cursor, size);
			value = Translator.getValue(fieldData, typeName);
			Reflector.setValue(entity, field, value);
			cursor += size;
		}
	}

	/**
	 * Creates entity or {@link Collection} of entities from {@link Byte} array
	 * for OneTo family size joins
	 * 
	 * @param entityClass
	 * @param data
	 * @param cursor
	 */
	public Object translateToEntity(Class<?> entityClass, byte[] data) {
		// Map<Field, FieldMethod> fieldMethods = getFieldMethods(entityClass);
		if (data[cursor] == 0) {
			return null;
		}
		Field[] fields = getFields(entityClass);
		int length = fields.length;
		Field current;
		Object entity = Reflector.instantiate(entityClass);
		for (int i = 0; i < length; i++) {
			current = fields[i];
			fillData(data, current, entity);
		}

		return entity;
	}
}
