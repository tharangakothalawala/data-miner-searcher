
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
    //private String[] foreignKeyArray;

    public Query (String[] primaryKeyArray, String[] foreignKeyArray) {
        this.init();
        this.primaryKeyArray = primaryKeyArray;
        //this.foreignKeyArray = foreignKeyArray;
    }

    public void init () {
        this.sqlQuery = "";
        this.selectClause = "SELECT";
        this.whereCondition = " WHERE ";
        this.joinStatement = "";
    }

    /*
     * this will creat the SQL join statement. ex. input: USERS s name a s name ad PROFILE s prof_name min s prof_name dmin
     */
    public String buildQuery (String[] queryRawDataArray, boolean includeJoinCondition) {
        this.init();
        String parentJoinTable = "";
        String parentTableCondition = "";

        // creating the select clause to the selected tables
        String sqlSelects = "";
        for (int i = 0; i < queryRawDataArray.length; i++) {
            if (queryRawDataArray[i] != null) {
                String[] tableData = queryRawDataArray[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);

                // always need to select first user slection (first category/entity)
                if (i == 0) {
                    sqlSelects +=  " " + tableData[1] + ",";
                }
                // only select table attributes if we have a condition to join with the first/parent selection. (array index 2 contains the condition)
                if (tableData.length > 1) {
                    if (i != 0 && !tableData[1].equalsIgnoreCase("")) {
                        sqlSelects +=  " " + tableData[1] + ",";
                    }
                }
            }
        }

        for (int i = 0; i < queryRawDataArray.length; i++) {
            if (queryRawDataArray[i] != null) {
                // index 0 means to consider the 0th index values as related to levelOne table
                if (i == 0) {
                    String[] mainTableData = queryRawDataArray[0].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP); // seperating the table::with their coditions
                    parentJoinTable = mainTableData[0];
                    if (mainTableData.length > 1) {
                        this.whereCondition += this.createJoinWhereClause(mainTableData, false);
                        parentTableCondition = this.whereCondition;
                    }

                    this.selectClause += sqlSelects.substring(0, (sqlSelects.length()) - 1) + " FROM " + mainTableData[0]; // SELECT * FROM <selected_first_table>
                } else {
                    // JOINs will be set in here
                    String[] mainTableData = queryRawDataArray[i-1].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);
                    parentJoinTable = mainTableData[0];
                    String[] tableClause = queryRawDataArray[i].split(this.db.COLNAMETYPESP+this.db.COLNAMETYPESP);

                    String[] leftjoinData = primaryKeyArray[this.findExistingTableClausePrefixIndex(primaryKeyArray, tableClause[0], db.COLNAMETYPESP)].split(db.COLNAMETYPESP);
                    String condition = "";
                    //System.out.println(tableClause.length);
                    if (tableClause.length > 1) {
                        if (includeJoinCondition) {
                            condition = " AND " + this.createJoinWhereClause(tableClause, true);
                        }

                        joinStatement += " JOIN " + tableClause[0] + " ON " + tableClause[0] + "." + leftjoinData[1] + " = " + parentJoinTable + "." + leftjoinData[1] + condition;
                    } else {
                        condition = "";
                    }
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

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

}
