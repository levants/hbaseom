package org.hbaseom.client.meta.reflect;



/**
 * 
 * @author levan
 * 
 */
public class RelationTarget {

    private Class<?> target;

    private KeyField[] keyFields;

    public Class<?> getTarget() {
        return this.target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public KeyField[] getKeyFields() {
        return this.keyFields;
    }

    public void setKeyFields(KeyField[] keyFields) {
        this.keyFields = keyFields;
    }

}
