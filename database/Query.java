
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package database;

import java.util.*;

public class Query {

    private Database db = new Database();

    private String selectClause;
    private String whereCondition;
    private String joinStatement;

    private String[] primaryKeyArray;
    private String[] foreignKeyArray;

    public Query (String[] primaryKeyArray, String[] foreignKeyArray) {
        this.init();
        this.primaryKeyArray = primaryKeyArray;
        this.foreignKeyArray = foreignKeyArray;
    }

    /*
     * Initializes the key variables
     */
    public void init () {
        this.selectClause = "SELECT";
        this.whereCondition = " WHERE ";
        this.joinStatement = "";
    }

    /*
     * @param (String[])    queryRawDataArray   : This contains the processed raw user input data
     * @param (boolean)     includeJoinCondition: This indicates whether the query needs a conditions for the JOINing table or not
     */
    public String buildQuery (String[] queryRawDataArray, boolean isFacetedSearchMode) {
        this.init();
        String parentJoinTable = "";
        String condition = "";

        // creating the select clause
        String sqlSelects = "";
        for (int i = 0; i < queryRawDataArray.length; i++) {
            if (queryRawDataArray[i] != null) {
                String[] tableData = queryRawDataArray[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);

                // tableData[1] : index 1 contains the required SELECT fields
                if (!tableData[1].equalsIgnoreCase("")) {
                    sqlSelects +=  " " + tableData[1] + ",";
                }
            }
        }

        for (int i = 0; i < queryRawDataArray.length; i++) {
            if (queryRawDataArray[i] != null) {
                // index 0 means to consider the 0th index values as <selected_root_table> data
                if (i == 0) {
                    String[] rootTableData = queryRawDataArray[0].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP); // seperating the data by "::"
                    parentJoinTable = rootTableData[0];

                    this.whereCondition += this.createJoinWhereClause(rootTableData, false);

                    this.selectClause += sqlSelects.substring(0, (sqlSelects.length()) - 1) + " FROM " + rootTableData[0]; // SELECT <attributes> FROM <selected_root_table>
                } else {
                    // creating the rest of the SQL statement including the JOINs
                    String[] parentJoinTableData = queryRawDataArray[i-1].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);
                    parentJoinTable = parentJoinTableData[0];
                    String[] joinTableClause = queryRawDataArray[i].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);

                    String[] joinTableKeyData = primaryKeyArray[this.getArrayIndexAtValue(primaryKeyArray, joinTableClause[0], db.COLNAMETYPESP, false)].split(db.COLNAMETYPESP); // getting the primary key for a table
                    String[] parentTableKeyData = foreignKeyArray[this.getArrayIndexAtValue(foreignKeyArray, parentJoinTable + db.COLNAMETYPESP + joinTableClause[0], db.COLNAMETYPESP, true)].split(db.COLNAMETYPESP); // getting the foreign key for a table

                    if (isFacetedSearchMode) { // include conditions for the JOIN table
                        // this is used to specify conditions for the join table (Facets)
                        condition = " AND " + this.createJoinWhereClause(joinTableClause, true);
                    }

                    joinStatement += " JOIN " + joinTableClause[0] + " ON " + joinTableKeyData[0] + "." + joinTableKeyData[1] + " = " + parentTableKeyData[0] + "." + parentTableKeyData[1] + condition;
                }
            }
        }
        joinStatement += this.whereCondition;

        return this.selectClause + joinStatement + ";";
    }

    /*
     * This returns the query result count
     * @param	(String)	sqlQuery	: the pre-generated SQL COUNT query
     * @return	(int)		count		: number of data rows
     */
    public int getCount (String sqlQuery) {
        if (sqlQuery != null) {
            Map[] resultsets = db.sqlSelect(sqlQuery, "null", null, null, null, null, true);

            Map<String, String> rs = resultsets[0];
            return Integer.parseInt(rs.get("COUNT(*)"));
        }
        return 0;
    }

    /*
     * This creates the formatted WHERE clause
     * @param	(String)	searchables	: attributes which are needed in the WHERE clause
     * @param	(String)	keyword		: user search keyword
     */
    public String makeClause (String searchables, String keyword) {
        if (searchables != null) {
            searchables = searchables.replaceAll(",", " LIKE '%" + keyword + "%' OR ");
            searchables += " LIKE '%" + keyword + "%'";

            return searchables;
        } else {
            return null;
        }
    }

    /*
     * Similar version of the above function to create the WHERE clause,
	But here it is not creating the clause but passing the already created clause
     */
    public String createJoinWhereClause (String[] rawDataArray, boolean isWithinJoin) {
        String[] conditions = rawDataArray[2].split(db.COLNAMETYPESP);
        String whereClause = "";
        for (int k = 0; k < conditions.length; k++) {
            whereClause += conditions[k] + " OR ";
        }
        whereClause = whereClause.substring(0, (whereClause.length()) - 4);
        if (isWithinJoin)
            return "(" + whereClause + ")";
        else
            return whereClause;
    }

    /*
     * Returns the index of a primary/foreign array where a matching value is found in the first splited part of any value
     * @param	(String[])	array		: the array with values
     * @param	(String)	value		: the value to be matched against
     * @param	(boolean)	isForeignKeyLookup	: true value indicates a foreign key lookup,
	it will check for two value to get the correct foreign key table reference
     * @param	(String)	splitter	: the value splitter symbol
     */
    public int getArrayIndexAtValue (String[] array, String value, String splitter, boolean isForeignKeyLookup) {
        int index = 0; // the very first array insertion index
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null){
                if (!isForeignKeyLookup) {
                    String[] arrayValue = array[i].split(splitter);
                    if (arrayValue[0].toString().equalsIgnoreCase(value)) {
                        index = i;
                        break;
                    }
                } else {
                    String[] arrayValue = array[i].split(splitter);
                    String[] valueSplit = value.split(splitter);
                    if (arrayValue[0].toString().equalsIgnoreCase(valueSplit[0]) && arrayValue[2].toString().equalsIgnoreCase(valueSplit[1])) {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

}
