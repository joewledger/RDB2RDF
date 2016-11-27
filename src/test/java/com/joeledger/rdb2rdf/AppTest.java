package com.joeledger.rdb2rdf;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang3.tuple.*;
import org.apache.jena.base.Sys;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


    public void testManyToManyConversion () {
        String dbName = "chinook.db";
        String tableName = "playlist_track";

        Table table = new Table(dbName, tableName);
        table.fillWithMetadata();

        Set<Triple<String, String, String>> rdfTriples = table.getTripleSet();

        int rowCount = DBUtils.getRowCount(dbName, tableName);

        Assert.assertEquals(rdfTriples.size(), rowCount * 2);
        Assert.assertEquals(rdfTriples.stream().filter(t -> t.getLeft().startsWith("<playlists/")).count(), rowCount);
        Assert.assertEquals(rdfTriples.stream().filter(t -> t.getLeft().startsWith("<tracks/")).count(), rowCount);

        Assert.assertTrue(rdfTriples.stream().allMatch(s -> s.getLeft().substring(0, s.getLeft().indexOf("/"))
                                                    .equals(s.getMiddle().substring(0, s.getMiddle().indexOf("#")))));
    }

    public void testStandardConversion() throws SQLException{
        String dbName = "chinook.db";
        String tableName = "albums";

        Table table = new Table(dbName, tableName);
        table.fillWithMetadata();

        Set<Triple<String, String, String>> rdfTriples = table.getTripleSet();

        final Set<String> columnNames = DBUtils.getColumnNames(dbName, tableName);

        int rowCount = DBUtils.getRowCount(dbName, tableName);
        int colCount = columnNames.size();

        System.out.println(rdfTriples);


        //Check to make sure that the one tuple is created for each row-column combination in the table
        Assert.assertEquals(rdfTriples.size(), rowCount * colCount);

        //Checks that all tuples with the "rdf:type" predicate also have the table name as an object
        Assert.assertTrue(rdfTriples.stream()
                .filter(t -> t.getMiddle().equals("rdf:type"))
                .allMatch(t -> t.getRight().equals(String.format("<%s>", tableName))));

        //Checks to make sure that all columns except the primary key are used as predicates
        Assert.assertEquals(rdfTriples.stream()
                .map(Triple::getMiddle)
                .filter(s -> !s.equals("rdf:type"))
                .map(s -> s.substring(Math.max(s.indexOf("#") + 1, 0)))
                .map(s -> s.substring(0, s.endsWith(">") ? s.length() - 1 : s.length()))
                .map(s -> s.substring(s.startsWith("ref-") ? 4 : 0))
                .collect(Collectors.toSet()).size() + 1, colCount);
    }
}
