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
 * To translate from entity {@link Object}s to {@link Byte} array for OneTo
 * family side joins
 * 
 * @author Levan
 * 
 */
public class JoinSerializer {

	private HBaseOM hBaseOM;

	public JoinSerializer(HBaseOM hBaseOM) {
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
	private byte[] fillFromCollection(Collection<?> values) {
		Class<?> type = values.getClass().getGenericInterfaces()[0].getClass();
		boolean isEntity = checkForEntity(type);
		byte[] data = new byte[0];
		byte[] dataForAdd;
		for (Object value : values) {
			if (isEntity) {
				dataForAdd = translateToData(value);
			} else {
				dataForAdd = Translator.getSupportedType(value);
			}
			BytesApi.add(data, dataForAdd);
		}
		return data;
	}

	private byte[] fillData(Class<?> type, Object value) {
		byte[] data;
		if (checkForEntity(type)) {
			data = translateToData(value);
		} else if (checkForCollection(type)) {
			data = fillFromCollection((Collection<?>) value);
		} else {
			data = Translator.getSupportedType(value);
		}

		return data;
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
	private byte[] fillData(Field field, Object entity) {
		Class<?> type = field.getType();
		Object value = Reflector.getValue(entity, field);
		return fillData(type, value);
	}

	public byte[] translateToData(Object entity) {
		if (entity == null) {

		}
		Class<?> entityClass = entity.getClass();
		Field[] fields = getFields(entityClass);
		int length = fields.length;
		Field current;
		byte[] data = new byte[0];
		for (int i = 0; i < length; i++) {
			current = fields[i];
			byte[] dataForAdd = fillData(current, entity);
			if (dataForAdd != null && dataForAdd.length > 0) {
				data = BytesApi.add(data, dataForAdd);
			}
		}

		return data;
	}
}
