package com.joeledger.rdb2rdf;

import java.sql.*;
import java.util.*;
import org.apache.commons.lang3.tuple.*;

public class Table {

    private String dbName;
    private String tableName;
    private Set<String> primaryKeys;
    private Map<String, String> references;
    private Set<String> columnSet;

    public Table(String dbName, String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.primaryKeys = new HashSet<>();
        this.references = new HashMap<>();
        this.columnSet = new HashSet<>();
        this.fillWithMetadata();
    }

    public Set<Triple<String, String, String>> getTripleSet() {
        return null;
    }


    private boolean isManyToMany() {
        return false;
    }

    private void fillWithMetadata() {


    }

    public void getColumnNames(String tableName) throws SQLException {
        Connection connection = DBUtils.connect(this.dbName);

        String query = String.format("SELECT * FROM %s", tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            this.columnSet.add(rsmd.getColumnName(i));
        }
    }
}
