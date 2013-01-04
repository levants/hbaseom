package org.hbaseom.client.meta.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hbaseom.client.annotations.HColumnFamily;
import org.hbaseom.client.annotations.HMultiColumnFamily;
import org.hbaseom.client.annotations.RowKey;

/**
 * 
 * @author levan
 * 
 */
public class FieldMethod {

    private Field field;

    private Method setter;

    private Method getter;

    private HColumnFamily family;

    private HMultiColumnFamily mFamily;

    private RowKey rowKey;

    private boolean lob;

    private boolean multi;

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getSetter() {
        return this.setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public HColumnFamily getFamily() {
        return family;
    }

    public void setFamily(HColumnFamily family) {
        this.family = family;
    }

    public HMultiColumnFamily getMFamily() {
        return mFamily;
    }

    public void setMFamily(HMultiColumnFamily mFamily) {
        this.mFamily = mFamily;
    }

    public RowKey getRowKey() {
        return rowKey;
    }

    public void setRowKey(RowKey rowKey) {
        this.rowKey = rowKey;
    }

    public boolean isLob() {
        return lob;
    }

    public void setLob(boolean lob) {
        this.lob = lob;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

}
