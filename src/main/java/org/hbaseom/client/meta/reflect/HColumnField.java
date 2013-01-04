package org.hbaseom.client.meta.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hbaseom.client.translators.util.BytesApi;

/**
 * 
 * @author levan
 * 
 */
public class HColumnField {

	private Field field;

	private String name;

	private String qualifier;

	private Object value;

	private Method getter;

	private Method setter;

	private byte[] nameBs;

	private byte[] qualifierBs;

	private boolean multi;

	public Field getField() {
		return this.field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.nameBs = BytesApi.toBytes(name);
		this.name = name;
	}

	public String getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifierBs = BytesApi.toBytes(qualifier);
		this.qualifier = qualifier;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
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

	public byte[] getNameBs() {
		return nameBs;
	}

	public void setNameBs(byte[] nameBs) {
		this.nameBs = nameBs;
	}

	public byte[] getQualifierBs() {
		return qualifierBs;
	}

	public void setQualifierBs(byte[] qualifierBs) {
		this.qualifierBs = qualifierBs;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

}
