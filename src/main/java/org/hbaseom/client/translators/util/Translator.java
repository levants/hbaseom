package org.hbaseom.client.translators.util;

import static org.hbaseom.client.meta.reflect.Reflector.invokeMethod;
import static org.hbaseom.client.translators.util.TypeChecker.isBoolean;
import static org.hbaseom.client.translators.util.TypeChecker.isByte;
import static org.hbaseom.client.translators.util.TypeChecker.isByteArray;
import static org.hbaseom.client.translators.util.TypeChecker.isCharacter;
import static org.hbaseom.client.translators.util.TypeChecker.isDouble;
import static org.hbaseom.client.translators.util.TypeChecker.isFloat;
import static org.hbaseom.client.translators.util.TypeChecker.isInteger;
import static org.hbaseom.client.translators.util.TypeChecker.isLong;
import static org.hbaseom.client.translators.util.TypeChecker.isShort;
import static org.hbaseom.client.translators.util.TypeChecker.isString;

import java.io.IOException;
import java.lang.reflect.Method;

import org.hbaseom.client.annotations.HCompoundSize;
import org.hbaseom.client.meta.EntityContainer;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.meta.reflect.KeyField;

/**
 * Utility class which translates between <b>POJOs</b> and
 * {@link org.HBaseOM.hadoop.hbase.ipc.HBaseClient} and
 * {@link org.HBaseOM.async.HBaseClient} objecs such as Put, Get, Delete etc.
 * and vice versa
 * 
 * @author levan
 * 
 */
public class Translator {

	private final transient EntityContainer container;

	public Translator(final EntityContainer container) {
		this.container = container;
	}

	public MetaEntity getMeta(final Class<?> class1) {
		return container.getMeta(class1);
	}

	/**
	 * Returns size of {@link Number} object or null by object type name
	 * 
	 * @param name
	 * @return {@link Integer}
	 */
	public static Integer getSize(final String name) {
		if (isShort(name)) {
			return BytesApi.SIZEOF_SHORT;
		} else if (isInteger(name)) {
			return BytesApi.SIZEOF_INT;
		} else if (isLong(name)) {
			return BytesApi.SIZEOF_LONG;
		} else if (isFloat(name)) {
			return BytesApi.SIZEOF_FLOAT;
		} else if (isDouble(name)) {
			return BytesApi.SIZEOF_DOUBLE;
		} else if (isByte(name)) {
			return BytesApi.SIZEOF_BYTE;
		} else if (isCharacter(name)) {
			return BytesApi.SIZEOF_CHAR;
		} else if (isBoolean(name)) {
			return BytesApi.SIZEOF_BOOLEAN;
		} else if (isString(name) || isByteArray(name)) {
			return null;
		} else {
			throw new IllegalArgumentException("Unsupported type in entity");
		}
	}

	public static int getSize(String name, Object value) throws IOException {
		if (name.equals("[B") || name.equals("[Ljava.lang.Byte;")) {
			return (value != null ? ((byte[]) value).length : 0);
		} else if (name.equals("String") || name.equals("string")
				|| name.equals("java.lang.String")) {
			return (value != null ? ((String) value).length() : 0);
		} else {
			return getSize(name);
		}
	}

	public static byte[] getSupportedType(Object value, boolean reversed) {
		String name = value.getClass().getName();
		return getSupportedType(name, value, reversed);
	}

	public static byte[] add(byte[] value1, byte[] value2) {
		return BytesApi.add(value1, value2);
	}

	public static byte[] add(byte[] value1, Object value2) {
		if (value2 == null) {
			return value1;
		}
		byte[] restValue = getSupportedType(value2);
		return add(value1, restValue);
	}

	public static byte[] addAll(Iterable<Object> datas) {
		byte[] ret = new byte[0];
		for (Object data : datas) {
			ret = add(ret, data);
		}
		return ret;
	}

	public static void revrece(byte[] array) {
		int length = array.length;
		byte temp;

		for (int i = 0; i < length / 2; i++) {
			temp = array[i];
			array[i] = array[length - i];
			array[length - i] = temp;
		}
	}

	/**
	 * Translates predefined {@link Number}, {@link String}, {@link Boolean}
	 * types in {@link byte} array respectively
	 * 
	 * @param name
	 * @param value
	 * @param reverse
	 * @return {@link byte[]}
	 */
	public static byte[] getSupportedType(String name, Object value,
			boolean reverse) {
		if (isShort(name)) {
			Short reverseValue = (Short) value;
			if (reverse) {
				reverseValue = (short) (Short.MAX_VALUE - (Short) value);
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isInteger(name)) {
			Integer reverseValue = (Integer) value;
			if (reverse) {
				reverseValue = (Integer) (Integer.MAX_VALUE - (Integer) value);
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isLong(name)) {
			Long reverseValue = (Long) value;
			if (reverse) {
				reverseValue = (Long) (Long.MAX_VALUE - (Long) value);
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isFloat(name)) {
			Float reverseValue = (Float) value;
			if (reverse) {
				reverseValue = (Float) (Float.MAX_VALUE - (Float) value);
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isDouble(name)) {
			Double reverseValue = (Double) value;
			if (reverse) {
				reverseValue = (Double) (Double.MAX_VALUE - (Double) value);
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isByte(name)) {
			Byte reverseValue = (Byte) value;
			if (reverse) {
				reverseValue = (byte) (Byte.MAX_VALUE - (Byte) value);
			}
			return (new byte[] { reverseValue });
		} else if (isBoolean(name)) {
			Boolean reverseValue = (Boolean) value;
			return BytesApi.toBytes(reverseValue);
		} else if (isCharacter(name)) {
			Character reverseValue = (Character) value;
			return BytesApi.toBytes(reverseValue);
		} else if (isString(name)) {
			String reverseValue = (String) value;
			if (reverse) {
				StringBuilder buffer = new StringBuilder(reverseValue);
				buffer = buffer.reverse();
				reverseValue = buffer.toString();
			}
			return BytesApi.toBytes(reverseValue);
		} else if (isByteArray(name)) {
			byte[] reverseValue = (byte[]) value;
			if (reverse) {
				revrece(reverseValue);
			}
			return reverseValue;
		} else {
			throw new IllegalArgumentException("Unsupported type in entity");
		}
	}

	public static byte[] getSupportedType(String name, Object value) {
		return getSupportedType(name, value, false);
	}

	public static byte[] getSupportedType(Object value) {
		return getSupportedType(value.getClass().getName(), value, false);
	}

	/**
	 * Translates from {@link byte} array to predefined {@link Object} (such as
	 * {@link Number}, {@link Boolean}, {@link String} and etc.) for fill entity
	 * fields
	 * 
	 * @param data
	 * @param name
	 * @param offset
	 * @param length
	 * @param reversed
	 * @return {@link Object}
	 */
	public static Object getValue(byte[] data, String name, int offset,
			int length, boolean reversed) {
		if (data == null) {
			return null;
		}
		Object value;
		if (isShort(name)) {
			value = BytesApi.toShort(data, offset);
			if (reversed) {
				return Short.MAX_VALUE - (Short) value;
			}
			return value;
		} else if (isByte(name)) {
			value = data[offset];
			if (reversed) {
				return Byte.MAX_VALUE - (Byte) value;
			}
			return value;
		} else if (isInteger(name)) {
			value = BytesApi.toInt(data, offset);
			if (reversed) {
				return Integer.MAX_VALUE - (Integer) value;
			}
			return value;
		} else if (isLong(name)) {
			value = BytesApi.toLong(data, offset);
			if (reversed) {
				return Long.MAX_VALUE - (Long) value;
			}
			return value;
		} else if (isFloat(name)) {
			value = BytesApi.toFloat(data, offset);
			if (reversed) {
				return Float.MAX_VALUE - (Float) value;
			}
			return value;
		} else if (isDouble(name)) {
			value = BytesApi.toDouble(data, offset);
			if (reversed) {
				return Double.MAX_VALUE - (Double) value;
			}
			return value;
		} else if (isBoolean(name)) {
			byte[] data2 = new byte[length];
			if (offset == 0 && length == data.length) {
				data2 = data;
			} else {
				BytesApi.putBytes(data, offset, data2, 0, length);
			}
			value = BytesApi.toBoolean(data2);
			if (reversed) {
				return !(Boolean) value;
			}
			return value;
		} else if (isString(name)) {
			value = BytesApi.toString(data, offset, length);
			if (reversed) {
				StringBuilder builder = new StringBuilder((String) value);
				return (builder.reverse()).toString();
			}
			return value;
		} else if (isByteArray(name)) {
			byte[] data2 = new byte[length];
			if (offset == 0 && length == data.length) {
				data2 = data;
			} else {
				BytesApi.putBytes(data, offset, data2, 0, length);
			}
			return data2;
		} else {
			throw new IllegalArgumentException("Unsupported type in entity");
		}
	}

	public static Object getValue(byte[] data, String name, boolean reversed) {
		if (data == null) {
			return null;
		}
		return getValue(data, name, 0, data.length, reversed);
	}

	public static Object getValue(byte[] data, String name) {
		return getValue(data, name, false);
	}

	public static byte[] setCompoundValue(byte[] row, Object value,
			String name, boolean reversed) {
		byte[] restKey = getSupportedType(name, value, reversed);
		return add(row, restKey);
	}

	public static byte[] getCompoundValue(byte[] row, Object value,
			boolean reversed) {
		if (value == null) {
			return null;
		}
		byte[] restKey = getSupportedType(value.getClass().getName(), value,
				reversed);
		return add(row, restKey);
	}

	public byte[] getKey(Class<?> entityClass, Object data) {
		return getKey(getMeta(entityClass).getKeyFields(), data);
	}

	/**
	 * Translates from entity fields to {@link byte} array for HBase row key by
	 * {@link KeyField} array - meta parameter for each entity
	 * 
	 * @param keyFields
	 * @param entity
	 * @return {@link byte[]}
	 */
	public static byte[] getKey(KeyField[] keyFields, Object entity) {
		byte[] key = new byte[0];
		Object value;
		byte[] restKey;
		for (KeyField keyField : keyFields) {
			value = invokeMethod(keyField.getGetter(), entity);
			if (value == null) {
				continue;
			}
			restKey = getSupportedType(keyField.getName(), value,
					keyField.isReversed());
			key = BytesApi.add(key, restKey);
		}
		return key;
	}

	public static byte[] getKey(boolean[] reverseds, Object... params) {
		byte[] key = new byte[0];
		byte[] restKey;
		for (int i = 0; i < params.length; i++) {
			if (params[i] == null) {
				continue;
			}
			restKey = getSupportedType(params[i].getClass().getSimpleName(),
					params[i], reverseds[i]);
			key = BytesApi.add(key, restKey);
		}
		return key;
	}

	public static byte[] getKey(KeyField[] keyFields, Object... values) {
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("set values for key");
		} else if (values.length != keyFields.length) {
			throw new IllegalArgumentException(
					"compozite keys and arguments amoutn are not the same");
		}
		byte[] key = new byte[0];
		byte[] restKey;
		for (int i = 0; i < keyFields.length; i++) {
			if (values[i] == null) {
				continue;
			}
			restKey = getSupportedType(keyFields[i].getName(), values[i],
					keyFields[i].isReversed());
			key = BytesApi.add(key, restKey);
		}
		return key;
	}

	public byte[] getRow(Class<?> entityClass, Object... values) {
		KeyField[] keyFields = getMeta(entityClass).getKeyFields();
		return getKey(keyFields, values);
	}

	private boolean[] getNulls(MetaEntity metaEntity, int size) {
		if (metaEntity.getKeySizes() == null) {
			return null;
		}
		return metaEntity.getKeySizes().get(size);
	}

	/**
	 * Checks by size should field value be null or not
	 * 
	 * @param metaEntity
	 * @param size
	 * @param int
	 * @return boolean
	 */
	private boolean checkNullable(boolean[] indexes, int i) {
		if (indexes != null) {
			return indexes[i];
		} else {
			return false;
		}
	}

	/**
	 * Gets meta result from result as byte[]
	 * 
	 * @param sizeOffset
	 * @return int[][]
	 */
	public static int[][] getMetaResult(byte[] sizeOffset) {
		int sizeOfInt = BytesApi.SIZEOF_INT;
		int length = sizeOffset.length / sizeOfInt;
		int[][] sizes = new int[2][length];
		for (int i = 0; i < length; i++) {
			sizes[0][i] = BytesApi.toInt(sizeOffset, 0);
			sizes[1][i] = BytesApi.toInt(sizeOffset, sizeOfInt);

		}
		return sizes;
	}

	/**
	 * Set key parameters where {@link HCompoundSize} is DEFINED
	 * 
	 * @param metaEntity
	 * @param row
	 * @param data
	 */
	private void setKeyForDefine(MetaEntity metaEntity, byte[] row, Object data) {
		KeyField[] keyFields = metaEntity.getKeyFields();
		KeyField keyField;
		boolean[] indexes = getNulls(metaEntity, row.length);
		int offsetLess = 0;
		Method setter;
		Object value;
		for (int i = 0; i < keyFields.length; i++) {
			keyField = keyFields[i];
			if (checkNullable(indexes, i)) {
				offsetLess += keyField.getSize();
				continue;
			}
			setter = keyField.getSetter();
			value = getValue(row, keyField.getName(),
					(keyField.getOffset() - offsetLess), keyField.getSize(),
					keyField.isReversed());
			invokeMethod(setter, data, value);
		}
	}

	/**
	 * Set key parameters where {@link HCompoundSize} is NONE
	 * 
	 * @param metaEntity
	 * @param row
	 * @param data
	 * @param result
	 */
	private void setKeyForNone(MetaEntity metaEntity, byte[] row, Object data,
			byte[] sizeOffset) {
		KeyField[] keyFields = metaEntity.getKeyFields();
		int[][] sizes = getMetaResult(sizeOffset);
		int j = 0;
		Method setter;
		Object value;
		for (KeyField keyField : keyFields) {
			setter = keyField.getSetter();
			if (keyField.getOffset() == null) {
				value = getValue(row, keyField.getName(), sizes[0][j],
						sizes[1][j], keyField.isReversed());
				invokeMethod(setter, data, value);
				j++;
			}
			value = getValue(row, keyField.getName(), keyField.getOffset(),
					keyField.getSize(), keyField.isReversed());
			invokeMethod(setter, data, value);
		}
	}

	/**
	 * Sets first keys when in general {@link HCompoundSize} is DEPENDED
	 * 
	 * @param currentKeyField
	 * @param row
	 * @param data
	 */
	private boolean setFirstKeysForDepended(KeyField currentKeyField,
			byte[] row, Object data) {
		Integer size = currentKeyField.getSize();
		Integer offset = currentKeyField.getOffset();
		boolean isReversed = currentKeyField.isReversed();
		boolean isDefined = currentKeyField.getHCompoundSize().equals(
				HCompoundSize.DEFINED)
				&& size > 0 && offset != null;

		if (isDefined) {
			Method setter = currentKeyField.getSetter();
			Object value = getValue(row, currentKeyField.getName(), offset,
					size, isReversed);
			invokeMethod(setter, data, value);
		}

		return isDefined;
	}

	/**
	 * Sets key for undefined keys when in general {@link HCompoundSize} is
	 * DEPENDED
	 * 
	 * @param valuedFrom
	 * @param valuedTo
	 * @param currentKeyField
	 * @param data
	 * @param row
	 */
	private void setUndefinedKeyforDepended(KeyField valuedFrom,
			KeyField valuedTo, KeyField currentKeyField, Object data, byte[] row) {
		int from;
		int to;
		int rowLength = row.length;
		if (valuedFrom == null) {
			from = 0;
		} else {
			from = valuedFrom.getOffset() + valuedFrom.getSize() - 1;
		}
		if (valuedTo == null) {
			to = rowLength;
		} else {
			to = rowLength - valuedTo.getReverseOffset();
		}
		Object value = getValue(row, currentKeyField.getName(), from, to,
				currentKeyField.isReversed());
		Method setter = currentKeyField.getSetter();
		invokeMethod(setter, data, value);
	}

	/**
	 * Sets key for defined or depended keys when in general
	 * {@link HCompoundSize} is DEPENDED
	 * 
	 * @param valuedFrom
	 * @param valuedTo
	 * @param currentKeyField
	 * @param data
	 * @param row
	 */
	private void setDefinedKeyforDepended(KeyField currentKeyField,
			Object data, byte[] row) {
		int from;
		int to;

		from = row.length - currentKeyField.getReverseOffset();
		to = currentKeyField.getSize();
		Method setter = currentKeyField.getSetter();
		Object value = getValue(row, currentKeyField.getName(), from, to,
				currentKeyField.isReversed());
		invokeMethod(setter, data, value);

	}

	/**
	 * Set key parameters where {@link HCompoundSize} is DEPENDED
	 * 
	 * @param metaEntity
	 * @param row
	 * @param data
	 */
	private void setKeyForDepended(MetaEntity metaEntity, byte[] row,
			Object data) {
		KeyField[] keyFields = metaEntity.getKeyFields();

		int keyFieldsLength = keyFields.length;

		boolean isDefined = true;
		KeyField valuedFrom = null;
		KeyField currentKeyField;

		Integer size;

		for (int i = 0; i < keyFieldsLength && isDefined; i++) {
			currentKeyField = keyFields[i];
			isDefined = setFirstKeysForDepended(currentKeyField, row, data);
			if (isDefined) {
				valuedFrom = currentKeyField;
			}
		}

		if (isDefined) {
			return;
		}

		KeyField valuedTo = null;
		keyFieldsLength--;
		for (int i = keyFieldsLength; i >= 0; i--) {

			currentKeyField = keyFields[i];
			size = currentKeyField.getSize();

			isDefined = currentKeyField.getHCompoundSize().equals(
					HCompoundSize.DEFINED)
					&& size > 0 && currentKeyField.getReverseOffset() != null;
			if (!isDefined) {
				setUndefinedKeyforDepended(valuedFrom, valuedTo,
						currentKeyField, data, row);
			} else {
				setDefinedKeyforDepended(currentKeyField, data, row);
				valuedTo = currentKeyField;
			}
		}
	}

	/**
	 * Fills key fields by {@link MetaEntity} configuration from {@link byte}[]
	 * as key to {@link Object}... entity's fields respectively
	 * 
	 * @param row
	 * @param ...datas
	 */
	public void setKey(byte[] row, Object... datas) {
		Object data = datas[0];
		MetaEntity metaEntity = getMeta(data.getClass());
		KeyField[] keyFields = metaEntity.getKeyFields();
		if (keyFields.length == 1) {
			KeyField firstKeyField = keyFields[0];
			Method setter = firstKeyField.getSetter();
			Object value = getValue(row, firstKeyField.getName(), 0,
					row.length, firstKeyField.isReversed());
			invokeMethod(setter, data, value);
		} else if (metaEntity.getHCompountSize().equals(HCompoundSize.DEFINED)) {
			setKeyForDefine(metaEntity, row, data);
		} else if (metaEntity.getHCompountSize().equals(HCompoundSize.NONE)
				&& datas.length == 2) {
			setKeyForNone(metaEntity, row, data, (byte[]) datas[1]);
			return;
		} else if (metaEntity.getHCompountSize().equals(HCompoundSize.DEPENDED)) {
			setKeyForDepended(metaEntity, row, data);
		}
	}
}
