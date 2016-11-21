package com.joeledger.rdb2rdf;

import java.util.Set;
import java.util.HashSet;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.sql.*;

public class App 
{
    public static void main( String[] args ) {

        Set<MutableTriple<String, String, String>> rdfTriples = new HashSet<>();
        rdfTriples.add(new MutableTriple<>("a", "b", "c"));

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:chinook.db");

            PreparedStatement statement = c.prepareStatement("SELECT Name from genres");
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                System.out.println(result.getString("Name"));
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }
}
