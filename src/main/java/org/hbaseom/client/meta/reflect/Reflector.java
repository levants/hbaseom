package org.hbaseom.client.meta.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hbaseom.client.annotations.HCascadeType;
import org.hbaseom.client.annotations.HColumnFamily;
import org.hbaseom.client.annotations.HCompound;
import org.hbaseom.client.annotations.HCompoundSize;
import org.hbaseom.client.annotations.HFetchType;
import org.hbaseom.client.annotations.HJoinColumn;
import org.hbaseom.client.annotations.HJoinFamily;
import org.hbaseom.client.annotations.HLob;
import org.hbaseom.client.annotations.HManyToOne;
import org.hbaseom.client.annotations.HMultiColumnFamily;
import org.hbaseom.client.annotations.HOneToMany;
import org.hbaseom.client.annotations.HOneToOne;
import org.hbaseom.client.annotations.RowKey;
import org.hbaseom.client.annotations.RowKeyJoin;
import org.hbaseom.client.meta.EntityContainer;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.translators.RelationContainer;
import org.hbaseom.client.translators.Relations;
import org.hbaseom.client.translators.util.Translator;

/**
 * Reflection resolver class
 * 
 * @author levan
 * 
 */
public class Reflector {

	private EntityContainer entityContainer;

	public Reflector(EntityContainer entityContainer) {
		this.entityContainer = entityContainer;
	}

	/**
	 * One common method for initialize{@link Class} and to avoid
	 * {@link ClassNotFoundException}
	 * 
	 * @param name
	 * @param initialize
	 * @param loader
	 * @return {@link Class}
	 */
	public static Class<?> forName(String name, boolean initialize,
			ClassLoader loader) {

		try {
			return Class.forName(name, initialize, loader);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * One common method for create new instance of {@link Class} and to avoid
	 * {@link InstantiationException} and {@link IllegalAccessException}
	 * 
	 * @param clazz
	 * @return {@link Object}
	 */
	public static <E> E instantiate(Class<E> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * One common method for invoke {@link Method} and to avoid
	 * {@link IllegalAccessException}, {@link IllegalArgumentException} and
	 * {@link InvocationTargetException}
	 * 
	 * @param method
	 * @param entity
	 * @param params
	 * @return {@link Object}
	 */
	public static Object invokeMethod(Method method, Object entity,
			Object... params) {
		try {
			return method.invoke(entity, params);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * One common method for invoke {@link Method} and to avoid
	 * {@link NoSuchFieldException} and {@link SecurityException}
	 * 
	 * @param clazz
	 * @param name
	 * @return {@link Field}
	 */
	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * One common method for get {@link Method} from {@link Class} and to avoid
	 * {@link NoSuchMethodException} and {@link SecurityException}
	 * 
	 * @param clazz
	 * @param name
	 * @param parameterTypes
	 * @return {@link Method}
	 */
	public static Method getMethod(Class<?> clazz, String name,
			Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Checks whether {@link Field} is annotated by {@link RowKey},
	 * {@link HCompound} or {@link HColumnFamily}
	 * 
	 * @param field
	 * @return boolean
	 */
	public static boolean isAnnotated(Field field) {
		return (field.isAnnotationPresent(RowKey.class)
				|| field.isAnnotationPresent(HCompound.class) || field
					.isAnnotationPresent(HColumnFamily.class));
	}

	/**
	 * Generates {@link HColumnField} container class {@link List} for passed
	 * entity {@link Class}
	 * 
	 * @param entityClass
	 * @return List<{@link HColumnField}>
	 */
	public static List<HColumnField> getHColumnFields(Class<?> entityClass) {
		List<HColumnField> hColumnFields = new ArrayList<HColumnField>();
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HColumnFamily.class)
					&& !field.isAnnotationPresent(HMultiColumnFamily.class)) {
				continue;
			}
			HColumnField hColumnField = new HColumnField();
			hColumnField.setField(field);
			hColumnField.setSetter(getSetter(entityClass, field,
					field.getType()));
			hColumnField.setGetter(getGetter(entityClass, field));
			if (field.isAnnotationPresent(HColumnFamily.class)) {
				HColumnFamily hColumnFamily = field
						.getAnnotation(HColumnFamily.class);
				hColumnField.setName(hColumnFamily.family());
				hColumnField.setQualifier(hColumnFamily.qualifier());
			} else {
				HMultiColumnFamily hColumnFamily = field
						.getAnnotation(HMultiColumnFamily.class);
				hColumnField.setName(hColumnFamily.family());
				hColumnField.setMulti(true);
			}
			hColumnFields.add(hColumnField);
		}
		return hColumnFields;
	}

	/**
	 * Sets byte offsets for each {@link KeyField} from passed array
	 * 
	 * @param keyFields
	 * @param index
	 */
	private static void setOffsets(KeyField[] keyFields, int index) {
		KeyField keyField = keyFields[index];
		if (index > 0
				&& keyField.getHCompoundSize().equals(HCompoundSize.DEFINED)
				&& !keyField.hasOffset()) {
			keyField.setReverseOffset(keyField.getSize());
			return;
		}
		for (int i = index - 1; i >= 0; i--) {
			keyField = keyFields[i];
			KeyField nextKeyField = keyFields[i + 1];
			if (keyField.getHCompoundSize().equals(HCompoundSize.DEFINED)
					&& nextKeyField.getReverseOffset() != null
					&& nextKeyField.getReverseOffset() > 0) {
				keyField.setReverseOffset(nextKeyField.getReverseOffset());
			}
		}
	}

	/**
	 * Sets start key end or begin part size
	 * 
	 * @param metaEntity
	 * @param sizeValue
	 */
	private static void setStartKeyLimits(MetaEntity metaEntity, int sizeValue) {
		KeyField[] keyFields = metaEntity.getKeyFields();
		metaEntity.setHCompoundSize(HCompoundSize.DEPENDED);
		int index = keyFields.length - 1;
		setOffsets(keyFields, index);
		if (sizeValue == 0) {
			metaEntity.setStartKeyEnd(true);
		} else if (sizeValue == keyFields.length - 1) {
			metaEntity.setStartKeyBegin(true);
		}
	}

	/**
	 * Defines {@link HCompoundSize} for {@link MetaEntity}
	 * 
	 * @param sized
	 * @param count
	 * @param sizeValue
	 * @param metaEntity
	 */
	private static void setHCompoundSize(boolean sized, int count,
			int sizeValue, MetaEntity metaEntity) {
		KeyField[] keyFields = metaEntity.getKeyFields();
		int index = keyFields.length - 1;
		if (!sized) {
			metaEntity.setHCompoundSize(HCompoundSize.NONE);
			setOffsets(keyFields, index);
		} else {
			if (count == 0) {
				metaEntity.setHCompoundSize(HCompoundSize.DEFINED);
			} else {
				setStartKeyLimits(metaEntity, sizeValue);
			}
		}
	}

	/**
	 * Checks and Fills {@link HCompound} annotated fields from
	 * {@link MetaEntity}
	 * 
	 * @param metaEntity
	 */
	public static void checkKey(MetaEntity metaEntity) {
		KeyField[] keyFields = metaEntity.getKeyFields();
		boolean sized = true;
		int count = 0;
		int sizeValue = -1;
		int previewsOffset;
		KeyField currentKeyField;
		KeyField previewsKeyField;
		for (int i = 0; i < keyFields.length && sized; i++) {
			currentKeyField = keyFields[i];
			sized = count <= 1;
			if (currentKeyField.getHCompoundSize().equals(HCompoundSize.NONE)) {
				count++;
				sizeValue = i;
			}
			if (i == 0) {
				currentKeyField.setOffset(0);
			} else if (keyFields[i - 1].getHCompoundSize().equals(
					HCompoundSize.DEFINED)) {
				previewsKeyField = keyFields[i - 1];
				previewsOffset = previewsKeyField.getOffset();
				if ((i > 1 && previewsOffset > 0) || (i == 1)) {
					currentKeyField.setOffset(previewsOffset
							+ previewsKeyField.getSize());
				}
			}
		}
		setHCompoundSize(sized, count, sizeValue, metaEntity);
	}

	/**
	 * Gets {@link RowKey} annotation from entity {@link Class}
	 * 
	 * @param entityClass
	 * @return {@link RowKey}
	 */
	public static RowKey getRowKey(Class<?> entityClass) {
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(RowKey.class)) {
				continue;
			}
			return field.getAnnotation(RowKey.class);
		}
		return null;
	}

	/**
	 * Generates {@link FieldMethod} map with {@link Field} key respectively by
	 * the reflection
	 * 
	 * @param entityClass
	 * @return Map<{@link Field}, {@link FieldMethod}>
	 */
	public static Map<Field, FieldMethod> getReflect(Class<?> entityClass) {

		Map<Field, FieldMethod> retMap = new HashMap<Field, FieldMethod>();
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HColumnFamily.class)
					&& !field.isAnnotationPresent(HMultiColumnFamily.class)
					&& !field.isAnnotationPresent(RowKey.class)) {
				continue;
			}

			FieldMethod fieldMethod = new FieldMethod();
			fieldMethod.setField(field);
			if (field.isAnnotationPresent(HLob.class)) {
				fieldMethod.setLob(true);
			}
			fieldMethod.setSetter((getSetter(entityClass, field,
					field.getType())));
			fieldMethod.setGetter((getGetter(entityClass, field)));
			if (field.isAnnotationPresent(RowKey.class)) {
				fieldMethod.setRowKey(field.getAnnotation(RowKey.class));
			} else if (field.isAnnotationPresent(HColumnFamily.class)) {
				fieldMethod.setFamily(field.getAnnotation(HColumnFamily.class));
			} else if (field.isAnnotationPresent(HMultiColumnFamily.class)) {
				fieldMethod.setMFamily(field
						.getAnnotation(HMultiColumnFamily.class));
				fieldMethod.setMulti(true);
			}

			retMap.put(field, fieldMethod);
		}

		return retMap;
	}

	/**
	 * Generates {@link FieldMethod} map with {@link String} key as column
	 * qualifier from {@link HColumnFamily} annotation of {@link Field} from
	 * Map<{@link Field}, {@link FieldMethod}> generated by {@link Reflector}
	 * .reflect method
	 * 
	 * @param fieldMethods
	 * @return Map<{@link String}, {@link FieldMethod}>
	 */
	public static Map<String, FieldMethod> getQualifierMethods(
			Map<Field, FieldMethod> fieldMethods) {
		Map<String, FieldMethod> columnMethods = new HashMap<String, FieldMethod>();
		HColumnFamily hColumnFamily;
		FieldMethod fieldMethod;
		for (Map.Entry<Field, FieldMethod> entry : fieldMethods.entrySet()) {
			fieldMethod = entry.getValue();
			hColumnFamily = fieldMethod.getFamily();
			if (hColumnFamily == null) {
				continue;
			}
			columnMethods.put(String.format("%s:%s", hColumnFamily.family(),
					hColumnFamily.qualifier()), fieldMethod);
		}
		return columnMethods;
	}

	/**
	 * Generates List<{@link FieldMethod}> map with {@link String} key as column
	 * family name from {@link HColumnFamily} annotation of {@link Field} from
	 * Map<{@link Field}, {@link FieldMethod}> generated by {@link Reflector}
	 * .reflect method
	 * 
	 * @param fieldMethods
	 * @return Map<{@link String}, List<{@link FieldMethod}>>
	 */
	public static Map<String, List<FieldMethod>> getColumnMethods(
			Map<Field, FieldMethod> fieldMethods) {
		Map<String, List<FieldMethod>> columnMethods = new HashMap<String, List<FieldMethod>>();
		HColumnFamily hColumnFamily;
		FieldMethod fieldMethod;
		String family;
		List<FieldMethod> fMethods;
		for (Map.Entry<Field, FieldMethod> entry : fieldMethods.entrySet()) {
			fieldMethod = entry.getValue();
			hColumnFamily = fieldMethod.getFamily();
			if (hColumnFamily == null) {
				continue;
			}
			family = hColumnFamily.family();
			fMethods = columnMethods.get(family);
			if (fMethods == null) {
				fMethods = new ArrayList<FieldMethod>();
				columnMethods.put(hColumnFamily.family(), fMethods);
			}
			fMethods.add(fieldMethod);
		}
		return columnMethods;
	}

	/**
	 * Gets all {@link HCompound} annotations from entity {@link Class} by
	 * Reflection
	 * 
	 * @param entityClass
	 * @return {@link HCompound}[]
	 */
	public static HCompound[] getHCompounds(Class<?> entityClass) {
		Field[] fields = entityClass.getDeclaredFields();
		int size = 0;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HCompound.class)) {
				continue;
			}
			size++;
		}
		if (size == 0) {
			return null;
		}
		size++;
		HCompound[] hCompounds = new HCompound[size];
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HCompound.class)) {
				continue;
			}
			hCompounds[field.getAnnotation(HCompound.class).value()] = field
					.getAnnotation(HCompound.class);
		}

		return hCompounds;
	}

	/**
	 * Gets {@link KeyField} which is related to {@link RowKey} annotated
	 * {@link Field}
	 * 
	 * @param entityClass
	 * @param rowKey
	 * @param field
	 * @return {@link KeyField}
	 */
	private static KeyField getKeyFieldRowKeyPart(Class<?> entityClass,
			RowKey rowKey, Field field) {
		String[] composition = rowKey.composion();
		KeyField keyField = new KeyField();
		keyField.setField(field);
		keyField.setReversed(rowKey.reverse());
		keyField.setSetter(getSetter(entityClass, field, field.getType()));
		keyField.setGetter(getGetter(entityClass, field));
		keyField.setName(composition[rowKey.value()]);
		keyField.setNullable(rowKey.nullable());

		if (rowKey.size() > 0) {
			keyField.setHCompoundSize(HCompoundSize.DEFINED);
			keyField.setSize(rowKey.size());
		} else {
			Integer size = Translator.getSize(composition[rowKey.value()]);
			if (size != null && size > 0) {
				keyField.setHCompoundSize(HCompoundSize.DEFINED);
				keyField.setSize(size);
			} else {
				keyField.setHCompoundSize(HCompoundSize.NONE);
			}
		}

		return keyField;
	}

	/**
	 * Gets {@link KeyField} which is related to {@link HCompound} annotated
	 * {@link Field}
	 * 
	 * @param rowKey
	 * @param entityClass
	 * @param field
	 * @return {@link KeyField}
	 */
	private static KeyField getKeyFieldHCompoundPart(RowKey rowKey,
			Class<?> entityClass, Field field) {

		String[] composition = rowKey.composion();

		HCompound hCompound = field.getAnnotation(HCompound.class);
		KeyField keyField = new KeyField();
		keyField.setField(field);
		keyField.setReversed(hCompound.reverse());
		keyField.setSetter(getSetter(entityClass, field, field.getType()));
		keyField.setGetter(getGetter(entityClass, field));
		keyField.setName(composition[hCompound.value()]);
		keyField.setNullable(hCompound.nullable());
		if (hCompound.size() > 0) {
			keyField.setHCompoundSize(HCompoundSize.DEFINED);
			keyField.setSize(hCompound.size());
		} else {
			Integer size = Translator.getSize(composition[hCompound.value()]);
			if (size != null && size > 0) {
				keyField.setHCompoundSize(HCompoundSize.DEFINED);
				keyField.setSize(size);
			} else {
				keyField.setHCompoundSize(HCompoundSize.NONE);
			}
		}

		return keyField;
	}

	/**
	 * Defines and calculates {@link KeyField} array for {@link MetaEntity}
	 * cache
	 * 
	 * @param entityClass
	 * @return {@link KeyField}[]
	 */
	public static KeyField[] getKeyField(Class<?> entityClass) {
		KeyField[] keyFields = null;
		Field[] fields = entityClass.getDeclaredFields();
		String[] composition = null;
		RowKey rowKey = null;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(RowKey.class)) {
				continue;
			}
			rowKey = field.getAnnotation(RowKey.class);
			composition = rowKey.composion();
			keyFields = new KeyField[composition.length];
			KeyField keyField = getKeyFieldRowKeyPart(entityClass, rowKey,
					field);
			keyFields[rowKey.value()] = keyField;
			break;
		}
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HCompound.class)) {
				continue;
			}

			HCompound hCompound = field.getAnnotation(HCompound.class);
			KeyField keyField = getKeyFieldHCompoundPart(rowKey, entityClass,
					field);
			keyFields[hCompound.value()] = keyField;
		}

		return keyFields;
	}

	public static boolean getHFetchType(Field field) {
		if (field.isAnnotationPresent(HOneToOne.class)) {
			return field.getAnnotation(HOneToOne.class).fetch()
					.equals(HFetchType.EAGER);
		} else if (field.isAnnotationPresent(HManyToOne.class)) {
			return field.getAnnotation(HManyToOne.class).fetch()
					.equals(HFetchType.EAGER);
		}

		return false;
	}

	public static HCascadeType[] getHCascadeType(Field field) {
		if (field.isAnnotationPresent(HOneToOne.class)) {
			return field.getAnnotation(HOneToOne.class).cascade();
		} else if (field.isAnnotationPresent(HManyToOne.class)) {
			return field.getAnnotation(HManyToOne.class).cascade();
		} else if (field.isAnnotationPresent(HOneToMany.class)) {
			return field.getAnnotation(HOneToMany.class).cascade();
		}

		return null;
	}

	private static HJoinColumnMethod getKeyJoinColumnMethod(
			JoinParameters joinParameters) {
		HJoinColumnMethod hJoinColumnMethod = new HJoinColumnMethod();

		Class<?> entityClass = joinParameters.getEntityClass();
		Class<?> target = joinParameters.getTarget();

		Field[] fields = entityClass.getDeclaredFields();
		Method[] getters = null;
		hJoinColumnMethod.setTarget(target);
		hJoinColumnMethod.setTargetSetter(getSetter(entityClass,
				joinParameters.getField(), target));
		if (target.equals(entityClass)) {
			hJoinColumnMethod.setMatch(true);
		}
		boolean[] reverseds = null;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(RowKey.class)) {
				continue;
			}
			RowKey rowKey = field.getAnnotation(RowKey.class);
			if (rowKey.composion().length == 0) {
				getters = new Method[1];
			} else {
				getters = new Method[rowKey.composion().length];
			}
			if (rowKey.composion().length == 0) {
				reverseds = new boolean[1];
			} else {
				reverseds = new boolean[rowKey.composion().length];
			}
			reverseds[0] = rowKey.reverse();
			getters[0] = getGetter(entityClass, field);
			break;
		}
		for (Field field : fields) {
			if (!field.isAnnotationPresent(HCompound.class)) {
				continue;
			}

			HCompound hCompound = field.getAnnotation(HCompound.class);
			getters[hCompound.value()] = getGetter(entityClass, field);
			reverseds[hCompound.value()] = hCompound.reverse();
		}

		hJoinColumnMethod.setEager(joinParameters.isEager());
		hJoinColumnMethod.setGetters(getters);
		hJoinColumnMethod.setReverses(reverseds);

		return hJoinColumnMethod;
	}

	/**
	 * Creates {@link HJoinColumnMethod} on startup from entity {@link Class}
	 * for save in {@link MetaEntity} and use in join GET PUT DELETE SCAN from
	 * cache
	 * 
	 * @param entityClass
	 * @return List<{@link HJoinColumnMethod}>
	 */
	public static List<HJoinColumnMethod> getKeyFieldByJoinColumns(
			Class<?> entityClass) {

		Field[] fields = entityClass.getDeclaredFields();
		List<HJoinColumnMethod> columnMethods = new ArrayList<HJoinColumnMethod>();
		JoinParameters joinParameters;
		for (Field field : fields) {
			joinParameters = new JoinParameters();
			joinParameters.setField(field);
			joinParameters.setEntityClass(entityClass);
			if (field.isAnnotationPresent(HOneToOne.class)) {
				HOneToOne joinAnnotation = field.getAnnotation(HOneToOne.class);
				joinParameters.setCascadeTypes(joinAnnotation.cascade());
				joinParameters.setEager(joinAnnotation.fetch().equals(
						HFetchType.EAGER));
				joinParameters.setTarget(joinAnnotation.target());
				joinParameters.sethJoinColumns(joinAnnotation.joinColumns());
				joinParameters.setRowKeyJoin(setJoinAnnotations(field));
			} else if (field.isAnnotationPresent(HManyToOne.class)) {
				HManyToOne joinAnnotation = field
						.getAnnotation(HManyToOne.class);
				joinParameters.setManyToOneCheck(true);
				joinParameters.setCascadeTypes(joinAnnotation.cascade());
				joinParameters.setEager(joinAnnotation.fetch().equals(
						HFetchType.EAGER));
				joinParameters.setTarget(joinAnnotation.target());
				joinParameters.sethJoinColumns(joinAnnotation.joinColumns());
				joinParameters.setRowKeyJoin(setJoinAnnotations(field));
			} else if (field.isAnnotationPresent(HOneToMany.class)) {
				HOneToMany joinAnnotation = field
						.getAnnotation(HOneToMany.class);
				joinParameters.setCascadeTypes(joinAnnotation.cascade());
				joinParameters.setEager(joinAnnotation.fetch().equals(
						HFetchType.EAGER));
				joinParameters.setTarget(joinAnnotation.target());
				joinParameters.sethJoinColumns(joinAnnotation.joinColumns());
				joinParameters.setJoinTable(!joinAnnotation.hJoinTable()
						.equals("") ? joinAnnotation.hJoinTable() : null);
				joinParameters.setJoinEntity(!joinAnnotation.hJoinEntity()
						.equals("") ? joinAnnotation.hJoinEntity() : null);
				joinParameters.setCollection(true);
				joinParameters.setCollectionType(field.getType());
				boolean isFamilyJoin = field
						.isAnnotationPresent(HJoinFamily.class);
				joinParameters.setFamilyJoin(isFamilyJoin);
				if (isFamilyJoin) {
					joinParameters.setHJoinFamily(field
							.getAnnotation(HJoinFamily.class));
				}
			} else {
				continue;
			}

			HJoinColumnMethod hJoinColumnMethod = getHJoinColumnMethod(joinParameters);
			hJoinColumnMethod.setManyToOne(joinParameters.isManyToOneCheck());
			columnMethods.add(hJoinColumnMethod);
		}

		return columnMethods;
	}

	/**
	 * Checks if join is familly side
	 * 
	 * @param metaEntity
	 * @return boolean
	 */
	public static boolean chekOnFamilySide(MetaEntity metaEntity) {
		List<HJoinColumnMethod> columnMethods = metaEntity
				.gethJoinColumnMethods();
		if (columnMethods == null) {
			return false;
		}
		for (HJoinColumnMethod columnMethod : columnMethods) {
			if (columnMethod.isFamilySide()) {
				return true;
			}
		}
		return false;
	}

	private static boolean setJoinAnnotations(Field field) {
		if (field.isAnnotationPresent(RowKeyJoin.class)) {
			return true;
		}

		return false;
	}

	/**
	 * Defines {@link HJoinColumnMethod} for row key join from
	 * {@link JoinParameters}
	 * 
	 * @param joinParameters
	 * @return {@link HJoinColumnMethod}
	 */
	private static HJoinColumnMethod getRowKeyHJoinColumnMethod(
			JoinParameters joinParameters) {

		HJoinColumnMethod hJoinColumnMethod = getKeyJoinColumnMethod(joinParameters);
		hJoinColumnMethod.setKeyFields(getKeyField(joinParameters.getTarget()));
		hJoinColumnMethod.setTargetGetter(getGetter(
				joinParameters.getEntityClass(), joinParameters.getField()));
		hJoinColumnMethod.sethCascadeTypes(joinParameters.getCascadeTypes());
		hJoinColumnMethod.setCollection(joinParameters.isCollection());
		hJoinColumnMethod
				.setCollectionClass(joinParameters.getCollectionType());
		return hJoinColumnMethod;
	}

	/**
	 * Defines and fills {@link HJoinColumnMethod} from {@link JoinParameters}
	 * for not row key joins
	 * 
	 * @param joinParameters
	 * @return {@link HJoinColumnMethod}
	 */

	private static HJoinColumnMethod defineHJoinColumnMethod(
			JoinParameters joinParameters) {

		Class<?> entityClass = joinParameters.getEntityClass();
		Class<?> target = joinParameters.getTarget();
		Field field = joinParameters.getField();

		HJoinColumnMethod hJoinColumnMethod = new HJoinColumnMethod();
		hJoinColumnMethod.setKeyFields(getKeyField(target));
		hJoinColumnMethod.setTargetSetter(getSetter(entityClass, field,
				field.getType()));
		hJoinColumnMethod.setTargetGetter(getGetter(entityClass, field));
		hJoinColumnMethod.setTarget(target);
		hJoinColumnMethod.sethCascadeTypes(joinParameters.getCascadeTypes());
		hJoinColumnMethod.setJoinTable(joinParameters.getJoinTable());
		hJoinColumnMethod.setJoinEntity(joinParameters.getJoinEntity());
		hJoinColumnMethod.setCollection(joinParameters.isCollection());
		hJoinColumnMethod
				.setCollectionClass(joinParameters.getCollectionType());
		if (target.equals(entityClass)) {
			hJoinColumnMethod.setMatch(true);
		}
		hJoinColumnMethod.setEager(joinParameters.isEager());
		if (joinParameters.isFamilyJoin()) {
			HJoinFamily hJoinFamily = joinParameters.getHJoinFamily();
			hJoinColumnMethod.setFamilySide(true);
			hJoinColumnMethod.setFamilyName(hJoinFamily.name());
			hJoinColumnMethod.setKeyOnly(hJoinFamily.isKeyOnly());
		}

		return hJoinColumnMethod;
	}

	/**
	 * Defines getters and setters ({@link Method}s in entity) and join column
	 * orders for {@link HJoinColumnMethod}
	 * 
	 * @param joinParameters
	 * @param hJoinColumnMethod
	 */
	private static void defineJoinColumnMethodAndOrder(
			JoinParameters joinParameters, HJoinColumnMethod hJoinColumnMethod) {

		HJoinColumn[] hJoinColumns = joinParameters.gethJoinColumns();
		Class<?> entityClass = joinParameters.getEntityClass();
		boolean manyToOneCheck = joinParameters.isManyToOneCheck();
		Class<?> target = joinParameters.getTarget();

		int joinColumnsLength = hJoinColumns.length;
		boolean[] reverses = new boolean[joinColumnsLength];
		Method[] getters = new Method[joinColumnsLength];
		Method[] setters = new Method[joinColumnsLength];
		boolean isSingle = false;
		Method getter;
		Method setter;
		HJoinColumn joinColumn;
		for (int i = 0; i < joinColumnsLength; i++) {
			joinColumn = hJoinColumns[i];
			getter = getGetter(entityClass, joinColumn.name());
			if (manyToOneCheck) {
				setter = getSetter(entityClass, joinColumn.name(),
						getField(target, joinColumn.name()).getType());
				setters[i] = setter;
			}
			if (joinColumn.single()) {
				Method[] methods1 = { getter };
				hJoinColumnMethod.setGetters(methods1);
				isSingle = true;
				boolean[] reverses1 = { hJoinColumns[i].reverse() };
				hJoinColumnMethod.setReverses(reverses1);
				continue;
			}
			reverses[i] = joinColumn.reverse();
			getters[i] = getter;
		}

		if (!isSingle) {
			hJoinColumnMethod.setGetters(getters);
			if (manyToOneCheck) {
				hJoinColumnMethod.setSetters(setters);
			}
			hJoinColumnMethod.setReverses(reverses);
		}
	}

	/**
	 * Defines {@link HJoinColumnMethod} for {@link JoinParameters}
	 * 
	 * @param joinParameters
	 * @return {@link HJoinColumnMethod}
	 */
	private static HJoinColumnMethod getHJoinColumnMethod(
			JoinParameters joinParameters) {

		HJoinColumnMethod hJoinColumnMethod;
		if (joinParameters.isRowKeyJoin()) {
			hJoinColumnMethod = getRowKeyHJoinColumnMethod(joinParameters);
			return hJoinColumnMethod;
		}
		hJoinColumnMethod = defineHJoinColumnMethod(joinParameters);

		defineJoinColumnMethodAndOrder(joinParameters, hJoinColumnMethod);

		return hJoinColumnMethod;
	}

	/**
	 * Defines {@link KeyField} for {@link HJoinColumnMethod}s
	 * 
	 * @param hJoinColumnMethods
	 * @param eagers
	 * @param lazies
	 * @return List<{@link HJoinColumnMethod}>
	 */
	public static List<HJoinColumnMethod> getKeyFieldByJoinColumns(
			List<HJoinColumnMethod> hJoinColumnMethods, Set<Class<?>> eagers,
			Set<Class<?>> lazies) {
		if (eagers == null && lazies == null) {
			return hJoinColumnMethods;
		}
		List<HJoinColumnMethod> hJoinColumnMethods2 = new ArrayList<HJoinColumnMethod>();
		for (HJoinColumnMethod hJoinColumnMethod : hJoinColumnMethods) {
			if (lazies != null && hJoinColumnMethod.isEager()
					&& lazies.contains(hJoinColumnMethod.getTarget())) {
				continue;
			}
			if (eagers != null && !hJoinColumnMethod.isEager()
					&& eagers.contains(hJoinColumnMethod.getTarget())) {
				hJoinColumnMethods2.add(hJoinColumnMethod);
				continue;
			}

			hJoinColumnMethods2.add(hJoinColumnMethod);
		}

		return hJoinColumnMethods2;

	}

	public static Object[] getValues(Object object, Method... methods) {
		Object[] objects = new Object[methods.length];
		for (int i = 0; i < methods.length; i++) {
			objects[i] = invokeMethod(methods[i], object);
			if (objects[i] == null) {
				return null;
			}
		}

		return objects;
	}

	public static void setValue(Object data, Method method, Object value) {
		invokeMethod(method, data, value);
	}

	private static Method getSetter(Object data, Field field,
			Class<?>... parameters) {
		return getSetter(data.getClass(), field, parameters);
	}

	public static Method getSetter(Class<?> class1, Field field) {
		return getSetter(class1, field, field.getType());
	}

	public static Method getSetter(Class<?> entityClass, Field field,
			Class<?>... parameters) {
		return getSetter(entityClass, field.getName(), parameters);
	}

	private static Method getSetter(Class<?> entityClass, String name,
			Class<?>... parameters) {
		StringBuilder builder = new StringBuilder("set");
		builder.append(name.substring(0, 1).toUpperCase());
		builder.append(name.substring(1, name.length()));
		return getMethod(entityClass, builder.toString(), parameters);
	}

	private static Method getGetter(Object data, Field field) {
		return getGetter(data.getClass(), field);
	}

	public static Method getGetter(Class<?> entityClass, Field field) {
		return getGetter(entityClass, field.getName());
	}

	private static Method getGetter(Class<?> entityClass, String name) {
		StringBuilder builder = new StringBuilder("get");
		builder.append(name.substring(0, 1).toUpperCase());
		builder.append(name.substring(1, name.length()));
		return getMethod(entityClass, builder.toString());
	}

	public static void setValue(Object data, Field field, Object value) {
		setValue(data, getSetter(data, field, field.getType()), value);
	}

	public static Object getValue(Object data, Method method) {
		return invokeMethod(method, data);
	}

	public static Object getValue(Object data, Field field) {
		Method getter = getGetter(data, field);
		return invokeMethod(getter, data);
	}

	public static Field[] getKeyFields(Class<?> class1) {
		Field[] keyFields = null;
		Field[] fields = class1.getDeclaredFields();
		RowKey rowKey;
		HCompound hCompound;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(RowKey.class)) {
				continue;
			}
			rowKey = field.getAnnotation(RowKey.class);
			keyFields = new Field[rowKey.composion().length];
			keyFields[rowKey.value()] = field;
			break;

		}

		for (Field field : fields) {
			if (!field.isAnnotationPresent(HCompound.class)) {
				continue;
			}
			hCompound = field.getAnnotation(HCompound.class);
			keyFields[hCompound.value()] = field;
		}

		return keyFields;
	}

	public List<RelationContainer> getRelations(Object data) {
		return getRelations(data,
				entityContainer.getMetaEntities().get((data.getClass()))
						.gethJoinColumnMethods());
	}

	private static void setRelationKeys(HJoinColumnMethod columnMethod,
			Object data, Object relation) {

		Method[] getters = columnMethod.getGetters();
		KeyField[] keyFields = columnMethod.getKeyFields();
		int j = 0;
		boolean isSameClass;
		Object dataToSet;
		if (columnMethod.isCollection()) {
			for (int i = 0; i < getters.length; i++) {
				isSameClass = false;
				while (j < keyFields.length && !isSameClass) {
					isSameClass = keyFields[j].getSetter().getDeclaringClass()
							.equals(getters[i].getDeclaringClass());
					j++;
				}
				if (isSameClass) {
					dataToSet = invokeMethod(getters[i], data);
					invokeMethod(keyFields[j].getSetter(), relation, dataToSet);
				}
			}
		} else {
			for (int i = 0; i < getters.length; i++) {
				dataToSet = invokeMethod(getters[i], data);
				invokeMethod(keyFields[i].getSetter(), relation, dataToSet);
			}
		}
	}

	public static List<byte[]> getRelationKeys(Object relations,
			HJoinColumnMethod columnMethod) {
		List<byte[]> keys = new ArrayList<byte[]>();
		byte[] key;
		if (relations instanceof Collection<?>) {
			for (Object relation : (Collection<?>) relations) {
				key = Translator.getKey(columnMethod.getKeyFields(), relation);
				keys.add(key);
			}
		} else {
			key = Translator.getKey(columnMethod.getKeyFields(), relations);
			keys.add(key);
		}

		return keys;
	}

	private static void setRelationKeysRev(HJoinColumnMethod columnMethod,
			Object data, Object relation) {
		Method[] setters = columnMethod.getSetters();
		KeyField[] keyFields = columnMethod.getKeyFields();
		Object dataToSet;
		for (int i = 0; i < setters.length; i++) {
			dataToSet = invokeMethod(keyFields[i].getGetter(), relation);
			invokeMethod(setters[i], data, dataToSet);
		}
	}

	private void setRelationKeys(HJoinColumnMethod columnMethod, Object data,
			Collection<Object> relations, RelationContainer container) {
		if (!columnMethod.isCollection()) {
			return;
		}
		if (relations == null || relations.size() == 0) {
			return;
		}
		KeyField[] relationKeyFields = columnMethod.getKeyFields();
		KeyField[] dataKeyFields = entityContainer.getMetaEntities()
				.get((data.getClass())).getKeyFields();
		for (KeyField keyField : dataKeyFields) {
			keyField.setValue(invokeMethod(keyField.getGetter(), data));
		}

		List<KeyField[]> keyFields = new ArrayList<KeyField[]>();
		for (Object relation : relations) {
			KeyField[] newKeyFields = new KeyField[relationKeyFields.length];
			for (int i = 0; i < relationKeyFields.length; i++) {
				setRelationKeys(columnMethod, dataKeyFields, relation);
				KeyField newKeyField = new KeyField();
				newKeyField.setField(relationKeyFields[i].getField());
				newKeyField.setReversed(relationKeyFields[i].isReversed());
				newKeyField.setGetter(relationKeyFields[i].getGetter());
				newKeyField.setSetter(relationKeyFields[i].getSetter());
				newKeyField.setValue(invokeMethod(
						relationKeyFields[i].getGetter(), relation));
				newKeyFields[i] = newKeyField;
				if (container.getEntityClass() == null) {
					container.setEntityClass(relation.getClass());
				}
			}

			keyFields.add(newKeyFields);
		}
		container.setJoinTableName(columnMethod.getJoinTable());
		container.setJoinKeyFields(keyFields);
	}

	@SuppressWarnings("unchecked")
	public List<RelationContainer> getRelations(Object data,
			List<HJoinColumnMethod> hJoinColumnMethods) {
		List<RelationContainer> containers = new ArrayList<RelationContainer>();
		Method getter;
		for (HJoinColumnMethod columnMethod : hJoinColumnMethods) {
			RelationContainer container = new RelationContainer();
			getter = columnMethod.getTargetGetter();
			Object relation = invokeMethod(getter, data);
			if (relation == null) {
				continue;
			}
			if (columnMethod.isCollection()) {
				setRelationKeys(columnMethod, data,
						((Collection<Object>) relation), container);
			} else {
				setRelationKeys(columnMethod, data, relation);
			}

			container.setRelation(relation);
			containers.add(container);
		}
		return containers;
	}

	/**
	 * Fills family side relations data
	 * 
	 * @param data
	 * @param relationData
	 * @param columnMethod
	 * @param familySides
	 */
	private void fillFamillySideRellations(Object relationData,
			HJoinColumnMethod columnMethod,
			Map<String, List<byte[]>> familySides) {

		List<byte[]> keys = getRelationKeys(relationData, columnMethod);
		familySides.put(columnMethod.getFamilyName(), keys);
	}

	/**
	 * Checks if {@link HCascadeType} coinsides
	 * 
	 * @param columnMethod
	 * @param hCascadeType
	 * @return boolean
	 */
	private boolean checkCascadeCoinsides(HJoinColumnMethod columnMethod,
			HCascadeType hCascadeType) {
		HCascadeType[] hCascadeTypes = columnMethod.gethCascadeTypes();
		boolean cascadCoinsides = false;
		for (int i = 0; i < hCascadeTypes.length && !cascadCoinsides; i++) {
			cascadCoinsides = hCascadeTypes[i].equals(hCascadeType)
					|| hCascadeTypes[i].equals(HCascadeType.ALL);
		}

		return cascadCoinsides;
	}

	/**
	 * Gets relation data from HBase and fills {@link RelationContainer}
	 * 
	 * @param data
	 * @param relation
	 * @param columnMethod
	 * @param container
	 */
	@SuppressWarnings("unchecked")
	private RelationContainer fillRelationContainer(Object data,
			Object relation, HJoinColumnMethod columnMethod) {
		RelationContainer container = new RelationContainer();
		if (relation == null) {
			return container;
		}
		if (columnMethod.isCollection()) {
			if (((Collection<Object>) relation).isEmpty()) {
				return container;
			}
			setRelationKeys(columnMethod, data,
					((Collection<Object>) relation), container);
		} else if (columnMethod.isManyToOne()) {
			setRelationKeysRev(columnMethod, data, relation);
		} else {
			setRelationKeys(columnMethod, data, relation);
		}

		container.setRelation(relation);

		return container;
	}

	/**
	 * Gets {@link Relations} for entity {@link Object} from {@link MetaEntity}
	 * and {@link HCascadeType}
	 * 
	 * @param data
	 * @param metaEntity
	 * @param hCascadeType
	 * @return {@link Relations}
	 */
	public Relations getRelations(Object data, MetaEntity metaEntity,
			HCascadeType hCascadeType) {
		List<HJoinColumnMethod> hJoinColumnMethods = metaEntity
				.gethJoinColumnMethods();
		List<RelationContainer> containers = null;
		Map<String, List<byte[]>> familySides = null;
		boolean cascadCoinsides;
		Method targetGetter;
		for (HJoinColumnMethod columnMethod : hJoinColumnMethods) {
			targetGetter = columnMethod.getTargetGetter();
			if (columnMethod.isFamilySide()) {
				Object relationData = invokeMethod(targetGetter, data);
				if (relationData == null) {
					continue;
				}
				if (familySides == null) {
					familySides = new HashMap<String, List<byte[]>>();
				}
				fillFamillySideRellations(relationData, columnMethod,
						familySides);
				continue;
			}
			cascadCoinsides = checkCascadeCoinsides(columnMethod, hCascadeType);
			if (!cascadCoinsides) {
				continue;
			}
			Object relation = invokeMethod(targetGetter, data);
			if (relation == null) {
				continue;
			}

			RelationContainer container = fillRelationContainer(data, relation,
					columnMethod);

			if (containers == null) {
				containers = new ArrayList<RelationContainer>();
			}
			containers.add(container);
		}
		Relations relations = new Relations();
		relations.setContainers(containers);
		relations.setFamilySides(familySides);
		return relations;
	}

	private static void setReplaces(Object data1, Object data2,
			KeyField[] keyFields) {
		Object value;
		for (KeyField keyField : keyFields) {
			value = invokeMethod(keyField.getGetter(), data1);
			invokeMethod(keyField.getSetter(), data2, value);
		}
	}

	public static RelationContainer getRelations(Object data1, Object data2,
			List<HJoinColumnMethod> hJoinColumnMethods) {
		RelationContainer container = null;
		Map<Object, Object> relations = new HashMap<Object, Object>();
		List<Object> deletes = new ArrayList<Object>();
		Object relation1, relation2;
		for (HJoinColumnMethod columnMethod : hJoinColumnMethods) {
			relation1 = getValue(data1, columnMethod.getTargetGetter());
			relation2 = getValue(data2, columnMethod.getTargetGetter());
			if (relation1 != null) {
				if (relation2 != null) {
					KeyField[] keyFields = columnMethod.getKeyFields();
					setReplaces(relation1, relation2, keyFields);
				} else {
					setRelationKeys(columnMethod, data1, relation1);
				}
				relations.put(relation1, relation2);
			} else if (relation2 != null) {
				deletes.add(relation2);
			}

		}

		if (relations != null || deletes != null) {
			container = new RelationContainer();
			container.setReplace(relations);
			container.setDeletes(deletes);
		}
		return container;
	}
}

class JoinParameters {

	private Field field;

	private Class<?> entityClass;

	private boolean eager;

	private Class<?> target;

	private HJoinColumn[] hJoinColumns;

	private boolean rowKeyJoin;

	private boolean isCollection;

	private boolean manyToOneCheck;

	private String joinTable;

	private String joinEntity;

	private HCascadeType[] cascadeTypes;

	private boolean isFamilyJoin;

	private HJoinFamily hJoinFamily;

	private Class<?> collectionType;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public boolean isEager() {
		return eager;
	}

	public void setEager(boolean eager) {
		this.eager = eager;
	}

	public Class<?> getTarget() {
		return target;
	}

	public void setTarget(Class<?> target) {
		this.target = target;
	}

	public HJoinColumn[] gethJoinColumns() {
		return hJoinColumns;
	}

	public void sethJoinColumns(HJoinColumn[] hJoinColumns) {
		this.hJoinColumns = hJoinColumns;
	}

	public boolean isRowKeyJoin() {
		return rowKeyJoin;
	}

	public void setRowKeyJoin(boolean rowKeyJoin) {
		this.rowKeyJoin = rowKeyJoin;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public boolean isManyToOneCheck() {
		return manyToOneCheck;
	}

	public void setManyToOneCheck(boolean manyToOneCheck) {
		this.manyToOneCheck = manyToOneCheck;
	}

	public String getJoinTable() {
		return joinTable;
	}

	public void setJoinTable(String joinTable) {
		this.joinTable = joinTable;
	}

	public String getJoinEntity() {
		return joinEntity;
	}

	public void setJoinEntity(String joinEntity) {
		this.joinEntity = joinEntity;
	}

	public HCascadeType[] getCascadeTypes() {
		return cascadeTypes;
	}

	public void setCascadeTypes(HCascadeType[] cascadeTypes) {
		this.cascadeTypes = cascadeTypes;
	}

	public boolean isFamilyJoin() {
		return isFamilyJoin;
	}

	public void setFamilyJoin(boolean isFamilyJoin) {
		this.isFamilyJoin = isFamilyJoin;
	}

	public HJoinFamily getHJoinFamily() {
		return hJoinFamily;
	}

	public void setHJoinFamily(HJoinFamily hJoinFamily) {
		this.hJoinFamily = hJoinFamily;
	}

	public Class<?> getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(Class<?> collectionType) {
		this.collectionType = collectionType;
	}
}
