package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import database.models.UsersSearchModel;
import database.plugins.Entity;
import com.graph.*;
import java.util.*;
import java.sql.*;

public class Search {

    private Database db = new Database();
    public String searchResults = "";
    public UsersSearchModel[] usermodel;
    //public int userRecordTracker = 0;
    Graph[] entitiyGraphArray;

    public void populateGraph() {
        String[] tableArray = db.getFilteredTables();
        entitiyGraphArray = new Graph[tableArray.length];

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
                entitiyGraphArray[t] = theGraph;
            }
        } // end: traversing through each table
    }

    public void doGraphSearch(String[]  entity, String[]  entityField, String searchKeyword) {
        searchResults = "";
        // Now we can start contructing relational data mapping within graphs, or in other words SQL JOIN in theory

        /*for (int f = 0; f < entity.length; f++) {
            System.out.println("entity: " + entity[f] + ", searchKeyword: " + searchKeyword);
        }
        for (int f = 0; f < entityField.length; f++) {
            System.out.println("entityField: " + entityField[f] + ", searchKeyword: " + searchKeyword);
        }//*/

        try {
            for (int j = 0; j < entity.length; j++) {
                System.out.println("entity: '" + entity[j] + "', entityField: '" + entityField[j] + "'" + ", searchKeyword: '" + searchKeyword + "'");
            if (entity[j] != null) { //  (!entity[j].equalsIgnoreCase(""))

            //Map[] resultsets = entitiyGraphArray[0].dfs("name", searchKeyword);
            //Map[] resultsets = this.graphSelect("USERS", "name", searchKeyword);
            Map[] resultsets = this.graphSelect(entity[j], entityField[j], searchKeyword);

            // begin :traversing through each table row to display data
            if (resultsets.length > 0) {
                for (int i = 0; i < resultsets.length; i++) {
                    Map<String, String> resultset = resultsets[i];

                    System.out.println("== ROW: " + i + " ========================");
                    for (Map.Entry<String, String> entry : resultset.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        System.out.println("['" + key + "'] = " + value);
                        if (entityField[j].equalsIgnoreCase(key)) // uncomment this if statement to display all the search results
                            searchResults += "\n['" + key + "'] = " + value;
                    }
                }
            } // end :traversing through each table row to display data//*/

            }
        }

        } catch (Exception ex) {
        }
    }

    public Map[] graphSelect(String table, String whereColumn, String searchKeyword) {
        Entity entity = new Entity();
        String[] dbtableArray = entity.getSearchableTables();
        int triggeredTable = 0;
        for (int i = 0; i < dbtableArray.length; i++) {
            if (table == dbtableArray[i]) {
                triggeredTable = i;
            }
        }
        //System.out.println("triggeredTable: " + triggeredTable);
        //entitiyGraphArray[triggeredTable].dfs(whereColumn, searchKeyword);
        Map[] resultsets = entitiyGraphArray[triggeredTable].dfs(whereColumn, searchKeyword);
        //System.out.println("USERS: name: " + entitiyGraphArray[triggeredTable].map.get(columns));

        return resultsets;
        //entitiyGraphArray[triggeredTable].dfs("user_id", entitiyGraphArray[0].map.get("columns"));
    }
}
