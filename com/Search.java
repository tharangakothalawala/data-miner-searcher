package com;

/**
 *
 * @author Tharanga
 */
import database.*;
import java.util.*;

public class Search {

    private Database db = new Database();

    /*
     * this is the limit where we can display the suggestable tables
     */
    private static int ENTITYDISPLAYLIMIT;

    /*
     * If the searchable table count is more than the ENTITYDISPLAYLIMIT, the number of suggestable table is too much to select from. So it will search in all.
     */
    private static String OVERFLOW = "too_much_suggesstable_tables";

    /*
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
    String[] unrelationalRawUserInputData;

    String[] primaryKeyArray;
    String[] foreignKeyArray;
    private Query query = new Query(primaryKeyArray, foreignKeyArray);

    /*
     * Constructor loads the entity relatonships from the XML for further processing.
     */
    public Search () {
        ENTITYDISPLAYLIMIT = db.entityDisplayLimit;
        this.loadEntityRelations ();
        rawUserInputData = new String[db.getEntity().getDefinedSearchableTables(0).length];
        unrelationalRawUserInputData = new String[db.getEntity().getDefinedSearchableTables(0).length];
    }

    /*
     * ex. inputs:
     * USERS s name a s name ad PROFILE s prof_name min s prof_name dmin
     */
    public void doSearch() {
        String[] entityRelationsArray = this.getEntityRelations();
        //this.vardumpArray(entityRelationsArray);

        String[] levelOneEntities = new String[entityRelationsArray.length];

        String initialUserInput = "";
        String upperLevelTable = "";
        String userTableSelection = "";
        String tableSugessions = "";
        String searchableTables = "";
        String searchKeywordValue = "";

        /* the following is an example of a raw data for a SQL join query
        rawUserInputData[0] = "images::image_name,image_description::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%'";
        rawUserInputData[1] = "users::user_name::user_name LIKE '%mike%' OR user_email LIKE '%mike%'";
        //rawUserInputData[2] = "4images_comments::comments_name,comments_description::comments_name LIKE '%Texas%' OR comments_description LIKE '%Texas%' OR comments_keywords LIKE '%Texas%'";
        String sqlQuery1 = query.buildQuery (rawUserInputData, true);
        System.out.println("sqlQuery : \n" + sqlQuery1);
        System.exit(0);//*/
         
        do {
        initialUserInput = this.promptMessage("For what are you searching for?\n: ", false);

        if (initialUserInput.equalsIgnoreCase("x")) // exit
            System.exit(0); //break;

        String detectedSuggestableTables = "";
        if (this.getSuggestableTables(initialUserInput) != null) {
            detectedSuggestableTables = this.getSuggestableTables(initialUserInput)[0];
        } else {
            detectedSuggestableTables = OVERFLOW;
        }

        if (!detectedSuggestableTables.toString().equalsIgnoreCase("") && !detectedSuggestableTables.toString().equalsIgnoreCase(OVERFLOW)) {
            String[] suggestableTables = this.getSuggestableTables(initialUserInput);
            for (int i = 0; i < suggestableTables.length; i++) {
                tableSugessions += suggestableTables[i] + ",";
                System.out.println("- " + suggestableTables[i] + " : " + db.getEntity().getEntityMeta(suggestableTables[i], 3));
            }
        

            userTableSelection = this.promptMessage("\nAre you looking for something under the above categories? Please select a category or say 'no'. (no|category)\n: ", false);


            if (this.in_array(suggestableTables, userTableSelection)) {
                        // SEARCH in the selected initialUserInput
                        searchableTables = userTableSelection;
                        searchMode = 1;
            } else if ((userTableSelection.equalsIgnoreCase("no") || userTableSelection.equalsIgnoreCase("n"))) {
                        // SEARCH in all defined tables
                        searchableTables = "*";
                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }
                        
                        for (int t = 0; t < db.getEntity().getDefinedSearchableTables(0).length; t++) {
                            String clause = query.makeClause(db.getEntity().getSearchables(db.getEntity().getDefinedSearchableTables(0)[t], false), searchKeywordValue);
                            unrelationalRawUserInputData[t] = db.getEntity().getDefinedSearchableTables(0)[t] + "::" + clause;
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
                            String clause = query.makeClause(db.getEntity().getSearchables(tables[t], false), searchKeywordValue);
                            unrelationalRawUserInputData[t] = tables[t] + "::" + clause;
                        }
                        searchMode = 3;
            } else if ((userTableSelection.equalsIgnoreCase("exit") || userTableSelection.equalsIgnoreCase("quit") || userTableSelection.equalsIgnoreCase("q"))) {
                System.exit(0);
            }

        } else {
            if (!detectedSuggestableTables.toString().equalsIgnoreCase(OVERFLOW)) {
                System.out.println("No matching category found! Will search in ALL");
            }

            searchableTables = "*";
                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }

                        for (int t = 0; t < db.getEntity().getDefinedSearchableTables(0).length; t++) {
                            String clause = query.makeClause(db.getEntity().getSearchables(db.getEntity().getDefinedSearchableTables(0)[t], false), searchKeywordValue);
                            unrelationalRawUserInputData[t] = db.getEntity().getDefinedSearchableTables(0)[t] + "::" + clause;
                        }
                        searchMode = 2;
        }

        // @TODO: seems can be removed as never executed!
        if (searchMode == 0) {
                System.out.println ("Invalid Category!");
        }

        if (searchMode == 1) {
            searchableTables = db.getEntity().getEntityMeta(searchableTables, 1); // getting the table_name
            String userCategoryAttributeSelection = this.promptMessage("\nPlease select the attributes/fields which you require. Seperate by commas for multiple entries. (all|<attribute1>,<attribute2>)\nAvailable related attributes: " + db.getEntity().getSearchables(searchableTables, false) + "\n: ", true);
            if (userCategoryAttributeSelection.equalsIgnoreCase("all") || userCategoryAttributeSelection.equalsIgnoreCase("a")) {
                userCategoryAttributeSelection = db.getEntity().getSearchables(searchableTables, true);
            } else {
                userCategoryAttributeSelection = searchableTables + "." + userCategoryAttributeSelection.replaceAll(",", "," + searchableTables + ".");
            }

            // displaying the child/related tables
                    boolean isFound = false;
                    boolean isRelatedTableAvailable = false;
                    for (int t = 0; t < levelOneEntities.length; t++) {
                            for (int i = 0; i < entityRelationsArray.length; i++) {
                                String[] level = entityRelationsArray[i].split(db.COLNAMETYPESP);

                                if (level[0].equalsIgnoreCase(searchableTables) && !upperLevelTable.equalsIgnoreCase(searchableTables)) {
                                    if (this.is_array(level)) {
                                        String[] relatedCategories = level[1].split(",");
                                        String relatedCategoriesDsp = "";
                                        for (int j = 0; j < relatedCategories.length; j++) {
                                            relatedCategoriesDsp += db.getEntity().getEntityMeta(relatedCategories[j], 2) + ", ";
                                        }
                                        relatedCategoriesDsp = relatedCategoriesDsp.substring(0, (relatedCategoriesDsp.length()) - 2); // #2: display_name
                                        System.out.println("Related categories: " + relatedCategoriesDsp); // available related entities
                                        isRelatedTableAvailable = true;
                                    }
                                    upperLevelTable = searchableTables;
                                    isFound = true;
                                } else if (level[0].equalsIgnoreCase(searchableTables) && this.isRelatedWithAnyTable(searchableTables) && !upperLevelTable.equalsIgnoreCase(searchableTables)) {
                                    upperLevelTable = searchableTables;
                                    isFound = true;
                                }
                            }

                            if (this.isRelatedWithAnyTable(searchableTables) && !isFound) {
                                upperLevelTable = searchableTables;
                                isFound = true;
                            }
                    }

                    String userRelatedCategorySelection = "";
                    if (isRelatedTableAvailable) {
                        userRelatedCategorySelection = this.promptMessage("\nYou can select one of the above related categories to get related data to your selected category, '" + db.getEntity().getEntityMeta(searchableTables, 2) + "'. Please select a related category or say 'no'. (no|category)\n: ", false);
                    }
                    if (this.in_array(db.getEntity().getDefinedSearchableTables(1), userRelatedCategorySelection) && isRelatedTableAvailable) {
                        userRelatedCategorySelection = db.getEntity().getEntityMeta(userRelatedCategorySelection, 1); // getting the real_name
                        String userRelatedCategoryAttributeSelection = this.promptMessage("\nPlease select the related attributes/fields which you require. Seperate by commas for multiple entries. (all|<attribute1>,<attribute2>)\nAvailable related attributes: " + db.getEntity().getSearchables(userRelatedCategorySelection, false) + "\n: ", true);
                        if (userRelatedCategoryAttributeSelection.equalsIgnoreCase("all") || userRelatedCategoryAttributeSelection.equalsIgnoreCase("a")) {
                            userRelatedCategoryAttributeSelection = db.getEntity().getSearchables(userRelatedCategorySelection, true);
                        } else {
                            userRelatedCategoryAttributeSelection = userRelatedCategorySelection + "." + userRelatedCategoryAttributeSelection.replaceAll(",", "," + userRelatedCategorySelection + ".");
                        }

                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search in the selected category, '"+ userTableSelection +"'\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }

                        String clause_level_1 = query.makeClause(db.getEntity().getSearchables(searchableTables, true), searchKeywordValue);
                        rawUserInputData[0] = searchableTables + "::" + userCategoryAttributeSelection + "::" + clause_level_1;
                        //String clause_level_2 = query.makeClause(userRelatedCategoryAttributeSelection, searchKeywordValue);
                        rawUserInputData[1] = userRelatedCategorySelection + "::" + userRelatedCategoryAttributeSelection;

                        /* example join of two tables, "4images_users" with "4images_images"
                         * rawUserInputData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
                         * rawUserInputData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";
                         */
                        String sqlQuery = query.buildQuery (rawUserInputData, false);
                        this.getRealData(sqlQuery, null);
                    } else {
                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search in the selected category, '"+ userTableSelection +"'\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }
                        
                        String clause_level_1 = query.makeClause(db.getEntity().getSearchables(searchableTables, false), searchKeywordValue);
                        unrelationalRawUserInputData[0] = searchableTables + "::" + clause_level_1;
                        this.getRealData(null, unrelationalRawUserInputData);
                    }
                    

            //this.getRealData(null, unrelationalRawUserInputData);
        } else if (searchMode == 2) {
            this.getRealData(null, unrelationalRawUserInputData);
        } else if (searchMode == 3) {
            this.getRealData(null, unrelationalRawUserInputData);
        }


        System.out.println("\n\n----------------------------------------------------------------");
        } while (!initialUserInput.equalsIgnoreCase(""));

    } // function end

    public String[] getSuggestableTables (String keyword) {
	// traversing through all the available/defined seachable tables
	String[] tables = db.getEntity().getDefinedSearchableTables(0);

	// counting the tables which have got a meta keyword
	int countOfTablesWithMetaKeyword = 0;
	String tableList = "";
	for (int i = 0; i < tables.length; i++) {
		String eachTableMetaDescription = db.getEntity().getEntityMeta(tables[i], 3); // #3: implicit_annotation

		// checking for the initialUserInput meta description for any available keyword/s
		if (eachTableMetaDescription.toLowerCase().contains(keyword.toLowerCase())) {
			//tableList += tables[i] + ",";
                        tableList += db.getEntity().getEntityMeta(tables[i], 2) + ",";
			countOfTablesWithMetaKeyword++;
		}
	}
        if (tableList.length() > 0) {
		tableList = tableList.substring(0, tableList.length()-1);
        }
           
	if (countOfTablesWithMetaKeyword < ENTITYDISPLAYLIMIT) {
		String[] suggestableTables = tableList.split(",");
		return suggestableTables;
	} else {
		return null;
	}
    }

    /*public void identifyAttributes (String[] attributes) {
        for (int a = 0; a < attributes.length; a++) {
            String[] attributeData = attributes[a].split(":");
            System.out.println(attributeData[0]);
        }

        System.exit(0);
    }//*/

    public String promptMessage (String message, boolean doTrim) {
        String returnValue = "";
        int inputAttemptCount = 0;
        do {
            if (inputAttemptCount != 0 && inputAttemptCount < 4) {
                System.out.println ("Error: No input detected!");
            } else if (inputAttemptCount >= 4) {
                System.out.println ("Error: No input detected! Exiting ...");
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

    /*public String createWhereClause (String[] rawDataArray, boolean isWithinJoin) {
        String[] conditions = rawDataArray[1].split(db.COLNAMETYPESP);
        String whereClause = "";
        for (int k = 0; k < conditions.length; k++) {
            whereClause += rawDataArray[0] + "." + conditions[k] + " OR ";
        }
        whereClause = whereClause.substring(0, (whereClause.length()) - 4);
        if (isWithinJoin)
            return "(" + whereClause + ")";
        else
            return whereClause;
    }//*/

    public void getRealData(String sqlQuery, String[] sqlQueryMeta) {
        String eachRowData = "";
        try {
            if (sqlQuery != null) {
                Map[] resultsets = db.sqlSelect(sqlQuery, "null", null, null, null, null, true);
                System.out.println("1###Query :" + db.getQuery());
                String sqlCountQuery = sqlQuery.replaceAll("SELECT([^<]*)FROM", "SELECT COUNT(*) FROM");
                System.out.println ("--- " + query.getCount(sqlCountQuery) + " results found ---");

                // begin :traversing through each initialUserInput row to display data
                if (resultsets.length > 0) {
                    for (int i = 0; i < resultsets.length; i++) {
                        Map<String, String> resultset = resultsets[i];
                        System.out.println("== ROW: " + (i + 1) + " ========================");
                        eachRowData += "\n== ROW: " + (i + 1) + " ========================";
                        for (Map.Entry<String, String> entry : resultset.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (value != null) {
                                System.out.println("['" + key + "'] = " + value);
                                eachRowData += "\n['" + key + "'] = " + value;
                            }
                        }
                    }
                }
            } else {
                System.out.println("\n========================\n");
                this.vardumpArray(sqlQueryMeta);
                System.out.println("\n========================\n");

                for (int c = 0; c < sqlQueryMeta.length; c++) {
                    
                    String[] queryMeta = sqlQueryMeta[c].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
                    Map[] resultsets = db.sqlSelect(queryMeta[0], db.getEntity().getSearchables(queryMeta[0], false), queryMeta[1], null, null, null, false);
                    System.out.println("2###Query :" + db.getQuery());

                    String sqlCountQuery = db.getQuery().replaceAll("SELECT([^<]*)FROM", "SELECT COUNT(*) FROM");
                    System.out.println ("--- " + query.getCount(sqlCountQuery) + " results found ---");
                    

                    // begin :traversing through each initialUserInput row to display data
                    if (resultsets.length > 0) {
                        for (int i = 0; i < resultsets.length; i++) {
                            Map<String, String> resultset = resultsets[i];
                            System.out.println("== ROW: " + (i + 1) + " ========================");
                            eachRowData += "\n== ROW: " + (i + 1) + " ========================";
                            for (Map.Entry<String, String> entry : resultset.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (value != null) {
                                    System.out.println("['" + key + "'] = " + value);
                                    eachRowData += "\n['" + key + "'] = " + value;
                                }
                            }
                        }
                    }
                    //searchResults = eachRowData;
                    //searchResults += eachRowData;
                    //eachRowData = "";
                }
            }
        } catch (Exception ex) {
        }

        //searchResults += eachRowData;
    }

    /*public String[] initializeArray (String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
        return array;
    }//*/

    public void vardumpArray (String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                System.out.println(array[i]);
        }
    }

    /*public int findExistingTableClausePrefixIndex (String[] array, String value, String splitter) {
        int index = 0; // the very first array insertion index
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null){
                String[] tableClause = array[i].split(splitter);
                if (tableClause[0].toString().equalsIgnoreCase(value)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }//*/

    /*
     * return the next insertion position for a given array
     */
    /*public int nextAvailableArrayIndex (String[] array) {
        int nextIndex = 0; // the very first array insertion index
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                nextIndex = i;
                break;
            }
        }
        return nextIndex;
    }//*/

    /*public boolean isTableSelected (String[] array, String table) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
            String[] tableClause = array[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            if (tableClause[0].matches(".*"+table+".*"))
                return true;
            }
        }
        return false;
    }//*/

    public boolean in_array (String[] array, String searchValue) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equalsIgnoreCase(searchValue))
                return true;

        return false;
    }

    public boolean is_array (String[] array) {
        try {
            String attempToGetTheValueAtArrayIndexTwo = array[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }

        return true;
    }

    public boolean isRelatedWithAnyTable (String table) {
        boolean result = false;
        String[] relatedEntities = this.getEntityRelations ();

        for (int i = 0; i < relatedEntities.length; i++) {
            if (!result && relatedEntities[i].matches(".*"+table+".*")) {
                String[] tableInfo = relatedEntities[i].split(db.COLNAMETYPESP);
                if (this.is_array(tableInfo)) {
                    String[] tables = tableInfo[1].split(",");
                    if (this.in_array(tables, table)) {
                        result = true;
                    }
                }
            }
        }

        //System.out.println("related tables : " + result);
        return result;
    }

    public void loadEntityRelations () {
        String[] tableArray = this.db.getEntity().getDefinedSearchableTables(0);

        int totalKeyColumns = 0;
        for (int t = 0; t < tableArray.length; t++) {
            Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);
            totalKeyColumns += resultsets.length;
        }
        primaryKeyArray = new String[totalKeyColumns];
        foreignKeyArray = new String[totalKeyColumns];

        int primarykeycount = 0;
        int foreignkeycount = 0;
        // begin: traversing through each initialUserInput
        for (int t = 0; t < tableArray.length; t++) {
            // need only the tables which contain searchable attributes
            if (!db.getEntity().getSearchables(tableArray[t], false).equalsIgnoreCase("")) {
                // sending the query to the database to retrieve data
                Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);

                // begin :traversing through each initialUserInput row to display data
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
                                    //System.out.println("FK: "+ tableArray[t] + db.COLNAMETYPESP + columnname);
                                    foreignkeycount++;
                                }
                                if (value.matches(".*PK.*") || value.matches(".*PRIMARY.*")) { // "PRIMARY" is used in mysql

                                    primaryKeyArray[primarykeycount] = tableArray[t] + db.COLNAMETYPESP + columnname;
                                    //System.out.println("PK: "+ tableArray[t] + db.COLNAMETYPESP + columnname);
                                    primarykeycount++;
                                }
                            }
                        }
                    }
                } // end :traversing through each initialUserInput row to display data
            }
        } // end: traversing through each initialUserInput
        query = new Query(this.primaryKeyArray, this.foreignKeyArray);
    }

    public String[] getEntityRelations () {
        String[] availableEntities = this.db.getEntity().getDefinedSearchableTables(0);
        String[] relatedEntities = new String[availableEntities.length];
        for (int t = 0; t < availableEntities.length; t++) {
            if (!this.db.getEntity().getEntityMeta(availableEntities[t], 5).toString().equalsIgnoreCase("null")) {
                relatedEntities[t] = availableEntities[t] + ":" + this.db.getEntity().getEntityMeta(availableEntities[t], 5);
            } else {
                relatedEntities[t] = availableEntities[t];
            }
        }
        return relatedEntities;
    }

}
