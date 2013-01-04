package org.hbaseom.client.meta;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.hbaseom.client.annotations.HCompoundSize;
import org.hbaseom.client.meta.reflect.FieldMethod;
import org.hbaseom.client.meta.reflect.HColumnField;
import org.hbaseom.client.meta.reflect.HJoinColumnMethod;
import org.hbaseom.client.meta.reflect.KeyField;

/**
 * Entity information container class which initialized on application startup
 * time and saved in cache
 * 
 * @author levan
 * 
 */
public class MetaEntity {

	private Class<?> entityClass;

	private String tableName;

	private byte[] tableNameBt;

	private KeyField[] keyFields;

	private List<HJoinColumnMethod> hJoinColumnMethods;

	private Map<Field, FieldMethod> fieldMethods;

	private List<HColumnField> hColumnFields;

	private HCompoundSize hCompoundSize;

	private boolean startKeyBegin;

	private boolean startKeyEnd;

	private int maxVersions = 1;

	private int getMaxVersions = 1;

	private int scanMaxVersions = 1;

	private int scanCache;

	private Map<String, FieldMethod> qualifierMethods;

	private Map<String, List<FieldMethod>> columnMethods;

	private ConcurrentMap<Integer, boolean[]> keySizes;

	private boolean predefinedNullables;

	private boolean hasFamilySideJoin;

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public byte[] getTableNameBt() {
		return tableNameBt;
	}

	public void setTableNameBt(byte[] tableNameBt) {
		this.tableNameBt = tableNameBt;
	}

	public KeyField[] getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(KeyField[] keyFields) {
		this.keyFields = keyFields;
	}

	public List<HJoinColumnMethod> gethJoinColumnMethods() {
		return hJoinColumnMethods;
	}

	public void sethJoinColumnMethods(List<HJoinColumnMethod> hJoinColumnMethods) {
		this.hJoinColumnMethods = hJoinColumnMethods;
	}

	public Map<Field, FieldMethod> getFieldMethods() {
		return fieldMethods;
	}

	public void setFieldMethods(Map<Field, FieldMethod> fieldMethods) {
		this.fieldMethods = fieldMethods;
	}

	public List<HColumnField> gethColumnFields() {
		return hColumnFields;
	}

	public void sethColumnFields(List<HColumnField> hColumnFields) {
		this.hColumnFields = hColumnFields;
	}

	public HCompoundSize getHCompountSize() {
		return hCompoundSize;
	}

	public void setHCompoundSize(HCompoundSize hCompoundSize) {
		this.hCompoundSize = hCompoundSize;
	}

	public boolean isStartKeyBegin() {
		return startKeyBegin;
	}

	public void setStartKeyBegin(boolean startKeyBegin) {
		this.startKeyBegin = startKeyBegin;
	}

	public boolean isStartKeyEnd() {
		return startKeyEnd;
	}

	public void setStartKeyEnd(boolean startKeyEnd) {
		this.startKeyEnd = startKeyEnd;
	}

	public int getMaxVersions() {
		return maxVersions;
	}

	public void setMaxVersions(int maxVersions) {
		this.maxVersions = maxVersions;
	}

	public int getGetMaxVersions() {
		return getMaxVersions;
	}

	public void setGetMaxVersions(int getMaxVersions) {
		this.getMaxVersions = getMaxVersions;
	}

	public int getScanMaxVersions() {
		return scanMaxVersions;
	}

	public void setScanMaxVersions(int scanMaxVersions) {
		this.scanMaxVersions = scanMaxVersions;
	}

	public int getScanCache() {
		return scanCache;
	}

	public void setScanCache(int scanCache) {
		this.scanCache = scanCache;
	}

	public Map<String, FieldMethod> getQualifierMethods() {
		return qualifierMethods;
	}

	public void setQualifierMethods(Map<String, FieldMethod> qualifierMethods) {
		this.qualifierMethods = qualifierMethods;
	}

	public Map<String, List<FieldMethod>> getColumnMethods() {
		return columnMethods;
	}

	public void setColumnMethods(Map<String, List<FieldMethod>> columnMethods) {
		this.columnMethods = columnMethods;
	}

	public ConcurrentMap<Integer, boolean[]> getKeySizes() {
		return keySizes;
	}

	public void setKeySizes(ConcurrentMap<Integer, boolean[]> keySizes) {
		this.keySizes = keySizes;
	}

	public boolean isPredefinedNullables() {
		return predefinedNullables;
	}

	public void setPredefinedNullables(boolean predefinedNullables) {
		this.predefinedNullables = predefinedNullables;
	}

	public boolean isHasFamilySideJoin() {
		return hasFamilySideJoin;
	}

	public void setHasFamilySideJoin(boolean hasFamilySideJoin) {
		this.hasFamilySideJoin = hasFamilySideJoin;
	}

}
