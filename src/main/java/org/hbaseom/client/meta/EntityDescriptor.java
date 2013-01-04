package org.hbaseom.client.meta;

/**
 * 
 * @author rezo
 */
public class EntityDescriptor {

	private Class<?> entityClass;

	private boolean autoCreate;

	private int maxVersions;

	private int getMaxVersions;

	private int scanMaxVersions;

	private int scanCache;

	public EntityDescriptor() {
	}

	public EntityDescriptor(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public EntityDescriptor(Class<?> entityClass, boolean autoCreate) {
		this.entityClass = entityClass;
		this.autoCreate = autoCreate;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public int getMaxVersions() {
		return this.maxVersions;
	}

	public void setMaxVersions(int maxVersions) {
		this.maxVersions = maxVersions;
	}

	public int getGetMaxVersions() {
		return this.getMaxVersions;
	}

	public void setGetMaxVersions(int getMaxVersions) {
		this.getMaxVersions = getMaxVersions;
	}

	public int getScanMaxVersions() {
		return this.scanMaxVersions;
	}

	public void setScanMaxVersions(int scanMaxVersions) {
		this.scanMaxVersions = scanMaxVersions;
	}

	public int getScanCache() {
		return this.scanCache;
	}

	public void setScanCache(int scanCache) {
		this.scanCache = scanCache;
	}
}
