package com.joeledger.rdb2rdf;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.TDBFactory;
//import org.apache.jena.query.ResultSetFormatter;

public class RdfModel {
	
	public Model model;
	public boolean createTdb = false; //set to True for populating TDB first time
	private Location location = Location.create("data");
	private Dataset dataset;
	
	public RdfModel(){
		this.dataset = TDBFactory.createDataset(location);
		this.model = dataset.getDefaultModel();
	}
	
	public void query(){
		
		//model.write(System.out, "N-TRIPLES");
		
		String qst = 
        		" SELECT * WHERE { ?s <rdf:type> ?o } ";
               
		dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(qst);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet results = qexec.execSelect();
                while ( results.hasNext() ) {
                    QuerySolution soln = results.nextSolution();
                    System.out.println(soln);
                }
            } finally {
                qexec.close();
            }
        } finally {
            dataset.end();
        }
        
		
        /*String queryString = "SELECT * { ?s ?p ?o }";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        try{
        	ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(System.out, results);
        }
        finally{
        	qexec.close();
        	dataset.end();
        }
        */
	}
	
	
}
