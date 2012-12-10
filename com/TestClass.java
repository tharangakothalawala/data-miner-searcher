package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import database.plugins.Entity;
import java.util.*;

public class TestClass {

    private Database db = new Database();
    private SearchGUI gui = new SearchGUI();

    public void TestClass() {
    }

    public void testLoad() {
        Entity entity = new Entity();
        String searchEntity = "users";
        String searchKeyword = "s";

        String searchables = "";

        /*System.out.println(entity.getSearchables(searchEntity));
        System.out.println(entity.makeClause(entity.getSearchables(searchEntity), searchKeyword));

        //gui.showSearchWindow();
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

        db.getTables();
        db.getViews();
        String[] tableArray = db.getFilteredTables();

        for (int t = 0; t < tableArray.length; t++) {
            //     System.out.println(t + "). " + tableArray[t]);
            //     System.out.println(t + "). " + entity.getSearchables(tableArray[t]));
            //     System.out.println(t + "). " + entity.makeClause(entity.getSearchables(tableArray[t]), searchKeyword));

            if (entity.getSearchables(tableArray[t]) != "") {
                Map[] resultsets = db.sqlSelect(tableArray[t], entity.getSearchables(tableArray[t]), entity.makeClause(entity.getSearchables(tableArray[t]), searchKeyword), null, null, null, null);

                //int arrLength = resultsets.length;
                System.out.println(t + "). " + resultsets.length + "################################## " + tableArray[t] + " ##################################");

                // traversing through each row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        Map<String, String> resultset = resultsets[i];

                        System.out.println("== ROW: " + i + " ========================");
                        for (Map.Entry<String, String> entry : resultset.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            System.out.println("['" + key + "'] = " + value);
                        }
                    }
                }//*/
            }
        }//*/
    }

    public static void main(String[] args) {
        TestClass tc = new TestClass();
        tc.testLoad();
    }
}
