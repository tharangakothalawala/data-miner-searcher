package database.plugins;

/**
 *
 * @author Tharanga
 */
import database.Database;
import java.sql.*;

public class Entity {

    private boolean doSearchInAll = false;
    private boolean isEnabledSearchInAllTables = false;
    Database db = new Database();

    /*
     * @param (String)      table           : The table which it going to search
     * @return (String)     attributes      : returns a set of possible searchable attributes to a given table
     */
    public String getSearchables(String table) {
        String attributes = "";

        db.setQuery("SELECT * FROM " + table);

        if (!this.doSearchInAll) {
            try {
                ResultSet rs = db.loadData();

                String[] arr = db.getMetaData(rs, 2); // get entity coloumns with their datatypes

                for (int i = 0; i < arr.length; i++) {
                    String[] splits = arr[i].split(db.COLNAMETYPESP);
                    if (this.isSearchable(splits[1])) { // column type
                        arr[i] = splits[0]; // column name
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
            }
        } else {
            attributes = "*";
        }

        return attributes;
    }

    public String makeClause(String searchables, String keyword) {
        if (searchables != null) {
            searchables = searchables.replaceAll(",", " LIKE '%" + keyword + "%' OR");
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
        String[] searchableTables = {"PROFILE", "USERS", "RSRC", "PROJECT", "CALENDAR", "ROLES", "DOCUMENT"};

        if (this.isEnabledSearchInAllTables)
            return null;
        else
            return searchableTables;
    }
    /*
    public static void main(String argv[]) {
    try {
    Db db = new Db(argv);

    System.out.println();
    System.out.println(
    "THIS SAMPLE SHOWS HOW TO GET INFO ABOUT DATA TYPES.");

    // connect to the 'sample' database
    db.connect();

    // Get information about the Data type
    infoGet(db.con);

    db.con.commit();

    // disconnect from the 'sample' database
    db.disconnect();
    } catch (Exception e) {
    JdbcException jdbcExc = new JdbcException(e);
    jdbcExc.handle();
    }
    } // main

    static void infoGet(Connection con) {
    try {
    System.out.println();
    System.out.println(
    "----------------------------------------------------------\n"
    + "USE THE JAVA APIs:\n"
    + "  Connection.getMetaData()\n"
    + "  ResultSet.getTypeInfo()\n"
    + "  ResultSetMetaData.getMetaData()\n"
    + "TO GET INFO ABOUT DATA TYPES AND\n"
    + "TO RETRIEVE THE AVAILABLE INFO IN THE RESULT SET.");

    DatabaseMetaData dbMetaData = con.getMetaData();

    // Get a description of all the standard SQL types supported by
    // this database
    ResultSet rs = dbMetaData.getTypeInfo();

    // Retrieve the number, type and properties of the resultset's columns
    ResultSetMetaData rsMetaData = rs.getMetaData();

    // Get the number of columns in the ResultSet
    int colCount = rsMetaData.getColumnCount();
    System.out.println();
    System.out.println(
    "  Number of columns in the ResultSet = " + colCount);

    // Retrieve and display the column's name along with its type
    // and precision in the ResultSet
    System.out.println();
    System.out.println("  A LIST OF ALL COLUMNS IN THE RESULT SET:\n"
    + "    Column Name         Column Type\n"
    + "    ------------------- -----------");

    String colName, colType;
    for (int i = 1; i <= colCount; i++) {
    colName = rsMetaData.getColumnName(i);
    colType = rsMetaData.getColumnTypeName(i);
    System.out.println(
    "    " + Data.format(colName, 19)
    + " " + Data.format(colType, 13) + " ");
    }

    System.out.println();
    System.out.println(
    "  HERE ARE SOME OF THE COLUMNS' INFO IN THE TABLE ABOVE:\n"
    + "           TYPE_NAME          DATA_  COLUMN    NULL-   CASE_\n"
    + "                              TYPE   _SIZE     ABLE  SENSITIVE\n"
    + "                              (int)                          \n"
    + "    ------------------------- ----- ---------- ----- ---------");

    String typeName;
    int dataType;
    Integer columnSize;
    boolean nullable;
    boolean caseSensitive;
    
    // Retrieve and display the columns' information in the table
    while (rs.next()) {
    typeName = rs.getString(1);
    dataType = rs.getInt(2);
    if (rs.getInt(7) == 1) {
    nullable = true;
    } else {
    nullable = false;
    }
    if (rs.getInt(8) == 1) {
    caseSensitive = true;
    } else {
    caseSensitive = false;
    }
    if (rs.getString(3) != null) {
    columnSize = Integer.valueOf(rs.getString(3));
    System.out.println(
    "    " + Data.format(typeName, 25)
    + " " + Data.format(dataType, 5)
    + " " + Data.format(columnSize, 10)
    + " " + Data.format(String.valueOf(nullable), 5)
    + " " + Data.format(String.valueOf(caseSensitive), 10));
    } else // for the distinct data type, column size does not apply
    {
    System.out.println(
    "    " + Data.format(typeName, 25)
    + " " + Data.format(dataType, 5)
    + "        n/a"
    + " " + Data.format(String.valueOf(nullable), 5)
    + " " + Data.format(String.valueOf(caseSensitive), 10));
    }
    }
    // close the result set
    rs.close();
    } catch (Exception e) {
    JdbcException jdbcExc = new JdbcException(e);
    jdbcExc.handle();
    }
    }
    //*/
}
