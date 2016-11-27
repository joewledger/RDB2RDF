package com.joeledger.rdb2rdf;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.jena.base.Sys;

import java.sql.SQLException;
import java.util.stream.*;
import java.util.*;


/**
 * Unit test for simple App.
 */
public class DBUtilsTest
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DBUtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DBUtilsTest.class );
    }


    public void testGetPrimaryKeys() throws SQLException {

        String dbName = "chinook.db";
        Assert.assertTrue(DBUtils.getPrimaryKeys(dbName, "albums").equals(Stream.of("AlbumId").collect(Collectors.toSet())));
        Assert.assertTrue(DBUtils.getPrimaryKeys(dbName, "playlist_track").equals(Stream.of("TrackId", "PlaylistId").collect(Collectors.toSet())));

    }

    public void testGetForeignKeys() throws SQLException {

        String dbName = "chinook.db";

        Map<String, String> playlistReferences = DBUtils.getForeignKeyConstraints(dbName, "playlist_track");
        Assert.assertEquals(playlistReferences.get("PlaylistId"), "playlists");
        Assert.assertEquals(playlistReferences.get("TrackId"), "tracks");

        Map<String, String> trackReferences = DBUtils.getForeignKeyConstraints(dbName, "tracks");
        Assert.assertEquals(trackReferences.get("MediaTypeId"), "media_types");
        Assert.assertEquals(trackReferences.get("AlbumId"), "albums");
        Assert.assertEquals(trackReferences.get("GenreId"), "genres");

        Map<String, String> employeeReferences = DBUtils.getForeignKeyConstraints(dbName, "employees");
        Assert.assertEquals(employeeReferences.get("ReportsTo"), "employees");

    }
}
