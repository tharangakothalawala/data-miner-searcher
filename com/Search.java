package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import database.models.UsersSearchModel;
import com.graph.*;
import java.util.*;
import java.sql.*;

public class Search {

    private Database db = new Database();
    public String searchResults = "";
    public UsersSearchModel[] usermodel;
    //public int userRecordTracker = 0;
    Graph[] entitiyGraphs;

    public void populateGraph() {
        String[] tableArray = db.getFilteredTables();
        entitiyGraphs = new Graph[tableArray.length];

        // begin: traversing through each table
        for (int t = 0; t < tableArray.length; t++) {
            // need only the tables which contain searchable attributes
            if (!db.getEntity().getSearchables(tableArray[t]).equalsIgnoreCase("")) {
                // sending the query to the database to retrieve data
                Map[] resultsets = db.sqlSelect(tableArray[t], db.getEntity().getSearchables(tableArray[t]), null, null, null, null, null);

                //System.out.println((t+1) + "). datarows: " + resultsets.length + " ################################## " + tableArray[t] + " ##################################");
                Graph theGraph = new Graph();
                theGraph.addVertex(new HashMap<String, String>()); // add as a initial item

                // begin :traversing through each table row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        theGraph.addVertex(resultsets[i]);
                        theGraph.addEdge(i, (i + 1)); // creating edges
                    }
                } // end :traversing through each table row to display data

                // saving each entity data into graphs, so that we can query later
                entitiyGraphs[t] = theGraph;
            }
        } // end: traversing through each table
    }

    public void doGraphSearch(String searchKeyword) {
        // Now we start contructing relational data mapping within graphs, or in other words SQL JOIN in theory
        System.out.println("name");
        entitiyGraphs[0].dfs("name", searchKeyword);
        
        for (Map.Entry<String, String> entry : entitiyGraphs[0].queryResult.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(">>['" + key + "'] = " + value);
        }
        //System.out.println("1user_id");
        //entitiyGraphs[0].dfs("user_id", searchKeyword);
        System.out.println("2user_id");
        entitiyGraphs[3].dfs("user_id", ""+10);
        for (Map.Entry<String, String> entry : entitiyGraphs[3].queryResult.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(">>['" + key + "'] = " + value);
        }
        //System.out.println("rsrc_name");
        //entitiyGraphs[3].dfs("rsrc_name", ""+10);
    }

    public void graphJoin () {
        
    }
}
