package com.joeledger.rdb2rdf;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang3.tuple.*;
import java.util.*;

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
}
