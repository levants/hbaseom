package org.hbaseom.client.query;

import org.hbaseom.client.filters.HQueryFilter;
import org.hbaseom.client.filters.HQueryType;

/**
 * Container class to keep {@link HBaseQuery} filters for translation in natural
 * HBase filters
 * 
 * @author levan
 * 
 */
public class FilterContainer {

	private HQueryType hQueryType;

	private HQueryFilter hQueryFilter;

	private String column;

	private String qualifier;

	private String field;

	public HQueryType gethQueryType() {
		return this.hQueryType;
	}

	public void sethQueryType(HQueryType hQueryType) {
		this.hQueryType = hQueryType;
	}

	public HQueryFilter gethQueryFilter() {
		return this.hQueryFilter;
	}

	public void sethQueryFilter(HQueryFilter hQueryFilter) {
		this.hQueryFilter = hQueryFilter;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}
