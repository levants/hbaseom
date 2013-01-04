package org.hbaseom.client.translators;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hbaseom.client.query.HBaseQuery;
import org.hbaseom.client.translators.util.Translator;

/**
 * Interface to work on main operations of hbase client on entities
 * 
 * @author levan
 * 
 */
public interface OMTranslator extends Closeable {

	void setMaxVersions(int maxVersions);

	Translator getTranslator();

	<E> E get(E data) throws IOException;

	<E> E get(Class<E> entityClass, Object... values) throws IOException;

	Object get(byte[] rowKey, Class<?> entityClass) throws IOException;

	void getLazyRelations(Object entity) throws IOException;

	<E> void put(E data) throws IOException;

	<E> void put(Iterable<E> datas) throws IOException;

	<E> void delete(E data) throws IOException;

	<E> void delete(Collection<E> datas) throws IOException;

	<E> void replace(E data1, E data2) throws IOException;

	List<?> scan(Class<?> entityClass) throws IOException;

	Collection<?> scan(Class<?> entityClass, Class<?> collectionType)
			throws IOException;

	HBaseQuery getHBaseQuery();

	HBaseQuery getHBaseQuery(String query);

	HBaseQuery getHBaseQuery(Class<?> entityClass);

	byte[] getNextPage() throws IOException;

	void flushCommits() throws IOException;

	void closeWithCommits() throws IOException;
}
