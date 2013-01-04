package org.hbaseom.client.meta;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Container class for {@link MetaEntity} and entity names (
 * {@link ConcurrentMap}) which is kept in cache
 * 
 * @author levan
 * 
 */
public class EntityContainer {

	/* Keeps Class (entity class) - MetaEntity objects */
	private ConcurrentMap<Class<?>, MetaEntity> metaEntities = new ConcurrentHashMap<Class<?>, MetaEntity>();

	/* Keeps netity class name and entity class objects */
	private ConcurrentMap<String, Class<?>> entities;

	public void addMeta(MetaEntity container) {
		metaEntities.put(container.getEntityClass(), container);
	}

	public MetaEntity getMeta(Class<?> class1) {
		return metaEntities.get(class1);
	}

	public ConcurrentMap<Class<?>, MetaEntity> getMetaEntities() {
		return metaEntities;
	}

	public ConcurrentMap<String, Class<?>> getEntities() {
		if (entities == null) {
			entities = new ConcurrentHashMap<String, Class<?>>();
		}
		return entities;
	}

}
