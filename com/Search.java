package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import java.util.*;

public class Search {

    private Database db = new Database();
    public String searchResults = "";
    String[] primaryKeyArray;
    String[] foreignKeyArray;

    public void doSearch(String searchKeyword) {
        String[] entityRelationsArray = this.getEntityRelations();

        String eachRowData = "";
        // Displaying the available entity relationships
        String[] levelOneEntities = new String[entityRelationsArray.length];
        for (int t = 0; t < entityRelationsArray.length; t++) {
            String[] level1 = entityRelationsArray[t].split(db.COLNAMETYPESP);
            System.out.println(level1[0] + " : " + level1[1]);
            levelOneEntities[t] = level1[0];
            //eachRowData += entityRelationsArray[t];
        }

        Scanner input = new Scanner(System.in);

        System.out.println("----------------");

        String table = "";
        do {
            System.out.print("Select a table : ");
            table = input.next();

            if (table.equalsIgnoreCase("x"))
                break;

            boolean isFound = false;
            for (int t = 0; t < levelOneEntities.length; t++) {
                if (levelOneEntities[t].equalsIgnoreCase(table)) {
                    for (int i = 0; i < entityRelationsArray.length; i++) {
                        String[] level = entityRelationsArray[i].split(db.COLNAMETYPESP);
                        if (level[0].equalsIgnoreCase(table))
                            System.out.println(level[1]);
                    }

                    System.out.println(db.getEntity().getSearchables(table));
                    isFound = true;

                    System.out.println("----------------");
                }
            }

            if (!isFound)
                System.out.println("Not Found!\n----------------");

        } while (!table.equalsIgnoreCase(""));

        searchResults = eachRowData;

        System.exit(0);
        //System.out.println(eachRowData);//*/
        //System.exit(0); // @TODO : just stopping the execution. Needs to be handled by the communigram
    } // function end

    public void processEntityRelations () {
        String[] tableArray = db.getFilteredTables();

        int totalKeyColumns = 0;
        for (int t = 0; t < tableArray.length; t++) {
            Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, null);
            totalKeyColumns += resultsets.length;
        }
        primaryKeyArray = new String[totalKeyColumns];
        foreignKeyArray = new String[totalKeyColumns];

        int primarykeycount = 0;
        int foreignkeycount = 0;
        // begin: traversing through each table
        for (int t = 0; t < tableArray.length; t++) {
            // need only the tables which contain searchable attributes
            if (!db.getEntity().getSearchables(tableArray[t]).equalsIgnoreCase("")) {
                // sending the query to the database to retrieve data
                Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, null);

                // begin :traversing through each table row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        Map<String, String> resultset = resultsets[i];
                        String columnname = "";
                        for (Map.Entry<String, String> entry : resultset.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();

                            if (key.equalsIgnoreCase("COLUMN_NAME")) {
                                columnname = value;
                            }

                            if (key.equalsIgnoreCase("CONSTRAINT_NAME")) {
                                if (value.matches(".*fk.*")) {
                                    foreignKeyArray[foreignkeycount] = tableArray[t] + db.COLNAMETYPESP + columnname;
                                    foreignkeycount++;
                                }
                                if (value.matches(".*PK.*")) {

                                    primaryKeyArray[primarykeycount] = tableArray[t] + db.COLNAMETYPESP + columnname;
                                    primarykeycount++;
                                }
                            }
                        }
                    }
                } // end :traversing through each table row to display data
            }
        } // end: traversing through each table
    }

    public String[] getEntityRelations () { //(String[] primaryKeyArray, String[] foreignKeyArray) {
        this.processEntityRelations ();
        String eachEntity = "";
        String relatedEntity = "";
        String nextForeignKeyTable = "";
        String nextForeignKey = null;
        String entityRelations = "";
        for (int f = 0; f < foreignKeyArray.length; f++) {
            if (foreignKeyArray[f] != null) {
                String foreignKey = foreignKeyArray[f];

                try {
                    nextForeignKey = foreignKeyArray[f+1];
                    String[] nextForeignKeySplits = nextForeignKey.split(db.COLNAMETYPESP);
                    nextForeignKeyTable = nextForeignKeySplits[0];
                } catch (Exception ex) {}

                String[] foreignKeySplits = foreignKey.split(db.COLNAMETYPESP);
                //System.out.println(eachEntity + " || " + foreignKeySplits[0] + " || " + nextForeignKeyTable);
                if (!eachEntity.equalsIgnoreCase(foreignKeySplits[0])) {
                    eachEntity = foreignKeySplits[0];
                    relatedEntity = "";
                }

                for (int p = 0; p < primaryKeyArray.length; p++) {
                    if (primaryKeyArray[p] != null) {
                        String primaryKey = primaryKeyArray[p];
                        String[] primaryKeySplits = primaryKey.split(db.COLNAMETYPESP);

                        //System.out.println ("F" + foreignKeySplits[1] + "wwww"+ primaryKeySplits[0] + primaryKeySplits[1]);
                        if (foreignKeySplits[1].equalsIgnoreCase(primaryKeySplits[1])) {
                            if (relatedEntity.equalsIgnoreCase("")) {
                                relatedEntity += primaryKeySplits[0];
                            } else {
                                relatedEntity += "," + primaryKeySplits[0];
                            }
                        }
                    }
                }

                if ((!nextForeignKeyTable.equalsIgnoreCase(foreignKeySplits[0]) || nextForeignKey == null) && !relatedEntity.equalsIgnoreCase(""))
                    entityRelations += foreignKeySplits[0] + db.COLNAMETYPESP + relatedEntity + db.COLNAMETYPESP+db.COLNAMETYPESP;
            }
        }
        entityRelations = entityRelations.substring(0, entityRelations.length()-2);
        String[] entityRelationsArray = entityRelations.split(db.COLNAMETYPESP+db.COLNAMETYPESP);

        return entityRelationsArray;
    }
}
