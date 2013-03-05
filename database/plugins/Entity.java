package database.plugins;

/**
 *
 * @author Tharanga
 */
import database.Database;
import java.sql.*;

public class Entity {

    private boolean isEnabledSearchInAllAttributes = false;
    private boolean isEnabledSearchInAllTables = false;
    Database db = new Database();

    /*
     * @param (String)      table           : The table which it going to search
     * @param (boolean)     appendTableName : true, if we need in the form of <table>.<attribute>, <table>.<attribute> ...
     * @return (String)     attributes      : returns a set of possible searchable attributes to a given table
     */
    public String getSearchables(String table, boolean appendTableName, boolean getPredefined) {
        String attributes = "";

        db.setQuery("SELECT * FROM " + table);

        if (!this.isEnabledSearchInAllAttributes) {
            try {
                ResultSet rs = db.loadData();

                String[] arr = {""};
                if (getPredefined) {
                    arr = this.getEntityMeta(table, 1);
                } else {
                    arr = db.getMetaData(rs, 2); // get entity coloumns with their datatypes
                }

                for (int i = 0; i < arr.length; i++) {
                    if (!getPredefined) {
                    String[] splits = arr[i].split(db.COLNAMETYPESP);
                        if (this.isSearchable(splits[1])) { // column type
                            arr[i] = splits[0]; // column name
                            if (appendTableName)
                                attributes += table + "." + arr[i] + ", ";
                            else
                                attributes += arr[i] + ", ";
                        }
                    } else {
                        if (appendTableName)
                            attributes += table + "." + arr[i] + ", ";
                        else
                            attributes += arr[i] + ", ";
                    }
                    //System.out.println(i + ")    " + arr[i] + " ");
                }

                if (attributes != "") {
                    attributes = attributes.substring(0, (attributes.length()) - 2);
                }
                //System.out.println("    '" + attributes + "' ");
           } catch (Exception error) {
                System.out.println("\n" + error);
                System.exit(0);
            }//*/
        } else {
            attributes = "*";
        }

        return attributes;
    }

    public String makeClause(String searchables, String keyword) {
        if (searchables != null) {
            searchables = searchables.replaceAll(",", " LIKE '%" + keyword + "%' OR ");
            searchables += " LIKE '%" + keyword + "%'";

            return searchables;
        } else {
            return null;
        }
    }

    public boolean isSearchable(String datatype) {
        String[] searchableTypes = {"nvarchar", "varchar", "ntext", "text"};
        boolean isSearchable = false;

        for (int i = 0; i < searchableTypes.length; i++) {
            if (searchableTypes[i].equalsIgnoreCase(datatype)) {
                isSearchable = true;
            }
        }
        return isSearchable;
    }

    public String[] getSearchableTables () {
        String[] searchableTables = {"PROFILE", "USERS", "RSRC", "PROJECT", "CALENDAR", "ROLES", "DOCUMENT", "TASK"};
        
        if (this.isEnabledSearchInAllTables)
            return null;
        else
            return searchableTables;
    }

    public String[] getSearchableTables (int key) {
        if (key == 1) {
            return this.getSearchableTables();
        } else if (key == 2) {
            // @NOTE: don't specify the tables if they don't have searchable attributes
            String[] searchableTables = {
                "PROFILE::"+
                        "false::"+ // correct - false because, there is no attribute to search against
                        "prof_name::"+
                        "no description",
                "USERS::"+
                        "true::"+ // correct
                        "name::"+
                        "This contains the users of the system.",
                "RSRC::"+
                        "true::"+ // correct
                        "rsrc_name,office_phone,email_addr::"+
                        "This contains all the resources or in other words the people who is involved in several projects of the system. You can search for their email addresses as well.",
                "PROJECT::"+
                        "false::"+ // correct - false because, there is no exact attribute to search on
                        "plan_start_date::"+
                        "no description",
                "CALENDAR::"+
                        "false::"+ // correct
                        "clndr_data::"+
                        "no description",
                "ROLES::"+
                        "false::"+ // correct - false because, this is not directly invloved with anything
                        "name,short_name::"+
                        "no description",
                "DOCUMENT::"+
                        "true::"+ // not sure yet but seems important
                        "name,short_name,author_name::"+
                        "This contains the documents created by users of the system.",
                "TASK::"+
                        "true::"+
                        "name::"+
                        "This contains all the tasks which are defined in all projects. So you can search for task"
            };
            return searchableTables;
        }
        return null;
    }

    public String[] getEntityMeta (String table, int key) {
        String[] definedTableData = this.getSearchableTables(2);

        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            String eachTable = tableData[0];
            String isAJoin = tableData[1]; // a direct or a join candidate entity
            String searchableAttributes = tableData[2];
            String eachTableDescription = tableData[3];

            //System.out.println (eachTable +"|"+ searchableAttributes +"|"+ eachTableDescription);
            if (eachTable.equalsIgnoreCase(table) && key == 1) {
                if (searchableAttributes.split(",").length > 1) {
                    return searchableAttributes.split(",");
                } else {
                    String[] arr = {searchableAttributes};
                    return arr;
                }
            } else if (eachTable.equalsIgnoreCase(table) && key == 2) {
                String[] arr = {searchableAttributes};
                return arr;
            } else if (eachTable.equalsIgnoreCase(table) && key == 3) {
                String[] arr = {eachTableDescription};
                return arr;
            }
        }
        String[] arr = {"null"};
        return arr;
    }
}