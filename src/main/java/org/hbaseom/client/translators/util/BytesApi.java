package org.hbaseom.client.translators.util;

/**
 * Provides translation from byte[] to java types and from java types to byte[]
 * and several method implementations over byte arrays
 * 
 * @author levan
 * 
 */
public class BytesApi {

	private static BytesConverter converter;

	public static int SIZEOF_SHORT;

	public static int SIZEOF_INT;

	public static int SIZEOF_LONG;

	public static int SIZEOF_FLOAT;

	public static int SIZEOF_DOUBLE;

	public static int SIZEOF_BYTE;

	public static int SIZEOF_CHAR;

	public static int SIZEOF_BOOLEAN;

	public static void setConverter(BytesConverter converterImpl) {
		converter = converterImpl;

		SIZEOF_SHORT = converter.getSizeOf("short");

		SIZEOF_INT = converter.getSizeOf("int");

		SIZEOF_LONG = converter.getSizeOf("long");

		SIZEOF_FLOAT = converter.getSizeOf("float");

		SIZEOF_DOUBLE = converter.getSizeOf("double");

		SIZEOF_BYTE = converter.getSizeOf("byte");

		SIZEOF_CHAR = converter.getSizeOf("char");

		SIZEOF_BOOLEAN = converter.getSizeOf("boolean");

	}

	public static byte[] toBytes(Short value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Integer value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Long value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Float value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Double value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Byte value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Character value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(Boolean value) {
		return converter.toBytes(value);
	}

	public static byte[] toBytes(String value) {
		return converter.toBytes(value);
	}

	public static short toShort(byte[] value) {
		return converter.toShort(value);
	}

	public static short toShort(byte[] value, int offset) {
		return converter.toShort(value, offset);
	}

	public static int toInt(byte[] value) {
		return converter.toInt(value);
	}

	public static int toInt(byte[] value, int offset) {
		return converter.toInt(value, offset);
	}

	public static long toLong(byte[] value) {
		return converter.toLong(value);
	}

	public static long toLong(byte[] value, int offset) {
		return converter.toLong(value, offset);
	}

	public static float toFloat(byte[] value) {
		return converter.toFloat(value);
	}

	public static float toFloat(byte[] value, int offset) {
		return converter.toFloat(value, offset);
	}

	public static double toDouble(byte[] value) {
		return converter.toDouble(value);
	}

	public static double toDouble(byte[] value, int offset) {
		return converter.toDouble(value, offset);
	}

	public static boolean toBoolean(byte[] value) {
		return converter.toBoolean(value);
	}

	public static String toString(byte[] value) {
		return converter.toString(value);
	}

	public static String toString(final byte[] value, int off, int len) {
		return converter.toString(value, off, len);
	}

	public static byte[] add(byte[] bt1, byte[] bt2) {
		return converter.add(bt1, bt2);
	}

	public static void putBytes(byte[] tgtBytes, int tgtOffset,
			byte[] srcBytes, int srcOffset, int srcLength) {
		converter.putBytes(tgtBytes, tgtOffset, srcBytes, srcOffset, srcLength);
	}
}
