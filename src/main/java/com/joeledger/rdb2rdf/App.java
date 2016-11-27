package com.joeledger.rdb2rdf;

import java.util.*;
import java.sql.*;
import org.apache.commons.lang3.tuple.*;

public class App 
{

    public static void main( String[] args ) {

        String dbName = "chinook.db";
        Set<Triple<String, String, String>> rdfTriples = new HashSet<>();
        Map<String, Table> tableMap = new HashMap<>();

        try {
            for(String tableName : DBUtils.getTableNames(dbName)) {
                Table table = new Table(dbName, tableName);
                table.fillWithMetadata();
                tableMap.put(tableName, table);
                rdfTriples.addAll(table.getTripleSet());
            }
        } catch (SQLException e) {
            System.out.println("Unable to access database");
        }

        System.out.println(rdfTriples);
    }
}