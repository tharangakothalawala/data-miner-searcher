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
     * this is the limit where we can search in all tables.
     * If the searchable table count is more than this limit, we need to ask the user to select/input the category
     */
    private static int ENTITYDISPLAYLIMIT;

    /*
     * 1: searching all searchable enities under the limit
     * 2: searching ALL the enities
     * 3: searching the specified entity
     */
    private int searchMode = 0;

    public String searchResults = "";
    Query query;
    String[] primaryKeyArray;
    String[] foreignKeyArray;
    String [] eachSelectedTableClauseData;
    String[] nonConceptuallyRelatedTableCalueData;

    String selectedMainCategory;

    public Search () {
        ENTITYDISPLAYLIMIT = db.entityDisplayLimit;
        this.loadEntityRelations ();
        eachSelectedTableClauseData = new String[db.getEntity().getDefinedSearchableTables(0).length];
        nonConceptuallyRelatedTableCalueData = new String[db.getEntity().getDefinedSearchableTables(0).length];
    }

    public String[] showInitialView () {
        String[] entityRelationsArray = this.getEntityRelations();

        //System.out.println(" ## Avaliable Entities ## ");
        // Displaying the available entity relationships
        String[] levelOneEntities = new String[entityRelationsArray.length];
        /*for (int t = 0; t < entityRelationsArray.length; t++) {
            String[] level1 = entityRelationsArray[t].split(db.COLNAMETYPESP);
            if (db.getEntity().getEntityMeta(level1[0], 2).equalsIgnoreCase("true") && this.is_array(level1)) { // show only the main entities.. not the join candidate entities
                System.out.println(db.getEntity().getEntityMeta(level1[0], 7) + " : " + level1[1] + " - " + db.getEntity().getEntityMeta(level1[0], 4));
            } else if (db.getEntity().getEntityMeta(level1[0], 2).equalsIgnoreCase("true")) {
                System.out.println(db.getEntity().getEntityMeta(level1[0], 7));
            }
        }
        System.out.println("\n -- Available commands: --\ns: to set a keyword for the current table selected.\nd: to search/display output\ni: show the initial view\n");
        System.out.println("----------------------------------------------------------------");
        //*/
        return levelOneEntities;
    }

    public String[] showInitialView (boolean withChildData) {
        String[] entityRelationsArray = this.getEntityRelations();
        //this.vardumpArray(entityRelationsArray);

        String[] levelOneEntities = new String[entityRelationsArray.length];
        int count = 0;
        for (int t = 0; t < entityRelationsArray.length; t++) {
            String[] level1 = entityRelationsArray[t].split(db.COLNAMETYPESP);
            if (db.getEntity().getEntityMeta(level1[0], 2).equalsIgnoreCase("true") && this.is_array(level1)) { // show only the main entities.. not the join candidate entities
                levelOneEntities[count] = db.getEntity().getEntityMeta(level1[0], 1) + ":" + level1[1];
                count++;
            } else if (db.getEntity().getEntityMeta(level1[0], 2).equalsIgnoreCase("true")) {
                levelOneEntities[count] = db.getEntity().getEntityMeta(level1[0], 1);
                count++;
            }
        }

        return levelOneEntities;
    }

    /*
     * @Desc : look whether a table(candidateEntity) is related (technically: if exists a foreign key) to a given table(entity)
     */
    public boolean isRelated (String entity, String candidateEntity) {
        String[] entityRelationsArray = this.getEntityRelations();
        for (int t = 0; t < entityRelationsArray.length; t++) {
            String[] tableDataArr = entityRelationsArray[t].split(db.COLNAMETYPESP);
            String[] relatedTables = tableDataArr[1].split(db.COLNAMETYPESP);
            if (tableDataArr[0].equalsIgnoreCase(entity) && this.in_array(relatedTables, candidateEntity)) {
                return true;
            }
        }
        return false;
    }

    /*
     * ex. inputs:
     * USERS s name a s name ad PROFILE s prof_name min s prof_name dmin
     */
    public void doSearch(String searchKeyword) {
        selectedMainCategory = "";

        String[] entityRelationsArray = this.getEntityRelations();
        //this.vardumpArray(entityRelationsArray);

        String eachRowData = "";
        String[] levelOneEntities = this.showInitialView();

        String table = "";
        String upperLevelTable = "";
        String userTableSelection = "";
        String tableSugessions = "";
        String searchableTables = "";
        String searchKeywordValue = "Texas";


        table = this.promptMessage("For what are you searching for?\n: ", false);

        //if (this.getSuggestableTables(table).length)
        

        if (!this.getSuggestableTables(table)[0].toString().equalsIgnoreCase("")) {
            String[] suggestableTables = this.getSuggestableTables(table);
            for (int i = 0; i < suggestableTables.length; i++) {
                tableSugessions += suggestableTables[i] + ",";
                System.out.println("- " + suggestableTables[i] + " : " + db.getEntity().getEntityMeta(suggestableTables[i], 3));
            }
        

            userTableSelection = this.promptMessage("\nAre you looking for something under the above categories? Please select a category or say 'no'. (no|category)\n: ", false);


            if (this.in_array(suggestableTables, userTableSelection)) {
                        // SEARCH in the selected table
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
                            String clause = query.makeClause(db.getEntity().getSearchables(db.getEntity().getDefinedSearchableTables(0)[t], false, false), searchKeywordValue);
                            nonConceptuallyRelatedTableCalueData[t] = db.getEntity().getDefinedSearchableTables(0)[t] + "::" + clause;
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
                            String clause = query.makeClause(db.getEntity().getSearchables(tables[t], false, false), searchKeywordValue);
                            nonConceptuallyRelatedTableCalueData[t] = tables[t] + "::" + clause;
                        }
                        searchMode = 3;
            } else if ((userTableSelection.equalsIgnoreCase("exit") || userTableSelection.equalsIgnoreCase("quit") || userTableSelection.equalsIgnoreCase("q"))) {
                System.exit(0);
            }

        } else {
            System.out.println("No matching category found! Will search in ALL");

            searchableTables = "*";
                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search.\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }

                        for (int t = 0; t < db.getEntity().getDefinedSearchableTables(0).length; t++) {
                            String clause = query.makeClause(db.getEntity().getSearchables(db.getEntity().getDefinedSearchableTables(0)[t], false, false), searchKeywordValue);
                            nonConceptuallyRelatedTableCalueData[t] = db.getEntity().getDefinedSearchableTables(0)[t] + "::" + clause;
                        }
                        searchMode = 2;
        }

        if (searchMode == 0) {
                System.out.println ("Invalid Category!");
        }

        if (searchMode == 1) {
            // displaying the child/related tables
                    boolean isFound = false;
                    for (int t = 0; t < levelOneEntities.length; t++) {
                            for (int i = 0; i < entityRelationsArray.length; i++) {
                                String[] level = entityRelationsArray[i].split(db.COLNAMETYPESP);

                                if (level[0].equalsIgnoreCase(searchableTables) && !upperLevelTable.equalsIgnoreCase(searchableTables)) {
                                    if (this.is_array(level)) {
                                        System.out.println("Related categories: " + level[1]); // available related entities
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
                    String userRelatedCategorySelection = this.promptMessage("\nYou can select one of the above related categories to get related data to your selected category, '" + searchableTables + "'. Please select a related category or say 'no'. (no|category)\n: ", false);
                    if (this.in_array(db.getEntity().getDefinedSearchableTables(0), userRelatedCategorySelection)) {
                        String relatedTableAttributes = db.getEntity().getSearchables(userRelatedCategorySelection, false, false);

                        String userRelatedCategoryAttributeSelection = this.promptMessage("\nPlease select the related attributes/fields which you require. Seperate by commas for multiple entries. (all|<attribute1>,<attribute2>)\nAvailable related attributes: " + relatedTableAttributes + "\n: ", true);
                        if (userRelatedCategoryAttributeSelection.equalsIgnoreCase("all") || userRelatedCategoryAttributeSelection.equalsIgnoreCase("a")) {
                            userRelatedCategoryAttributeSelection = relatedTableAttributes;
                        }

                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search in the selected category, '"+ userRelatedCategorySelection +"'\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }

                        String clause_level_1 = query.makeClause(db.getEntity().getSearchables(searchableTables, false, false), searchKeywordValue);
                        eachSelectedTableClauseData[0] = searchableTables + "::" + clause_level_1;
                        //String clause_level_2 = query.makeClause(userRelatedCategoryAttributeSelection, searchKeywordValue);
                        eachSelectedTableClauseData[1] = userRelatedCategorySelection + "::" + userRelatedCategoryAttributeSelection;

                        /* example join of two tables, "4images_users" with "4images_images"
                         * eachSelectedTableClauseData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
                         * eachSelectedTableClauseData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";
                         */
                        String sqlQuery = query.buildQuery (eachSelectedTableClauseData, false, false);
                        this.getRealData(sqlQuery, null);
                    } else {
                        String userSearchValue = this.promptMessage("\nPlease enter a keyword to search\n: ", false);
                        if (!userSearchValue.equalsIgnoreCase("")) {
                            searchKeywordValue = userSearchValue;
                        }
                        
                        String clause_level_1 = query.makeClause(db.getEntity().getSearchables(searchableTables, false, false), searchKeywordValue);
                        nonConceptuallyRelatedTableCalueData[0] = searchableTables + "::" + clause_level_1;
                        this.getRealData(null, nonConceptuallyRelatedTableCalueData);
                    }
                    

            //this.getRealData(null, nonConceptuallyRelatedTableCalueData);
        } else if (searchMode == 2) {
            this.getRealData(null, nonConceptuallyRelatedTableCalueData);
        } else if (searchMode == 3) {
            this.getRealData(null, nonConceptuallyRelatedTableCalueData);
        }

        

        // Stopping the execution
        System.exit(0);





































        
        do {
            table = this.promptMessage("Please enter your search keyword/s : ", false);

            
            String userCategorySelection = "";
            String entitySugessions = "";


            //if (db.getEntity().getDefinedSearchableTables(0).length > ENTITYDISPLAYLIMIT) {
                System.out.println("");
                if (db.getEntity().getSugesstableEntityCount() < ENTITYDISPLAYLIMIT) {
                    // if there are no or enough suggestable entities defined, display the first "entityDisplayLimit" (ex: 3 defined entities) from the XML descriptor
                    for (int i = 0; i < db.entityDisplayLimit; i++) {
                        entitySugessions += db.getEntity().getDefinedSearchableTables(0)[i] + ",";
                        System.out.println("- " + db.getEntity().getDefinedSearchableTables(0)[i] + " : " + db.getEntity().getEntityMeta(db.getEntity().getDefinedSearchableTables(0)[i], 4)); //db.getEntity().getEntityMeta(level1[0], 4))
                    }
                } else {
                    for (int i = 0; i < ENTITYDISPLAYLIMIT; i++) {
                        if (!db.getEntity().getEntityMeta(db.getEntity().getDefinedSearchableTables(0)[i], 8).equalsIgnoreCase("0")) {
                            entitySugessions += db.getEntity().getDefinedSearchableTables(0)[i] + ",";
                            System.out.println("- " + db.getEntity().getDefinedSearchableTables(0)[i] + " : " + db.getEntity().getEntityMeta(db.getEntity().getDefinedSearchableTables(0)[i], 4)); //db.getEntity().getEntityMeta(level1[0], 4))
                        } else {
                            // increment the limit by one to skip and to go to next suggestable entity
                            ENTITYDISPLAYLIMIT += 1;
                        }
                    }
                }
                entitySugessions = entitySugessions.substring(0, entitySugessions.length()-1);
                //System.out.println(entitySugessions);
                userCategorySelection = this.promptMessage("\nAre you looking for something under the above categories (yes|no|category name): ", false);

                /////////////////////////////////
                if ((userCategorySelection.equalsIgnoreCase("yes") || userCategorySelection.equalsIgnoreCase("y"))) {
                    // SEARCH in the suggested tables
                    searchableTables = entitySugessions;
                    String[] tables = searchableTables.split(",");
                    for (int t = 0; t < tables.length; t++) {
                        String clause = query.makeClause(db.getEntity().getSearchables(tables[t], false, false), table);
                        nonConceptuallyRelatedTableCalueData[t] = tables[t] + "::" + clause;
                    }
                    searchMode = 1;
                } else if ((userCategorySelection.equalsIgnoreCase("no") || userCategorySelection.equalsIgnoreCase("n"))) {
                    // SEARCH in all defined tables
                    searchableTables = "*";
                    for (int t = 0; t < db.getEntity().getDefinedSearchableTables(0).length; t++) {
                        String clause = query.makeClause(db.getEntity().getSearchables(db.getEntity().getDefinedSearchableTables(0)[t], false, false), table);
                        nonConceptuallyRelatedTableCalueData[t] = db.getEntity().getDefinedSearchableTables(0)[t] + "::" + clause;
                    }
                    searchMode = 2;
                } else if (this.in_array(db.getEntity().getDefinedSearchableTables (0), userCategorySelection)) {
                    // SEARCH in the selected table
                    searchableTables = userCategorySelection;
                    String clause = query.makeClause(db.getEntity().getSearchables(searchableTables, false, false), table);
                    nonConceptuallyRelatedTableCalueData[0] = searchableTables + "::" + clause;
                    searchMode = 3;
                }

                if (searchMode == 1) {
                    //this.vardumpArray(nonConceptuallyRelatedTableCalueData);
                    this.getRealData(null, nonConceptuallyRelatedTableCalueData);
                    //searchResults += this.searchResults;
                    //System.out.println("searchResults 1 : " + searchResults);
                    //this.searchResults = "";
                } else if (searchMode == 2) {
                    //this.vardumpArray(nonConceptuallyRelatedTableCalueData);
                    this.getRealData(null, nonConceptuallyRelatedTableCalueData);
                    //searchResults += this.searchResults;
                    //System.out.println("searchResults 2 : " + searchResults);
                    //this.searchResults = "";
                } else if (searchMode == 3) { // correct
                    // displaying the child/related tables
                    boolean isFound = false;
                    for (int t = 0; t < levelOneEntities.length; t++) {
                            for (int i = 0; i < entityRelationsArray.length; i++) {
                                String[] level = entityRelationsArray[i].split(db.COLNAMETYPESP);

                                if (level[0].equalsIgnoreCase(searchableTables) && !upperLevelTable.equalsIgnoreCase(searchableTables)) {
                                    if (this.is_array(level)) {
                                        System.out.println("Related categories: " + level[1]); // available related entities
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
                    String userRelatedCategorySelection = this.promptMessage("\nYou can select one of the above related categories to get related data to your selected category, '" + searchableTables + "'  (no|category name) : ", false);
                    //!userRelatedCategorySelection.equalsIgnoreCase("no") || !userRelatedCategorySelection.equalsIgnoreCase("n") || 
                    if (this.in_array(db.getEntity().getDefinedSearchableTables(0), userRelatedCategorySelection)) {
                        String clause_level_1 = query.makeClause(db.getEntity().getSearchables(searchableTables, false, false), table);
                        String clause_level_2 = query.makeClause(db.getEntity().getSearchables(searchableTables, false, false), table);
                        eachSelectedTableClauseData[0] = searchableTables + "::" + clause_level_1;
                        eachSelectedTableClauseData[1] = userRelatedCategorySelection + "::" + clause_level_2;

                        /* example join of two tables, "4images_users" with "4images_images"
                         * eachSelectedTableClauseData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
                         * eachSelectedTableClauseData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";
                         */
                        String sqlQuery = query.buildQuery (eachSelectedTableClauseData, false, false);
                        this.getRealData(sqlQuery, null);
                    } else {
                        // counting the tables which have got a meta keyword
////////////////////////////////////////////////////////////////////////////////
                        // traversing through all the available/defined seachable tables
                        int entityCount = db.getEntity().getDefinedSearchableTables(0).length;
                        String[] entities = db.getEntity().getDefinedSearchableTables(0);

                int countOfEntitiesWithMetaKeyword = 0;
                for (int i = 0; i < entityCount; i++) {
                    String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);
                    // checking for the enity description for the meta keyword/s
                    if (eachEntityDescription.toLowerCase().contains("@\""+searchableTables.toLowerCase()+"@\"")) {
                        countOfEntitiesWithMetaKeyword++;
                    }
                }

                String userSelectionExtraSearch = "";
                if (countOfEntitiesWithMetaKeyword > 0) {
                    userSelectionExtraSearch = this.promptMessage("Enter 'yes' or 'no' to continue & consider the " + countOfEntitiesWithMetaKeyword + " extra related category/ies found: ", false);
                }
                if (countOfEntitiesWithMetaKeyword > 0 && (userSelectionExtraSearch.equalsIgnoreCase("yes") || userSelectionExtraSearch.equalsIgnoreCase("y"))) {
                    for (int i = 0; i < entityCount; i++) {
                        String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);

                        // checking for the enity description for the meta keyword/s
                        if (eachEntityDescription.toLowerCase().contains("@\""+searchableTables.toLowerCase()+"@\"")) {
                            //System.out.println("\n##2" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                            String entityPreferance = this.promptMessage("\nConsider " + eachEntityDescription.replace("@\"", "") + "? (yes|no)", false);
                            if ((entityPreferance.equalsIgnoreCase("y") || entityPreferance.equalsIgnoreCase("yes")) && !this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                //String relatedEntityClause = db.getEntity().makeClause(db.getEntity().getEntityMeta(entities[i], 3), value);
                                //nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;

                                //////////////////////////////// Each entity attribute description //////////////
                                String searchableAttributes = db.getEntity().getEntityMeta(entities[i], 5);
                                String[] searchableAttributeData = searchableAttributes.split(",");

                                //-----
                                int countOfAttributesWithMetaKeyword = 0;
                                for (int a = 0; a < searchableAttributeData.length; a++) {
                                    String[] attributeData = searchableAttributeData[a].split(":");
                                    try {
                                        if (attributeData[1].toLowerCase().contains("@\""+searchableTables.toLowerCase()+"@\"")) {
                                            countOfAttributesWithMetaKeyword++;
                                        }
                                    } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description */ }
                                }
                                //-----
                                if (countOfAttributesWithMetaKeyword > 1) {
                                    System.out.println("Found " + countOfAttributesWithMetaKeyword + " search criteria under this category. Please say 'yes' or 'no' for the following prompts");
                                    String searchables = "";
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        String[] attributeData = searchableAttributeData[a].split(":");
                                        try {
                                        if (attributeData[1].toLowerCase().contains("@\""+searchableTables.toLowerCase()+"@\"")) {
                                            //System.out.println("\n##3" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                                            String preferance = this.promptMessage("\nConsider " + attributeData[1].replace("@\"", "") + "? (yes|no)", false);
                                            if (preferance.equalsIgnoreCase("y") || preferance.equalsIgnoreCase("yes")) {
                                                searchables += attributeData[0] + ",";
                                            } else {
                                                searchableAttributeData[a] = null; // unset the value (this will avoid considering this attribute later)
                                            }
                                        }
                                        } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description */ }
                                    }
                                    // going through the  attributes again to get any attribute which did't get considered above due to the user input
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        try {
                                            String[] attributeData = searchableAttributeData[a].split(":");
                                            if (!searchables.toLowerCase().contains(attributeData[0].toLowerCase())) {
                                                searchables += attributeData[0] + ",";
                                            }
                                        } catch (Exception ex) { }
                                    }
                                    ///////////////*/
                                    if (!searchables.equalsIgnoreCase("")) {
                                        searchables = searchables.substring(0, (searchables.length()) - 1);
                                        if (!this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                            String relatedEntityClause = query.makeClause(searchables, table);
                                            nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;
                                        }
                                    }
                                } else {
                                    if (!this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                        String relatedEntityClause = query.makeClause(db.getEntity().getEntityMeta(entities[i], 3), table);
                                        nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;
                                    }
                                }
                                //////////////////////////////////////*/
                            }
                        }
                    }
                }

                //////////////////////
                    //String sqlQuery = this.query.buildQuery(nonConceptuallyRelatedTableCalueData, false, true);
                    //this.getRealData(sqlQuery, null);
                this.getRealData(null, nonConceptuallyRelatedTableCalueData);
                    //searchResults += this.searchResults;
                    //System.out.println("searchResults 3 : " + searchResults);
                    //this.searchResults = "";
                    }
                }
                

            //}
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            System.exit(0);
            
            if (this.isRelatedWithAnyTable(table) || (!this.isRelatedWithAnyTable(table) && this.in_array(db.getEntity().getDefinedSearchableTables (0), table))) {
                selectedMainCategory = table;
                if (!this.isTableSelected(eachSelectedTableClauseData, selectedMainCategory)) {
                    eachSelectedTableClauseData[this.nextAvailableArrayIndex(eachSelectedTableClauseData)] = table + "::";
                }
            }

            if (table.equalsIgnoreCase("x")) // exit
                System.exit(0); //break;

            // this may be used to get to the level 1
            if (table.equalsIgnoreCase("i")) { // reset
                String[] arr = this.showInitialView();
            }

            // this may be used to get to the level 1
            // 4images_images s Texas Drive n 4images_users s sales@milezone.com
            if (table.equalsIgnoreCase("d")) { // display
                System.out.println("#########################################");
                System.out.println("eachSelectedTableClauseData");
                // example re-created search SQL meta data
                //eachSelectedTableClauseData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
                //eachSelectedTableClauseData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";
                this.vardumpArray(eachSelectedTableClauseData);
                //System.out.println("nonConceptuallyRelatedTableCalueData");
                //this.vardumpArray(nonConceptuallyRelatedTableCalueData);//*/
                //System.out.println("\n### Query : --------------------------------<<<");
                String sqlQuery = query.buildQuery (eachSelectedTableClauseData, false, true);
                this.getRealData (sqlQuery, null);
                this.getRealData (null, nonConceptuallyRelatedTableCalueData);
                //this.vardumpArray(this.getEntityRelations ());
                //this.vardumpArray(eachSelectedTableClauseData);
                System.out.println("-- exiting ...");
                break;
            }

            // selecting an attribute to set value (this will be used to create the SQL on the fly)
            if (table.equalsIgnoreCase("s")) { // select
                //System.out.print("Select an attribute to set a value : ");
                //String attribute = input.next();
                String value = this.promptMessage("search keyword : ", false);
                //String[] arr = db.getEntity().getEntityMeta(selectedMainCategory, 2);
                String clause = query.makeClause(db.getEntity().getEntityMeta(selectedMainCategory, 3), value);
                //eachSelectedTableClauseData[this.findExistingTableClausePrefixIndex(eachSelectedTableClauseData, selectedMainCategory, db.COLNAMETYPESP+db.COLNAMETYPESP)] += attribute + " LIKE '%" + value + "%':";
                eachSelectedTableClauseData[this.findExistingTableClausePrefixIndex(eachSelectedTableClauseData, selectedMainCategory, db.COLNAMETYPESP+db.COLNAMETYPESP)] += clause;

                // traversing through all the available/defined seachable tables
                int entityCount = db.getEntity().getDefinedSearchableTables(0).length;
                String[] entities = db.getEntity().getDefinedSearchableTables(0);

                // counting the tables which have got a meta keyword
                int countOfEntitiesWithMetaKeyword = 0;
                for (int i = 0; i < entityCount; i++) {
                    String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);
                    // checking for the enity description for the meta keyword/s
                    if (eachEntityDescription.toLowerCase().contains("@\""+selectedMainCategory.toLowerCase()+"@\"")) {
                        countOfEntitiesWithMetaKeyword++;
                    }
                }

                String userSelectionExtraSearch = "";
                if (countOfEntitiesWithMetaKeyword > 0) {
                    userSelectionExtraSearch = this.promptMessage("Enter 'yes' or 'no' to continue & consider the " + countOfEntitiesWithMetaKeyword + " extra related category/ies found: ", false);
                }
                if (countOfEntitiesWithMetaKeyword > 0 && (userSelectionExtraSearch.equalsIgnoreCase("yes") || userSelectionExtraSearch.equalsIgnoreCase("y"))) {
                    for (int i = 0; i < entityCount; i++) {
                        String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);

                        // checking for the enity description for the meta keyword/s
                        if (eachEntityDescription.toLowerCase().contains("@\""+selectedMainCategory.toLowerCase()+"@\"")) {
                            //System.out.println("\n##2" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                            String entityPreferance = this.promptMessage("\nConsider " + eachEntityDescription.replace("@\"", "") + "? (yes|no)", false);
                            if ((entityPreferance.equalsIgnoreCase("y") || entityPreferance.equalsIgnoreCase("yes")) && !this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                //String relatedEntityClause = db.getEntity().makeClause(db.getEntity().getEntityMeta(entities[i], 3), value);
                                //nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;

                                //////////////////////////////// Each entity attribute description //////////////
                                String searchableAttributes = db.getEntity().getEntityMeta(entities[i], 5);
                                String[] searchableAttributeData = searchableAttributes.split(",");

                                //-----
                                int countOfAttributesWithMetaKeyword = 0;
                                for (int a = 0; a < searchableAttributeData.length; a++) {
                                    String[] attributeData = searchableAttributeData[a].split(":");
                                    try {
                                        if (attributeData[1].toLowerCase().contains("@\""+selectedMainCategory.toLowerCase()+"@\"")) {
                                            countOfAttributesWithMetaKeyword++;
                                        }
                                    } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description */ }
                                }
                                //-----
                                if (countOfAttributesWithMetaKeyword > 1) {
                                    System.out.println("Found " + countOfAttributesWithMetaKeyword + " search criteria under this category. Please say 'yes' or 'no' for the following prompts");
                                    String searchables = "";
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        String[] attributeData = searchableAttributeData[a].split(":");
                                        try {
                                        if (attributeData[1].toLowerCase().contains("@\""+selectedMainCategory.toLowerCase()+"@\"")) {
                                            //System.out.println("\n##3" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                                            String preferance = this.promptMessage("\nConsider " + attributeData[1].replace("@\"", "") + "? (yes|no)", false);
                                            if (preferance.equalsIgnoreCase("y") || preferance.equalsIgnoreCase("yes")) {
                                                searchables += attributeData[0] + ",";
                                            } else {
                                                searchableAttributeData[a] = null; // unset the value (this will avoid considering this attribute later)
                                            }
                                        }
                                        } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description */ }
                                    }
                                    // going through the  attributes again to get any attribute which did't get considered above due to the user input
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        try {
                                            String[] attributeData = searchableAttributeData[a].split(":");
                                            if (!searchables.toLowerCase().contains(attributeData[0].toLowerCase())) {
                                                searchables += attributeData[0] + ",";
                                            }
                                        } catch (Exception ex) { }
                                    }
                                    ///////////////*/
                                    if (!searchables.equalsIgnoreCase("")) {
                                        searchables = searchables.substring(0, (searchables.length()) - 1);
                                        if (!this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                            String relatedEntityClause = query.makeClause(searchables, value);
                                            nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;
                                        }
                                    }
                                } else {
                                    if (!this.isTableSelected(nonConceptuallyRelatedTableCalueData, entities[i])) {
                                        String relatedEntityClause = query.makeClause(db.getEntity().getEntityMeta(entities[i], 3), value);
                                        nonConceptuallyRelatedTableCalueData[this.nextAvailableArrayIndex(nonConceptuallyRelatedTableCalueData)] = entities[i] + "::" + relatedEntityClause;
                                    }
                                }
                                //////////////////////////////////////*/
                            }
                        }
                    }
                }
            }

            // displaying the child/related tables
            boolean isFound = false;
            for (int t = 0; t < levelOneEntities.length; t++) {
                    for (int i = 0; i < entityRelationsArray.length; i++) {
                        String[] level = entityRelationsArray[i].split(db.COLNAMETYPESP);

                        if (level[0].equalsIgnoreCase(table) && !upperLevelTable.equalsIgnoreCase(table)) {
                            if (this.is_array(level)) {
                                System.out.println(level[1]); // available related entities
                            }

                            //System.out.println(db.getEntity().getSearchables(table, false, true)); displaying the current table's searchable attributes to specify values
                            upperLevelTable = table;
                            isFound = true;

                            System.out.println("----------------.");
                        } else if (level[0].equalsIgnoreCase(table) && this.isRelatedWithAnyTable(table) && !upperLevelTable.equalsIgnoreCase(table)) {
                            //System.out.println(db.getEntity().getSearchables(table, false, true));
                            upperLevelTable = table;
                            isFound = true;

                            System.out.println("----------------..");
                        }
                    }

                    if (this.isRelatedWithAnyTable(table) && !isFound) { //  && !upperLevelTable.equalsIgnoreCase(table)
                        //System.out.println(db.getEntity().getSearchables(table, false, true));
                        upperLevelTable = table;
                        isFound = true;

                        System.out.println("----------------...");
                    }
            }

        } while (!table.equalsIgnoreCase(""));
    } // function end

    public String[] getSuggestableTables (String keyword) {
	// traversing through all the available/defined seachable tables
	String[] tables = db.getEntity().getDefinedSearchableTables(0);

	// counting the tables which have got a meta keyword
	int countOfTablesWithMetaKeyword = 0;
	String tableList = "";
	for (int i = 0; i < tables.length; i++) {
		String eachTableMetaDescription = db.getEntity().getEntityMeta(tables[i], 3); // #3: implicit_annotation

		// checking for the table meta description for any available keyword/s
		if (eachTableMetaDescription.toLowerCase().contains(keyword.toLowerCase())) {
			tableList += tables[i] + ",";
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

    public void identifyAttributes (String[] attributes) {
        for (int a = 0; a < attributes.length; a++) {
            String[] attributeData = attributes[a].split(":");
            System.out.println(attributeData[0]);
        }

        System.exit(0);
    }

    public String promptMessage (String message, boolean doTrim) {
        System.out.print(message);
        Scanner input = new Scanner(System.in);

        if (doTrim) {
            return input.nextLine().toString().replace(" ", "");
        } else {
            return input.nextLine().toString();
        }
    }

    public String createWhereClause (String[] rawDataArray, boolean isWithinJoin) {
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
    }

    public void getRealData(String sqlQuery, String[] sqlQueryMeta) {
        String eachRowData = "";
        try {
            if (sqlQuery != null) {
                Map[] resultsets = db.sqlSelect(sqlQuery, "null", null, null, null, null, true);
                System.out.println("1###Query :" + db.getQuery());

                // begin :traversing through each table row to display data
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
                System.out.println ("sqlQueryMeta.length: " + sqlQueryMeta.length);

                for (int c = 0; c < 2; c++) {
                    
                    String[] queryMeta = sqlQueryMeta[c].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
                    Map[] resultsets = db.sqlSelect(queryMeta[0], db.getEntity().getSearchables(queryMeta[0], false, false), queryMeta[1], null, null, null, false);
                    System.out.println("2###Query :" + db.getQuery());

                    // begin :traversing through each table row to display data
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

    public String[] initializeArray (String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
        return array;
    }

    public void vardumpArray (String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                System.out.println(array[i]);
        }
    }

    public int findExistingTableClausePrefixIndex (String[] array, String value, String splitter) {
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
    }

    /*
     * return the next insertion position for a given array
     */
    public int nextAvailableArrayIndex (String[] array) {
        int nextIndex = 0; // the very first array insertion index
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                nextIndex = i;
                break;
            }
        }
        return nextIndex;
    }

    public boolean isTableSelected (String[] array, String table) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
            String[] tableClause = array[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            if (tableClause[0].matches(".*"+table+".*"))
                return true;
            }
        }
        return false;
    }

    public boolean in_array (String[] array, String searchValue) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equalsIgnoreCase(searchValue))
                return true;

        return false;
    }

    public boolean is_array (String[] array) {
        try {
            String valueAtArrayIndexTwo = array[1];
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
        String[] tableArray = db.getFilteredTables();

        int totalKeyColumns = 0;
        for (int t = 0; t < tableArray.length; t++) {
            Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);
            totalKeyColumns += resultsets.length;
        }
        primaryKeyArray = new String[totalKeyColumns];
        foreignKeyArray = new String[totalKeyColumns];

        int primarykeycount = 0;
        int foreignkeycount = 0;
        // begin: traversing through each table
        for (int t = 0; t < tableArray.length; t++) {
            // need only the tables which contain searchable attributes
            if (!db.getEntity().getSearchables(tableArray[t], false, true).equalsIgnoreCase("")) {
                // sending the query to the database to retrieve data
                Map[] resultsets = db.sqlSelect("INFORMATION_SCHEMA.KEY_COLUMN_USAGE", "*", "table_name = '" + tableArray[t] + "'", null, null, null, false);

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
                                if (value.matches(".*fk.*")) { // @TODO this string comparison needs to be re-coded
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
                } // end :traversing through each table row to display data
            }
        } // end: traversing through each table
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

    /*public String[] getEntityRelations () {
        /*System.out.println("-------------------------------pk>>");
        this.vardumpArray(primaryKeyArray);
        System.out.println("-------------------------------fk>>");
        this.vardumpArray(foreignKeyArray);//*
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
                                relatedEntity += db.getEntity().getEntityMeta(primaryKeySplits[0], 1);// primaryKeySplits[0];
                            } else {
                                if (!relatedEntity.matches(".*"+primaryKeySplits[0]+".*"))
                                    relatedEntity += "," + db.getEntity().getEntityMeta(primaryKeySplits[0], 1);// primaryKeySplits[0]; // setting the displayable search entity/category name
                            }
                        }
                    }
                }

                if ((!nextForeignKeyTable.equalsIgnoreCase(foreignKeySplits[0]) || nextForeignKey == null) && !relatedEntity.equalsIgnoreCase("")) {
                    entityRelations += foreignKeySplits[0] + db.COLNAMETYPESP + relatedEntity + db.COLNAMETYPESP+db.COLNAMETYPESP;
                    //System.out.println(entityRelations);
                }
            }
        }
        entityRelations = entityRelations.substring(0, entityRelations.length()-2); // removing the last two characters. ex: .... les,Resources::
        String[] entityRelationsArray = entityRelations.split(db.COLNAMETYPESP+db.COLNAMETYPESP);

        return entityRelationsArray;
    }//*/
}
