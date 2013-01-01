package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import java.util.*;

public class Search {

    private Database db = new Database();

    public void doSearch(String searchKeyword) {
        //Entity entity = new Entity();

        /*System.out.println(entity.getSearchables(searchEntity));
        System.out.println(entity.makeClause(entity.getSearchables(searchEntity), searchKeyword));

        //gui.showSearchGUI();
        //Map[] resultsets = db.sqlSelect("CALENDAR", "*", "clndr_id = '10' OR clndr_id = '20'", null, null, null, null);
        Map[] resultsets = db.sqlSelect(searchEntity, entity.getSearchables(searchEntity), entity.makeClause(entity.getSearchables(searchEntity), searchKeyword), null, null, null, null);



        //Map<String, String> rs = resultsets[0];
        //System.out.println("DATA: "+ rs.get("clndr_type"));

        // traversing through each row to display data
        for (int i = 0; i < resultsets.length; i++) {
        Map<String, String> resultset = resultsets[i];

        System.out.println("== ROW: " + i + " ========================");
        for (Map.Entry<String, String> entry : resultset.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        System.out.println("['" + key + "'] = " + value);
        }
        }//*/

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        String[] tableArray = db.getFilteredTables();

        // begin: traversing through each table
        for (int t = 0; t < tableArray.length; t++) {
            //     System.out.println(t + "). " + tableArray[t]);
            //     System.out.println(t + "). " + entity.getSearchables(tableArray[t]));
            //     System.out.println(t + "). " + entity.makeClause(entity.getSearchables(tableArray[t]), searchKeyword));

            // need only the tables which contain searchable attributes
            if (!db.getEntity().getSearchables(tableArray[t]).equalsIgnoreCase("")) {
                // sending the query to the database to retrieve data
                Map[] resultsets = db.sqlSelect(tableArray[t], db.getEntity().getSearchables(tableArray[t]), db.getEntity().makeClause(db.getEntity().getSearchables(tableArray[t]), searchKeyword), null, null, null, null);

                System.out.println((t+1) + "). datarows: " + resultsets.length + " ################################## " + tableArray[t] + " ##################################");

                // begin :traversing through each table row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        Map<String, String> resultset = resultsets[i];

                        System.out.println("== ROW: " + (i+1) + " ========================");
                        for (Map.Entry<String, String> entry : resultset.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            System.out.println("['" + key + "'] = " + value);
                        }
                    }
                } // end :traversing through each table row to display data
            }
        } // end: traversing through each table
    } // function end
}
