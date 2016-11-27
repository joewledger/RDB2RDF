package com.joeledger.rdb2rdf;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
            this.columnSet = DBUtils.getColumnNames(this.dbName, this.tableName);
            this.primaryKeys = DBUtils.getPrimaryKeys(this.dbName, this.tableName);
            this.foreignKeyReferences = DBUtils.getForeignKeyConstraints(this.dbName, this.tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<Triple<String, String, String>> getTripleSet() {
        try {
            return isManyToMany() ? getManyToManyTripleSet() : getStandardTripleSet();
        } catch (SQLException e){
            return null;
        }
    }

    private Set<Triple<String, String, String>> getStandardTripleSet() throws SQLException {
        return null;


    }

    private Set<Triple<String, String, String>> getManyToManyTripleSet() throws SQLException {
        Set<Triple<String, String, String>> rdfTriples = new HashSet<>();
        ResultSet rdbTuples = DBUtils.getFullTableResultSet(this.dbName, this.tableName);

        List<String> referenceColumns = this.foreignKeyReferences.keySet().stream().collect(Collectors.toList());

        while(rdbTuples.next()) {
            String subject1 = getManyToManySubject(rdbTuples, referenceColumns.get(0));
            String subject2 = getManyToManySubject(rdbTuples, referenceColumns.get(1));

            String predicate1 = getManyToManyPredicateString(referenceColumns.get(0));
            String predicate2 = getManyToManyPredicateString(referenceColumns.get(1));

            rdfTriples.add(new ImmutableTriple<>(subject1, predicate1, subject2));
            rdfTriples.add(new ImmutableTriple<>(subject2, predicate2, subject1));
        }
        return rdfTriples;
    }

    private String getManyToManySubject(ResultSet rdbTuples, String referenceColumn) throws SQLException{
        String referencedTable = this.foreignKeyReferences.get(referenceColumn);
        return String.format("<%s/%s=%s>", referencedTable, referenceColumn, rdbTuples.getString(referenceColumn));
    }

    private String getManyToManyPredicateString(String referenceColumn) {
        return String.format("<%s#%s>", this.foreignKeyReferences.get(referenceColumn), this.tableName);
    }


    private boolean isManyToMany() {
        return this.columnSet.size() == this.primaryKeys.size();
    }

    public String toString(){
        return this.tableName;
    }
}
