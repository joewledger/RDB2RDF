package com.joeledger.rdb2rdf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Joe Ledger on 11/22/2016.
 */
public class DBUtils {

    public static Connection connect(String dbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbName));
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }

}
