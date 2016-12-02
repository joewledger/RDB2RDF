package com.joeledger.rdb2rdf;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import org.apache.commons.lang3.tuple.*;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.URIref;

public class App 
{

    public static void main( String[] args ) {

        String dbName = "chinook.db";
        Set<Triple<String, String, String>> rdfTriples = new HashSet<>();
        RdfModel m = new RdfModel();

        if(m.createTdb){ //set createTdb true when creating TDB for the first time
        	try {
        		for(String tableName : DBUtils.getTableNames(dbName)) {
        			Table table = new Table(dbName, tableName);
        			table.fillWithMetadata();
        			rdfTriples.addAll(table.getTripleSet());
        		}
        	} catch (SQLException e) {
        		System.out.println("Unable to access database");
        	}
        	
        	/*
        	 * Works specifically for the string triples generated
        	 * bad coding 
        	 */
        	for (Triple<String, String, String> s : rdfTriples) {
                //System.out.println(s);
                String subject = s.getLeft();
                String predicate = s.getMiddle();
                String object = s.getRight();
                
                if(object!=null){
                	Pattern pattern = Pattern.compile("(.*?)/(.*?)"); 
                    Matcher mat = pattern.matcher(object);
                    if(mat.matches()){ //any object with '/' considered resource else literal
                    	m.model.createResource(subject)
                		.addProperty(ResourceFactory.createProperty(predicate), 
                				ResourceFactory.createResource(object));
                    }
                    else {
                    	m.model.createResource(URIref.encode(subject))
                    	.addProperty(ResourceFactory.createProperty(predicate), 
                           		object);
                    }
                }
                else {
                	object="unknown";
                	m.model.createResource(URIref.encode(subject))
                	.addProperty(ResourceFactory.createProperty(predicate), 
                       		object);
                }
                        
            }
        }
        m.query();
    }
}