package org.hbaseom.client.meta.reflect;


import static org.hbaseom.client.meta.reflect.Reflector.invokeMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hbaseom.client.annotations.HCompoundSize;

/**
 * Container class for hbase rowkey fields
 * 
 * @author levan
 * 
 */
public class KeyField {

	private Field field;

	private Object value;

	private Integer size;

	private boolean reversed;

	private Method getter;

	private Method setter;

	private HCompoundSize hCompoundSize;

	private Integer offset;

	private Integer reverseOffset;

	private String name;

	private boolean nullable;

	public Field getField() {
		return this.field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public Method getGetter() {
		return getter;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public Method getSetter() {
		return setter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public void setValueIn(Object data) {
		value = invokeMethod(getGetter(), data);
	}

	public void setValueOut(Object data) {
		value = data;
	}

	public void setValueTo(Object data, Object value) {
		this.value = value;
		invokeMethod(getSetter(), data, value);
	}

	public HCompoundSize getHCompoundSize() {
		return hCompoundSize;
	}

	public void setHCompoundSize(HCompoundSize hCompoundSize) {
		this.hCompoundSize = hCompoundSize;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReverseOffset() {
		return reverseOffset;
	}

	public void setReverseOffset(Integer reverseOffset) {
		this.reverseOffset = reverseOffset;
	}

	public boolean hasOffset() {
		return (offset != null && offset != 0);
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
}
