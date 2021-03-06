package com.joeledger.rdb2rdf;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DBUtils {

    public static Connection connect(String dbName) {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbName));
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }

    public static void executeQuery(String dbName, String query) throws SQLException {
        Connection connection = DBUtils.connect(dbName);

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        while(rs.next()) {
            StringBuilder colValues = new StringBuilder();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) colValues.append(",  ");
                colValues.append(rs.getString(i));
            }
            System.out.println(colValues.toString());
        }
    }


    public static int getRowCount(String dbName, String tableName) {
        Connection connection = DBUtils.connect(dbName);
        String query = String.format("SELECT count(*) FROM %s", tableName);

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            return result.getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }


    public static ResultSet getFullTableResultSet(String dbName, String tableName) throws SQLException{
        Connection connection = DBUtils.connect(dbName);
        String query = String.format("SELECT * FROM %s;", tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
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

    public static Set<String> getColumnNames(String dbName, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();

        Connection connection = DBUtils.connect(dbName);

        String query = String.format("SELECT * FROM %s", tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(rsmd.getColumnName(i));
        }
        return columns;
    }

    public static Set<String> getPrimaryKeys(String dbName, String tableName) throws SQLException {
        Set<String> pKeys = new HashSet<>();

        DatabaseMetaData dm = DBUtils.connect(dbName).getMetaData();
        ResultSet rs = dm.getPrimaryKeys(null, null, tableName);
        while(rs.next()) {
            pKeys.add(rs.getString("COLUMN_NAME"));
        }

        return pKeys;
    }

    public static Map<String, String> getForeignKeyConstraints(String dbName, String tableName) throws SQLException {
        Map<String, String> foreignKeys = new HashMap<>();

        Connection connection = DBUtils.connect(dbName);

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
        while (rs.next()) {
            foreignKeys.put(rs.getString("FKCOLUMN_NAME"),rs.getString("PKTABLE_NAME"));
        }

        return foreignKeys;
    }
}
