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

        try {

            Set<String> tableNames = getTableNames();
            System.out.println(tableNames);

            for(String t : tableNames){
                System.out.println(getAttributeNames(t));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public static Set<String> getAttributeNames(String tableName) throws SQLException {
        Set<String> attributeNames = new HashSet<>();

        Connection connection = connect();

        String query = String.format("SELECT * FROM %s", tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++ ) {
            attributeNames.add(rsmd.getColumnName(i));
        }
        return attributeNames;
    }

    public static Set<String> getTableNames() throws SQLException {
        Set<String> tableNames = new HashSet<>();
        Connection connection = connect();
        DatabaseMetaData md  = connection.getMetaData();

        ResultSet rs = md.getTables(null, null, "%", new String[] {"TABLE"});
        while (rs.next()) {
            tableNames.add(rs.getString("TABLE_NAME"));
        }

        return tableNames.stream().filter(s -> !s.startsWith("sqlite")).collect(Collectors.toSet());
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
