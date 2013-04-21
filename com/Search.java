
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package com;

import database.*;
import java.util.*;

public class Search {

    private Database db = new Database();
    private Query query;

    /*
     * this is the limit where we can display the suggestable tables
     */
    private static int ENTITY_DISPLAY_LIMIT;

    /*
     * If the searchable table count is more than the ENTITY_DISPLAY_LIMIT, the number of suggestable table is too much to select from. So it will search in all.
     */
    private static String OVERFLOW = "Too many categories found! Will search in ALL";

    /*
     * Used Values,
     * 1: searching the specified table / category
     * 2: searching in ALL tables which have been defined in the XML
     * 3: searching in ALL tables which have been suggested
     */
    private int searchMode = 0;

    /*
     * This will store the SQL query generation support data depending on the user input.
     * And this will be used to create automated SQL JOIN queries
     */
    String[] rawUserInputData;

    /*
     * This will store the SQL query generation support data for single SQL statements.
     */
    String[] irrelationalRawUserInputData;

    /*
     * This stores all the tables with their primary keys
     */
    String[] primaryKeyArray;

    /*
     * This stores all the tables with their foreign keys
     */
    String[] foreignKeyArray;

    /*
     * User search keyword
     */
    String searchKeywordValue = "";

    /*
     * Constructor loads the entity relatonships from the XML for further processing and intializes the table key arrays
     */
    public Search() {
        ENTITY_DISPLAY_LIMIT = db.entityDisplayLimit;
        this.loadEntityRelations();
        db.ensureRelations(this.foreignKeyArray); // to check the validity of the defined related_table in the XML with the datbase schema
        query = new Query(this.primaryKeyArray, this.foreignKeyArray);
        rawUserInputData = new String[db.getEntity().getEntityConfigValuesAtIndex(0).length];
        irrelationalRawUserInputData = new String[db.getEntity().getEntityConfigValuesAtIndex(0).length];
    }

    /*
     *	This is the function which contain the user interaction codes.
     *	This also creates the SQL Query raw data arrays to get real data.
     */
    public void doSearch() {
        String initialUserInput = "";
        String userTableSelection = "";
        String tableSugessions = "";
        String searchableTables = "";

        do {
            initialUserInput = this.promptMessage("For what are you searching for?\n: ", false);

            // exit
            if (initialUserInput.equalsIgnoreCase("[q]") || initialUserInput.equalsIgnoreCase("[exit]")) {
                break;
            }

            String detectedSuggestableTables = "";
            if (this.getSuggestableTables(initialUserInput) != null) {
                detectedSuggestableTables = this.getSuggestableTables(initialUserInput)[0];
            } else {
                detectedSuggestableTables = OVERFLOW;
            }

            if (!detectedSuggestableTables.toString().equalsIgnoreCase("") && !detectedSuggestableTables.toString().equalsIgnoreCase(OVERFLOW)) {
                String[] suggestableTables = this.getSuggestableTables(initialUserInput);
                tableSugessions = ""; // re-initialize to prevent old data being considered
                for (int i = 0; i < suggestableTables.length; i++) {
                    tableSugessions += suggestableTables[i] + ",";
                    System.out.println("- " + suggestableTables[i] + " : " + db.getEntity().getEntityMeta(suggestableTables[i], 3, true));
                }
                tableSugessions = tableSugessions.substring(0, (tableSugessions.length()) - 1); // trims the extra comma at the end: ","


                userTableSelection = this.promptMessage("\nAre you looking for something under the above categories? Please select a category or say 'no'. (no|category)\n: ", false);


                if (Functions.in_array(suggestableTables, userTableSelection)) {
                    // SEARCH in the selected initialUserInput
                    searchableTables = userTableSelection;
                    searchMode = 1;
                } else if ((userTableSelection.equalsIgnoreCase("no") || userTableSelection.equalsIgnoreCase("n"))) {
                    // SEARCH in all defined tables
                    String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                    if (!userSearchValue.equalsIgnoreCase("")) {
                        searchKeywordValue = userSearchValue;
                    }

                    for (int t = 0; t < db.getEntity().getEntityConfigValuesAtIndex(0).length; t++) {
                        irrelationalRawUserInputData[t] = this.getQueryRawData(db.getEntity().getEntityConfigValuesAtIndex(0)[t], "a", searchKeywordValue);
                    }
                    searchMode = 2;
                } else if ((userTableSelection.equalsIgnoreCase("yes") || userTableSelection.equalsIgnoreCase("y"))) {
                    // SEARCH in the suggested tables
                    searchableTables = tableSugessions;
                    String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                    if (!userSearchValue.equalsIgnoreCase("")) {
                        searchKeywordValue = userSearchValue;
                    }
                    String[] tables = searchableTables.split(",");
                    for (int t = 0; t < tables.length; t++) {
                        irrelationalRawUserInputData[t] = this.getQueryRawData(tables[t], "a", searchKeywordValue);
                    }
                    searchMode = 3;
                } else if ((userTableSelection.equalsIgnoreCase("[q]") || userTableSelection.equalsIgnoreCase("[exit]"))) {
                    break;
                }

            } else {
                if (!detectedSuggestableTables.toString().equalsIgnoreCase(OVERFLOW)) {
                    System.out.println("No matching category found! Will search in ALL");
                } else {
                    System.out.println(OVERFLOW);
                }

                String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                if (!userSearchValue.equalsIgnoreCase("")) {
                    searchKeywordValue = userSearchValue;
                }

                for (int t = 0; t < db.getEntity().getEntityConfigValuesAtIndex(0).length; t++) {
                    irrelationalRawUserInputData[t] = this.getQueryRawData(db.getEntity().getEntityConfigValuesAtIndex(0)[t], "a", searchKeywordValue);
                }
                searchMode = 2; // this is equal to the step: 2 as this is searching in all tables
            }


            // "0" means nothing (1, 2, 3) is triggered above. Definetly due to an invalid input
            if (searchMode == 0) {
                System.out.println("Invalid Category!");
            }

            if (searchMode == 1) {
                searchableTables = db.getEntity().getEntityMeta(searchableTables, 1, true); // getting the table_name
                String userCategoryAttributeSelection = this.promptMessage("\nPlease select the attributes/fields which you require. Seperate by commas for multiple entries. (all|<attribute1>,<attribute2>)\nAvailable attributes: " + db.getEntity().getSearchables(searchableTables, false) + "\n: ", true);
                if (userCategoryAttributeSelection.equalsIgnoreCase("all") || userCategoryAttributeSelection.equalsIgnoreCase("a")) {
                    userCategoryAttributeSelection = db.getEntity().getSearchables(searchableTables, true);
                } else {
                    userCategoryAttributeSelection = searchableTables + "." + userCategoryAttributeSelection.replaceAll(",", "," + searchableTables + ".");
                }

                // displaying the child/related tables
                // by default it is assuming as we have related table. After that it will find out the reality and stop where necessary
                boolean isRelatedTableAvailable = true;
                int count = 1;
                String rootTable = searchableTables;
                String userRelatedCategorySelection = "";
                boolean isAjoin = false;

                while (isRelatedTableAvailable) {

                    isRelatedTableAvailable = false;
                    if (!this.db.getEntity().getEntityMeta(searchableTables, 5, true).toString().equalsIgnoreCase("null")) {
                        String relatedTables = this.db.getEntity().getEntityMeta(searchableTables, 5, true);
                        String[] relatedTablesSplit = relatedTables.split(",");
                        // If exists more than one related tables
                        if (Functions.is_array(relatedTablesSplit)) {
                            relatedTables = ""; // initialize again to put new multiple table names
                            for (int r = 0; r < relatedTablesSplit.length; r++) {
                                relatedTables += db.getEntity().getEntityMeta(relatedTablesSplit[r], 2, true) + ", "; // #2: display_name
                            }
                            relatedTables = relatedTables.substring(0, (relatedTables.length()) - 2); // trims the extra comma at the end: ", "
                        } else {
                            relatedTables = db.getEntity().getEntityMeta(relatedTables, 2, true); // #2: display_name
                        }
                        System.out.println("Related categories: " + relatedTables); // available related entities
                        isRelatedTableAvailable = true;
                    }

                    if (isRelatedTableAvailable) {
                        userRelatedCategorySelection = this.promptMessage("\nYou can select one of the above related categories to get related data to your selected category, '" + db.getEntity().getEntityMeta(searchableTables, 2, true) + "'. Please select a related category or say 'no'. (no|category)\n: ", false);
                    }
                    if (Functions.in_array(db.getEntity().getEntityConfigValuesAtIndex(1), userRelatedCategorySelection) && isRelatedTableAvailable) {
                        userRelatedCategorySelection = db.getEntity().getEntityMeta(userRelatedCategorySelection, 1, true); // getting the real_name
                        String userRelatedCategoryAttributeSelection = this.promptMessage("\nPlease select the related attributes/fields which you require. Seperate by commas for multiple entries. (all|<attribute1>,<attribute2>)\nAvailable related attributes: " + db.getEntity().getSearchables(userRelatedCategorySelection, false) + "\n: ", true);
                        if (userRelatedCategoryAttributeSelection.equalsIgnoreCase("all") || userRelatedCategoryAttributeSelection.equalsIgnoreCase("a")) {
                            userRelatedCategoryAttributeSelection = db.getEntity().getSearchables(userRelatedCategorySelection, true);
                        } else {
                            userRelatedCategoryAttributeSelection = userRelatedCategorySelection + "." + userRelatedCategoryAttributeSelection.replaceAll(",", "," + userRelatedCategorySelection + ".");
                        }

                        isAjoin = true;
                        rawUserInputData[count] = this.getQueryRawData(userRelatedCategorySelection, userRelatedCategoryAttributeSelection, "");
                        searchableTables = userRelatedCategorySelection;
                        count++;
                    } else {
                        isRelatedTableAvailable = false; // Exit the loop. No more related tables found
                    }
                }

                if (isAjoin) {
                    String userSearchValue = this.promptMessage("\nPlease enter a keyword to search in the selected category, '" + userTableSelection + "'\n: ", false);
                    if (!userSearchValue.equalsIgnoreCase("")) {
                        searchKeywordValue = userSearchValue;
                    }

                    // according to this application logic, always the index 0 should contain the selected root category (look at the Query Class)
                    rawUserInputData[0] = this.getQueryRawData(rootTable, userCategoryAttributeSelection, searchKeywordValue);

                    String sqlQuery = query.buildQuery(rawUserInputData, false);
                    this.displayRealData(sqlQuery, null);
                } else {
                    String userSearchValue = this.promptMessage("\nPlease enter a keyword to search in the selected category, '" + userTableSelection + "'\n: ", false);
                    if (!userSearchValue.equalsIgnoreCase("")) {
                        searchKeywordValue = userSearchValue;
                    }

                    irrelationalRawUserInputData[0] = this.getQueryRawData(searchableTables, userCategoryAttributeSelection, searchKeywordValue);
                    this.displayRealData(null, irrelationalRawUserInputData);
                }

                // re-initialize the array to prevent the old values being considered during the command line execution
                Functions.initializeArray(rawUserInputData);
            } else if (searchMode == 2) {
                this.displayRealData(null, irrelationalRawUserInputData);
                Functions.initializeArray(irrelationalRawUserInputData);
            } else if (searchMode == 3) {
                this.displayRealData(null, irrelationalRawUserInputData);
                Functions.initializeArray(irrelationalRawUserInputData);
            }


            System.out.println("\n\n----------------------------------------------------------------");
        } while (!initialUserInput.equalsIgnoreCase(""));
    }

    /*
     * @param (String)	tableName		: selected table name
     * @param (String)	requiredDataFields	: requested data fields
     * @param (String)	searchKeyword		: search keyword
     * @return (String)	queryRawData		: this returns the query raw data which will be used to create the SQL statement
     * 							ex: "<table_name>::<requested_fields>::<where_clause>"
     */
    public String getQueryRawData(String tableName, String requiredDataFields, String searchKeyword) {
        tableName = db.getEntity().getEntityMeta(tableName, 1, true);
        String dataSeperator = db.COLNAMETYPESP + db.COLNAMETYPESP;
        String queryRawData = "";

        if (requiredDataFields.equalsIgnoreCase("a")) { // all
            requiredDataFields = db.getEntity().getSearchables(tableName, false);
        }

        String clause = "";
        if (db.considerUserAttributeSelectionForWhereClause) {
            clause = query.makeClause(requiredDataFields, searchKeyword);
        } else {
            clause = query.makeClause(db.getEntity().getSearchables(tableName, true), searchKeyword);
        }

        queryRawData = tableName + dataSeperator
                + requiredDataFields + dataSeperator
                + clause;
        return queryRawData;
    }

    /*
     * This returns the suggestable table according to the users category lookup value.
     * @param (String)      keyword             : the category search keyword
     * @return (String[])   suggestableTables   : an array of tables with their descriptions
     */
    public String[] getSuggestableTables(String keyword) {
        // traversing through all the available/defined seachable tables
        String[] tables = db.getEntity().getEntityConfigValuesAtIndex(0);

        // counting the tables which has got a meta keyword
        int countOfTablesWithMetaKeyword = 0;
        String tableList = "";
        for (int i = 0; i < tables.length; i++) {
            String eachTableMetaDescription = db.getEntity().getEntityMeta(tables[i], 3, true); // #3: implicit_annotation
            String eachTableAliases = db.getEntity().getEntityMeta(tables[i], 4, false); // #4: aliases
            String eachTableAttributeMetaDescription = db.getEntity().getEntityMeta(tables[i], 7, false); // #7: searchable_attributes

            // checking for the meta descriptions for any available keyword/s
            if (eachTableMetaDescription.toLowerCase().contains(keyword.toLowerCase())
                    || eachTableAttributeMetaDescription.toLowerCase().contains(keyword.toLowerCase())
                    || eachTableAliases.toLowerCase().contains(keyword.toLowerCase())) {

                tableList += db.getEntity().getEntityMeta(tables[i], 2, true) + ",";
                countOfTablesWithMetaKeyword++;
            }
        }
        if (tableList.length() > 0) {
            tableList = tableList.substring(0, tableList.length() - 1);
        }

        if (countOfTablesWithMetaKeyword <= ENTITY_DISPLAY_LIMIT) {
            String[] suggestableTables = tableList.split(",");
            return suggestableTables;
        } else {
            return null;
        }
    }

    /*
     * Get the user input from the command line interface
     * @param (String)  message     : the message for the user
     * @param (boolean) doTrim      : to remove white spaces according ot the value
     * @return (String) returnValue : returns the user input value
     */
    public String promptMessage(String message, boolean doTrim) {
        String returnValue = "";
        int inputAttemptCount = 0;
        do {
            if (inputAttemptCount != 0 && inputAttemptCount < 4) {
                System.out.println("Error: No input detected!");
            } else if (inputAttemptCount >= 4) {
                System.out.println("Error: No input detected! Exiting ...");
                System.exit(0);
            }

            System.out.print(message);
            Scanner input = new Scanner(System.in);


            if (doTrim) {
                returnValue = input.nextLine().toString().replace(" ", "");
            } else {
                returnValue = input.nextLine().toString();
            }
            inputAttemptCount++;
        } while (returnValue.length() == 0);
        return returnValue;
    }

    /*
     * The place where it triggers the Database SQL execution method to get the data out of the database
     * @param (String)      sqlQuery        : the pre-generated SQL query
     * @param (String[])    sqlQueryMeta    : an array containing the raw data for single SQL SELECT query generation
     */
    public void displayRealData(String sqlQuery, String[] sqlQueryMeta) {
        try {
            if (sqlQuery != null) {
                Map[] resultsets = db.sqlSelect(sqlQuery, "null", null, null, null, null, true);
                System.out.println("SQL :" + db.getQuery());
                String sqlCountQuery = sqlQuery.replaceAll("SELECT([^<]*)FROM", "SELECT COUNT(*) FROM");
                System.out.println("--- " + query.getCount(sqlCountQuery) + " results found ---");

                // begin :traversing through each row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        Map<String, String> resultset = resultsets[i];
                        System.out.println("== ROW: " + (i + 1) + " ========================");
                        for (Map.Entry<String, String> entry : resultset.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (value != null) {
                                System.out.println("['" + key + "'] = " + value);
                            }
                        }
                    }
                }
            } else {
                for (int c = 0; c < sqlQueryMeta.length; c++) {
                    String[] queryMeta = sqlQueryMeta[c].split(db.COLNAMETYPESP + db.COLNAMETYPESP);
                    Map[] resultsets = db.sqlSelect(queryMeta[0], queryMeta[1], queryMeta[2], null, null, null, false);
                    System.out.println("SQL :" + db.getQuery());

                    String sqlCountQuery = db.getQuery().replaceAll("SELECT([^<]*)FROM", "SELECT COUNT(*) FROM");
                    System.out.println("--- " + query.getCount(sqlCountQuery) + " results found ---");

                    // begin :traversing through each row to display data
                    if (resultsets.length > 0) {
                        for (int i = 0; i < resultsets.length; i++) {
                            Map<String, String> resultset = resultsets[i];
                            System.out.println("== ROW: " + (i + 1) + " ========================");
                            for (Map.Entry<String, String> entry : resultset.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (value != null) {
                                    System.out.println("['" + key + "'] = " + value);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /*
     * This loads the table relationships in terms of primary and foreign keys.
	And this will fill the primary and foreignkey arrays
     */
    public void loadEntityRelations() {
        // get the tables which have been defined in the <db-name>_entity_config.xml
        String[] tableArray = this.db.getEntity().getEntityConfigValuesAtIndex(0);

        int totalKeyColumns = 0;
        for (int t = 0; t < tableArray.length; t++) {
            Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);
            totalKeyColumns += resultsets.length;
        }
        primaryKeyArray = new String[totalKeyColumns];
        foreignKeyArray = new String[totalKeyColumns];

        int primarykeycount = 0;
        int foreignkeycount = 0;
        // begin: traversing through each defined table
        for (int t = 0; t < tableArray.length; t++) {
            // getting meta data from the database information schema table related to primary/foreign keys
            Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);

            // begin :traversing through each row
            if (resultsets.length > 0) {
                for (int i = 0; i < resultsets.length; i++) {
                    // Each data record/row
                    Map<String, String> resultset = resultsets[i];

                    if (resultset.get("CONSTRAINT_NAME").matches(".*fk.*") && resultset.get("CONSTRAINT_NAME") != null) {
                        foreignKeyArray[foreignkeycount] = tableArray[t] + db.COLNAMETYPESP + resultset.get("COLUMN_NAME") + ":" + resultset.get("REFERENCED_TABLE_NAME");
                        //System.out.println("FK: " + foreignKeyArray[foreignkeycount]);
                        foreignkeycount++;
                    }
                    if (resultset.get("CONSTRAINT_NAME").matches(".*PK.*") || resultset.get("CONSTRAINT_NAME").matches(".*PRIMARY.*")) { // "PRIMARY" is used in mysql
                        primaryKeyArray[primarykeycount] = tableArray[t] + db.COLNAMETYPESP + resultset.get("COLUMN_NAME");
                        //System.out.println("PK: " + primaryKeyArray[primarykeycount]);
                        primarykeycount++;
                    }
                }// end :traversing through each row
            }
        } // end: traversing through each table
    }
}
