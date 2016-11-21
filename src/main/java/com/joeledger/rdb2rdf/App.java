package com.joeledger.rdb2rdf;

import java.util.Set;
import java.util.HashSet;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.sql.*;
import java.util.stream.Collectors;

public class App 
{

    public static void main( String[] args ) {

        Set<MutableTriple<String, String, String>> rdfTriples = new HashSet<>();
        rdfTriples.add(new MutableTriple<>("a", "b", "c"));

        getTableNames().stream().forEach(System.out::println);
    }

    public static Set<String> getTableNames(){
        Set<String> tableNames = new HashSet<>();
        try {
            Connection connection = connect();
            DatabaseMetaData md  = connection.getMetaData();

            ResultSet rs = md.getTables(null, null, "%", new String[] {"TABLE"});
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }

            return tableNames.stream().filter(s -> !s.startsWith("sqlite")).collect(Collectors.toSet());
        } catch (Exception e) {
            return null;
        }
    }

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:chinook.db");
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }

}
