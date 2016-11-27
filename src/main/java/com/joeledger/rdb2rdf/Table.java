package com.joeledger.rdb2rdf;

import java.sql.*;
import java.util.*;
import org.apache.commons.lang3.tuple.*;

public class Table {

    private String dbName;
    private String tableName;
    private Set<String> primaryKeys;
    private Map<String, String> foreignKeyReferences;
    private Set<String> columnSet;

    public Table(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.primaryKeys = new HashSet<>();
        this.foreignKeyReferences = new HashMap<>();
        this.columnSet = new HashSet<>();
    }

    public void fillWithMetadata() {
        try {
            setColumnNames();
            setPrimaryKeys();
            setForeignKeyReferences();
        } catch (SQLException e) {
            System.out.println("Failed to populate table metadata");
        }
    }

    public Set<Triple<String, String, String>> getTripleSet() {
        return isManyToMany() ? getManyToManyTripleSet() : getStandardTripleSet();
    }

    private Set<Triple<String, String, String>> getStandardTripleSet() {
        return null;
    }

    private Set<Triple<String, String, String>> getManyToManyTripleSet() {
        return null;
    }

    private boolean isManyToMany() {
        return this.columnSet.size() == this.primaryKeys.size();
    }

    private void setColumnNames() throws SQLException {
        this.columnSet = DBUtils.getColumnNames(this.dbName, this.tableName);
    }

    private void setPrimaryKeys() {
        return;
    }

    private void setForeignKeyReferences() {
        return;
    }

    public String toString(){
        return this.tableName;
    }
}
