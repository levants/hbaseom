package org.hbaseom.client.translators;

import java.util.List;
import java.util.Map;

public class Relations {

    private List<RelationContainer> containers;

    private Map<String, List<byte[]>> familySides;

    public List<RelationContainer> getContainers() {
        return this.containers;
    }

    public void setContainers(List<RelationContainer> containers) {
        this.containers = containers;
    }

    public Map<String, List<byte[]>> getFamilySides() {
        return this.familySides;
    }

    public void setFamilySides(Map<String, List<byte[]>> familySides) {
        this.familySides = familySides;
    }
}
