package org.hbaseom.client.translators.util;

import static org.hbaseom.client.meta.reflect.Reflector.instantiate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains implementations for several collection interfaces for joins
 * 
 * @author levan
 * 
 */
public final class CollectionTypes {

	private static final Map<Class<?>, Class<?>> COLLECTION_TYPES = new HashMap<Class<?>, Class<?>>();

	private CollectionTypes() {
		throw new AssertionError("Can not instantiate class");
	}

	public static void setCollectionTypes() {
		COLLECTION_TYPES.put(List.class, ArrayList.class);
		COLLECTION_TYPES.put(Collection.class, ArrayList.class);
		COLLECTION_TYPES.put(Set.class, HashSet.class);
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> getAproprietedInstance(Class<?> clazz) {
		Class<?> collectionClass = COLLECTION_TYPES.get(clazz);
		if (collectionClass == null) {
			return (Collection<Object>) instantiate(clazz);
		} else {
			return (Collection<Object>) instantiate(collectionClass);
		}
	}

	public static boolean checkAproprietedType(Class<?> type) {
		return COLLECTION_TYPES.containsKey(type);
	}
}
