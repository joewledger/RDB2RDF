package com.joeledger.rdb2rdf;

import java.util.*;
import org.apache.commons.lang3.tuple.*;

public class Table {

    private String tableName;
    private Set<String> primaryKeys;
    private Map<String, String> references;
    private Set<String> columnSet;

    public Table(String tableName) {
        this.tableName = tableName;
        this.primaryKeys = new HashSet<>();
        this.references = new HashMap<>();
        this.columnSet = new HashSet<>();
    }

    public Set<Triple<String, String, String>> getTripleSet() {
        return null;
    }


    private boolean isManyToMany() {
        return false;
    }
    

}
