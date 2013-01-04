package org.hbaseom.client.meta.reflect;


import java.lang.reflect.Method;

import org.hbaseom.client.annotations.HCascadeType;

/**
 * 
 * @author levan
 * 
 */
public class HJoinColumnMethod {

    public static final String FAMILY_JOIN_SIZE_QUALIFIER = "s_z";

    private Method[] getters;

    private Method[] setters;

    private boolean eager;

    private Class<?> target;

    private boolean[] reverses;

    private boolean single;

    private Method targetSetter;

    private boolean match;

    private KeyField[] keyFields;

    private Method targetGetter;

    private HCascadeType[] hCascadeTypes;

    private HCascadeType all;

    private HCascadeType put;

    private HCascadeType delete;

    private HCascadeType replace;

    private String joinTable;

    private String joinEntity;

    private Class<?> hJoinEntity;

    private boolean collection;

    private boolean manyToOne;

    private boolean familySide;

    private boolean keyOnly;

    private String familyName;

    private Class<?> collectionClass;

    public Method[] getGetters() {
        return this.getters;
    }

    public void setGetters(Method[] getters) {
        this.getters = getters;
    }

    public Method[] getSetters() {
        return this.setters;
    }

    public void setSetters(Method[] setters) {
        this.setters = setters;
    }

    public boolean isEager() {
        return this.eager;
    }

    public void setEager(boolean eager) {
        this.eager = eager;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public boolean[] isReverses() {
        return reverses;
    }

    public void setReverses(boolean[] reverses) {
        this.reverses = reverses;
    }

    public boolean isSingle() {
        return this.single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public Method getTargetSetter() {
        return targetSetter;
    }

    public void setTargetSetter(Method targetSetter) {
        this.targetSetter = targetSetter;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public KeyField[] getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(KeyField[] keyFields) {
        this.keyFields = keyFields;
    }

    public Method getTargetGetter() {
        return targetGetter;
    }

    public void setTargetGetter(Method targetGetter) {
        this.targetGetter = targetGetter;
    }

    public HCascadeType[] gethCascadeTypes() {
        return hCascadeTypes;
    }

    public void sethCascadeTypes(HCascadeType[] hCascadeTypes) {
        this.hCascadeTypes = hCascadeTypes;
        for (HCascadeType hCascadeType : hCascadeTypes) {
            if (hCascadeType.equals(HCascadeType.ALL)) {
                setAll(hCascadeType);
            } else if (hCascadeType.equals(HCascadeType.PUT)) {
                setPut(hCascadeType);
            } else if (hCascadeType.equals(HCascadeType.DELETE)) {
                setDelete(hCascadeType);
            } else if (hCascadeType.equals(HCascadeType.REPLACE)) {
                setReplace(hCascadeType);
            }
        }
    }

    public HCascadeType getAll() {
        return this.all;
    }

    public void setAll(HCascadeType all) {
        this.all = all;
    }

    public HCascadeType getPut() {
        return this.put;
    }

    public void setPut(HCascadeType put) {
        this.put = put;
    }

    public HCascadeType getDelete() {
        return this.delete;
    }

    public void setDelete(HCascadeType delete) {
        this.delete = delete;
    }

    public HCascadeType getReplace() {
        return this.replace;
    }

    public void setReplace(HCascadeType replace) {
        this.replace = replace;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getJoinEntity() {
        return joinEntity;
    }

    public void setJoinEntity(String joinEntity) {
        this.joinEntity = joinEntity;
    }

    public Class<?> gethJoinEntity() {
        return hJoinEntity;
    }

    public void sethJoinEntity(Class<?> hJoinEntity) {
        this.hJoinEntity = hJoinEntity;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public boolean isManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(boolean manyToOne) {
        this.manyToOne = manyToOne;
    }

    public boolean isFamilySide() {
        return familySide;
    }

    public void setFamilySide(boolean familySide) {
        this.familySide = familySide;
    }

    public boolean isKeyOnly() {
        return keyOnly;
    }

    public void setKeyOnly(boolean keyOnly) {
        this.keyOnly = keyOnly;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Class<?> getCollectionClass() {
        return collectionClass;
    }

    public void setCollectionClass(Class<?> collectionClass) {
        this.collectionClass = collectionClass;
    }
}
