package org.hbaseom.client.translators;


import java.util.List;
import java.util.Map;

import org.hbaseom.client.meta.reflect.KeyField;

/**
 * Container class to cache relation metadata
 * 
 * @author levan
 * 
 */
public class RelationContainer {

	private Map<Object, Object> replace;

	private List<Object> deletes;

	private Object relation;

	private List<KeyField[]> joinKeyFields;

	private Class<?> entityClass;

	private String joinTableName;

	private String family;

	public Map<Object, Object> getReplace() {
		return replace;
	}

	public void setReplace(Map<Object, Object> replace) {
		this.replace = replace;
	}

	public List<Object> getDeletes() {
		return deletes;
	}

	public void setDeletes(List<Object> deletes) {
		this.deletes = deletes;
	}

	public List<KeyField[]> getJoinKeyFields() {
		return joinKeyFields;
	}

	public void setJoinKeyFields(List<KeyField[]> joinKeyFields) {
		this.joinKeyFields = joinKeyFields;
	}

	public Object getRelation() {
		return relation;
	}

	public void setRelation(Object relation) {
		this.relation = relation;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getJoinTableName() {
		return joinTableName;
	}

	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
}
