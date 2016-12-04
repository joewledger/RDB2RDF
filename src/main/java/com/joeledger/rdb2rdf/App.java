package com.joeledger.rdb2rdf;

import java.util.*;
import org.apache.commons.lang3.tuple.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

public class App 
{

    public static void main( String[] args ) {

        Set<Triple<String, String, String>> rdfTriples = getRDFTripleSet("chinook.db");
        Model model = buildModel(rdfTriples);

        String query = "SELECT ?name WHERE {?s <customers:City> \"Salt Lake City\" .\n ?s <customers:FirstName> ?name .}";
        executeQuery(query, model);
    }

    public static Set<Triple<String, String, String>> getRDFTripleSet(String dbName){
        Set<Triple<String, String, String>> rdfTriples = new HashSet<>();

        try {
            for(String tableName : DBUtils.getTableNames(dbName)) {
                Table table = new Table(dbName, tableName);
                table.fillWithMetadata();
                rdfTriples.addAll(table.getTripleSet());
            }
        } catch (Exception e) {
            System.out.println("Unable to access database");
        }

        return rdfTriples;
    }

    public static Model buildModel(Set<Triple<String, String, String>> tripleSet) {
        Model model = ModelFactory.createDefaultModel();

        for(Triple<String, String, String> r : tripleSet){
            if(r.getRight() == null) continue;
            Resource subject = model.createResource(r.getLeft());
            Property predicate = model.createProperty(r.getMiddle());
            RDFNode object = r.getMiddle().equals("rdf:type") ? model.createResource(r.getRight()) : model.createLiteral(r.getRight());

            Statement s = ResourceFactory.createStatement(subject, predicate, object);
            model.add(s);
        }

        return model;
    }

    public static void executeQuery(String qst, Model model){
        Query query = QueryFactory.create(qst);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                System.out.println(soln);
            }
        } finally {
            qexec.close();
        }
    }



}