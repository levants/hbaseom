package org.hbaseom.client.translators.util;

/**
 * 
 * @author levan
 *
 */
public class FirstMatches {

	private Class<?>[] matchClasses;

	private Object[] matchObjects;

	public Class<?>[] getMatchClasses() {
		return matchClasses;
	}

	public void setMatchClasses(Class<?>[] matchClasses) {
		this.matchClasses = matchClasses;
	}

	public Object[] getMatchObjects() {
		return matchObjects;
	}

	public void setMatchObjects(Object[] matchObjects) {
		this.matchObjects = matchObjects;
	}
}
