package org.hbaseom.client.translators.util;

/**
 * To provide translation from java types to byte[] and from byte[] to java
 * types for special layer
 * 
 * @author levan
 * 
 */
public interface BytesConverter {

	int getSizeOf(String name);

	byte[] toBytes(Short value);

	byte[] toBytes(Integer value);

	byte[] toBytes(Long value);

	byte[] toBytes(Float value);

	byte[] toBytes(Double value);

	byte[] toBytes(Byte value);

	byte[] toBytes(Character value);

	byte[] toBytes(Boolean value);

	byte[] toBytes(String value);

	byte[] add(byte[] bt1, byte[] bt2);

	void putBytes(byte[] tgtBytes, int tgtOffset, byte[] srcBytes,
			int srcOffset, int srcLength);

	short toShort(byte[] value);

	short toShort(byte[] value, int offset);

	int toInt(byte[] value);

	int toInt(byte[] value, int offset);

	long toLong(byte[] value);

	long toLong(byte[] value, int offset);

	float toFloat(byte[] value);

	float toFloat(byte[] value, int offset);

	double toDouble(byte[] value);

	double toDouble(byte[] value, int offset);

	boolean toBoolean(byte[] value);

	String toString(byte[] value);

	String toString(final byte[] value, int off, int len);

}
