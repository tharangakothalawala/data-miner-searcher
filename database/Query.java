
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package database;

import java.util.*;

public class Query {

    private Database db = new Database();
    
    private String sqlQuery;
    private String selectClause;
    private String whereCondition;
    private String joinStatement;
    //private boolean fetchCount;

    private String[] primaryKeyArray;
    private String[] foreignKeyArray;

    public Query (String[] primaryKeyArray, String[] foreignKeyArray) {
        this.init();
        this.primaryKeyArray = primaryKeyArray;
        this.foreignKeyArray = foreignKeyArray;
    }

    public void init () {
        this.sqlQuery = "";
        this.selectClause = "SELECT";
        this.whereCondition = " WHERE ";
        this.joinStatement = "";
    }

    /*
     * @param (String[])    queryRawDataArray   : This contains the processed raw user input data
     * @param (boolean)     includeJoinCondition: This indicates where the query needs a conditions for the JOINing table
     */
    public String buildQuery (String[] queryRawDataArray, boolean includeJoinCondition) {
        this.init();
        String parentJoinTable = "";
        String parentTableCondition = "";

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
                // index 0 means to consider the 0th index values as related to levelOne table
                if (i == 0) {
                    String[] rootTableData = queryRawDataArray[0].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP); // seperating the data by "::"
                    parentJoinTable = rootTableData[0];

                    this.whereCondition += this.createJoinWhereClause(rootTableData, false);
                    parentTableCondition = this.whereCondition;

                    this.selectClause += sqlSelects.substring(0, (sqlSelects.length()) - 1) + " FROM " + rootTableData[0]; // SELECT * FROM <selected_root_table>
                } else {
                    // creating the rest of the SQL statement including the JOINs
                    String[] parentJoinTableData = queryRawDataArray[i-1].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);
                    parentJoinTable = parentJoinTableData[0];
                    String[] joinTableClause = queryRawDataArray[i].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);

                    String[] joinTableKeyData = primaryKeyArray[this.getArrayIndexAtValue(primaryKeyArray, joinTableClause[0], db.COLNAMETYPESP)].split(db.COLNAMETYPESP); // getting the primary key for a table
                    String[] parentTableKeyData = foreignKeyArray[this.getArrayIndexAtValue(foreignKeyArray, parentJoinTable, db.COLNAMETYPESP)].split(db.COLNAMETYPESP); // getting the foreign key for a table
                    String condition = "";

                    if (includeJoinCondition) {
                        // this is used to specify conditions for the join table (Facets)
                        condition = " AND " + this.createJoinWhereClause(joinTableClause, true);
                    }

                    joinStatement += " JOIN " + joinTableClause[0] + " ON " + joinTableKeyData[0] + "." + joinTableKeyData[1] + " = " + parentTableKeyData[0] + "." + parentTableKeyData[1] + condition;
                }
            }
        }
        joinStatement += parentTableCondition;

        this.setSqlQuery(this.selectClause + joinStatement + ";");
        return this.getSqlQuery();
    }

    public int getCount (String sqlQuery) {
        if (sqlQuery != null) {
            Map[] resultsets = db.sqlSelect(sqlQuery, "null", null, null, null, null, true);

            Map<String, String> rs = resultsets[0];
            return Integer.parseInt(rs.get("COUNT(*)"));
        }
        return 0;
    }

    public String makeClause (String searchables, String keyword) {
        if (searchables != null) {
            searchables = searchables.replaceAll(",", " LIKE '%" + keyword + "%' OR ");
            searchables += " LIKE '%" + keyword + "%'";

            return searchables;
        } else {
            return null;
        }
    }

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

    public int getArrayIndexAtValue (String[] array, String value, String splitter) {
        int index = 0; // the very first array insertion index
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null){
                String[] arrayValue = array[i].split(splitter);
                if (arrayValue[0].toString().equalsIgnoreCase(value)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

}
