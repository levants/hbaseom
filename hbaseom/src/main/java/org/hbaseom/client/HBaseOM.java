package org.hbaseom.client;

import java.io.Closeable;
import java.util.Map;

import org.hbaseom.client.meta.EntityContainer;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.translators.OMTranslator;

/**
 * Interface to create and work on {@link OMTranslator}
 * 
 * @author levan
 * 
 */
public interface HBaseOM extends Closeable {

	Map<String, Class<?>> getEntities();

	Map<Class<?>, MetaEntity> getMetaEntities();

	EntityContainer getEntityContainer();

	OMTranslator getOMTranslator();
}
