package org.hbaseom.client.query;

import static org.hbaseom.client.meta.reflect.Reflector.getField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.hbaseom.client.HBaseOM;
import org.hbaseom.client.annotations.HColumnFamily;
import org.hbaseom.client.filters.HQueryFilter;
import org.hbaseom.client.filters.HQueryType;
import org.hbaseom.client.translators.OMTranslator;

/**
 * Query for Scan methods
 * 
 * @author levan
 * 
 */
public abstract class HBaseQuery {

	protected Class<?> entityClass;

	private String hbaseQuery;

	private Map<Integer, Object> parameters;

	protected transient OMTranslator omTranslator;

	private HBaseOM hBaseOM;

	private Map<Integer, FilterContainer> filters;

	private String scan;

	private boolean criteria;

	private static final String SCAN = "Scan";
	private static final String GET = "Get";

	public HBaseQuery(HBaseOM hBaseOM) {
		this.hBaseOM = hBaseOM;
		this.omTranslator = hBaseOM.getOMTranslator();
	}

	public HBaseQuery(HBaseOM hBaseClient, String query) {
		this(hBaseClient);
		this.hbaseQuery = query;
		query();
	}

	public HBaseQuery(HBaseOM hBaseClient, Class<?> entityClass) {
		this(hBaseClient);
		scan(entityClass);
	}

	private Map<Integer, Object> getParameters() {
		if (parameters == null) {
			parameters = new HashMap<Integer, Object>();
		}
		return parameters;
	}

	private Map<Integer, FilterContainer> getFilters() {
		if (filters == null) {
			filters = new HashMap<Integer, FilterContainer>();
		}
		return filters;
	}

	private HBaseQuery query() {
		StringTokenizer tokenizer = new StringTokenizer(hbaseQuery);
		scan = tokenizer.nextToken();
		String htableEntity;
		if (scan.equals(SCAN) || scan.equals(GET)) {
			htableEntity = tokenizer.nextToken();
		} else {
			htableEntity = scan;
		}
		entityClass = hBaseOM.getEntities().get(htableEntity);
		omTranslator = hBaseOM.getOMTranslator();
		return this;
	}

	protected void setColumnFamilie(FilterContainer filterContainer) {
		HColumnFamily hColumnFamily = getField(entityClass,
				filterContainer.getField()).getAnnotation(HColumnFamily.class);
		filterContainer.setColumn(hColumnFamily.family());
		filterContainer.setQualifier(hColumnFamily.qualifier());
	}

	public HBaseQuery scan() {
		this.scan = SCAN;
		String entityName = entityClass.getSimpleName();
		Map<String, Class<?>> entities = hBaseOM.getEntities();
		Class<?> entityClassCheck = entities.get(entityName);
		if (entityClassCheck != null && !entityClassCheck.equals(entityClass)) {
			throw new IllegalArgumentException(
					"Another entity with the same name already registered");
		}
		entities.put(entityName, entityClass);
		entities.put(entityClass.getName(), entityClass);
		if (omTranslator == null) {
			omTranslator = hBaseOM.getOMTranslator();
		}
		criteria = true;
		return this;
	}

	/**
	 * Prepare scan operation
	 * 
	 * @param entityClass
	 */
	private void preScan(Class<?> entityClass) {
		this.scan = SCAN;
		String entityName = entityClass.getSimpleName();
		Map<String, Class<?>> entities = hBaseOM.getEntities();
		Class<?> entityClassCheck = entities.get(entityName);
		if (entityClassCheck != null && !entityClassCheck.equals(entityClass)) {
			throw new IllegalArgumentException(
					"Another entity with the same name already registered");
		}
		entities.put(entityName, entityClass);
		hBaseOM.getEntities().put(entityClass.getName(), entityClass);
		this.entityClass = entityClass;
		if (omTranslator == null) {
			omTranslator = hBaseOM.getOMTranslator();
		}
		criteria = true;
	}

	/**
	 * Scan HBase by query
	 * 
	 * @param entityClass
	 * @return
	 */
	public HBaseQuery scan(Class<?> entityClass) {
		preScan(entityClass);
		return this;
	}

	public HBaseQuery scan(String className) {
		this.scan = SCAN;
		this.entityClass = hBaseOM.getEntities().get(className);
		if (omTranslator == null) {
			omTranslator = hBaseOM.getOMTranslator();
		}
		criteria = true;
		return this;
	}

	public HBaseQuery query(String hbaseQuery) {
		this.hbaseQuery = hbaseQuery;
		return query();
	}

	private FilterContainer getFilterContainer(HQueryType type,
			String... columns) {
		FilterContainer filterContainer = new FilterContainer();
		filterContainer.sethQueryType(type);
		if (columns.length > 0) {
			String columnName = columns[0];
			filterContainer.setColumn(columnName);
			if (entityClass != null) {
				setColumnFamilie(filterContainer);
			}
		}

		return filterContainer;
	}

	private void fillHQueryFilter(HQueryFilter filter, HQueryType type,
			Integer value, String... columns) {
		FilterContainer filterContainer;
		Map<Integer, FilterContainer> filtersToFill;
		if (filter.equals(HQueryFilter.PREFIX)) {
			filterContainer = getFilterContainer(type, columns);
			filterContainer.sethQueryFilter(HQueryFilter.PREFIX);
			filtersToFill = getFilters();
			filtersToFill.put(value, filterContainer);
		} else if (filter.equals(HQueryFilter.EQUALES)) {
			filterContainer = getFilterContainer(type, columns);
			filterContainer.sethQueryFilter(HQueryFilter.EQUALES);
			filtersToFill = getFilters();
			filtersToFill.put(value, filterContainer);
		}
	}

	public HBaseQuery setFilter(HQueryType type, HQueryFilter filter,
			Integer value) {
		if (type.equals(HQueryType.ROW)) {
			fillHQueryFilter(filter, type, value);
		} else if (type.equals(HQueryType.COLUMN)) {
			fillHQueryFilter(filter, type, value);
		}

		return this;
	}

	public HBaseQuery setFilter(HQueryType type, HQueryFilter filter,
			String column, Integer value) {
		if (type.equals(HQueryType.ROW)) {
			fillHQueryFilter(filter, type, value, column);
		} else if (type.equals(HQueryType.COLUMN)) {
			fillHQueryFilter(filter, type, value, column);
		}

		return this;
	}

	public HBaseQuery setParameter(Integer paramNo, Object parameter) {
		getParameters().put(paramNo, parameter);
		return this;
	}

	public HBaseQuery setParameter(Integer paramNo, Object... parameters) {
		getParameters().put(paramNo, parameters);
		return this;
	}

	public abstract HBaseQuery setFirstResult(Object... args);

	public abstract HBaseQuery setLastResult(Object... args);

	public abstract HBaseQuery setInclusiveLastResult(Object... args);

	public abstract HBaseQuery setLastResult(boolean include, Object... args);

	public abstract HBaseQuery setMaxResults(int pages);

	public abstract HBaseQuery setMaxResults(long pages);

	public abstract HBaseQuery setMaxVersions(int versions);

	public Object getSingleResult() {
		return null;
	}

	public List<?> getResultList() throws IOException {
		return (List<?>) getResultCollection(List.class);
	}

	private void setRowFilters(List<Object> prefixes, HQueryFilter filter,
			Integer key, Object value) {
		if (filter.equals(HQueryFilter.PREFIX)) {
			// adds prefix filter array
			prefixes.add((key - 1), value);
		}
	}

	protected abstract void addPrefixes(List<Object> prefixes);

	private Collection<?> getFinallResult(Class<?> collectionType)
			throws IOException {
		if (criteria || (scan != null && !scan.equals("Get"))) {
			return omTranslator.scan(entityClass, collectionType);
		} else {
			return null;
		}
	}

	/**
	 * Returns result collections from scan statement
	 * 
	 * @param collectionType
	 * @return {@link Collection}
	 * @throws IOException
	 */
	public Collection<?> getResultCollection(Class<?> collectionType)
			throws IOException {

		if (parameters != null) {

			List<Object> prefixes = new ArrayList<Object>(parameters.size());
			Integer key;
			Object value;
			/* Loop through filter parameters */
			for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				FilterContainer filterContainer = getFilters().get(key);
				HQueryType type = filterContainer.gethQueryType();
				HQueryFilter filter = filterContainer.gethQueryFilter();
				if (type.equals(HQueryType.ROW)) {
					setRowFilters(prefixes, filter, key, value);
				}
			}
			addPrefixes(prefixes);
		}

		return getFinallResult(collectionType);
	}
}
