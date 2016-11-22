package com.joeledger.rdb2rdf;

import java.util.*;
import java.util.stream.*;
import java.sql.*;
import org.apache.commons.lang3.tuple.*;

public class App 
{

    public static void main( String[] args ) {

        String dbName = "chinook.db";
        Set<Triple<String, String, String>> rdfTriples = new HashSet<>();

        try {
            for(String tableName : getTableNames(dbName)) {
                Table table = new Table(dbName, tableName);
                rdfTriples.addAll(table.getTripleSet());
                System.out.println(rdfTriples);
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database");
        }
    }

    public static Set<String> getTableNames(String dbName) throws SQLException {
        Set<String> tableNames = new HashSet<>();
        Connection connection = DBUtils.connect(dbName);
        DatabaseMetaData md  = connection.getMetaData();

        ResultSet rs = md.getTables(null, null, "%", new String[] {"TABLE"});
        while (rs.next()) {
            tableNames.add(rs.getString("TABLE_NAME"));
        }

        return tableNames.stream().filter(s -> !s.startsWith("sqlite")).collect(Collectors.toSet());
    }
}