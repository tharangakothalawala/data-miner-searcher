package database;

import java.sql.*;
import java.util.*;
import database.plugins.*;

public class Database {

    // database parameters
    private String host = "localhost"; // localhost | 192.168.232.134
    private int port = 1433;
    private String dbname = "CGDB";
    private String dbuser = "sa";
    private String dbpasswd = "qwerty1"; // FooBar(1)?
    // query parameters
    private String query = "";
    private Map[] resultset; // to return all the loaded data
    private Map<String, String> map; // to hold each row
    // other parameters
    public final String COLNAMETYPESP = ":"; // to be used to identify metadata ex: username:nvarchar
    private String[] dbtableArray;
    private String[] dbviewArray;

    // establish a connection between database and returns the connection
    public Connection getDbConnection() throws Exception {
        // loading the driver for the MS SQL server
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        return DriverManager.getConnection("jdbc:sqlserver://" + this.host + ":" + this.port + ";databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd);
        //return DriverManager.getDbConnection("jdbc:sqlserver://176.250.128.96:1433;databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd); // this is to connect to the database at home remotely
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }

    /*
     * @param (ResultSet)	rs		: The rs resultset used to fetch all the meta data about the query being processed
     * @param (int)		key		: The param used to identify what is needed as the output
     * @return (String[])	entityMetaArray	: The returnArray contains the data which is requested. (return data will depend on the second param)
     */
    public String[] getMetaData(ResultSet rs, int key) throws Exception {

        String[] entityMetaArray = new String[0];

        // creating ResultSetMetaData instance to get meta data
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        switch (key) {
            case 1: // will get the number of attributes
                entityMetaArray = new String[2];
                entityMetaArray[0] = numberOfColumns + "";

                // will get the row count
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                }

                entityMetaArray[1] = rowCount + "";
                break;
            case 2: // will get the data type of each attribute
                entityMetaArray = new String[numberOfColumns];

                String columnName = "",
                 columnType = "";
                for (int i = 0; i < numberOfColumns; i++) {
                    columnName = rsMetaData.getColumnName(i + 1);
                    columnType = rsMetaData.getColumnTypeName(i + 1);
                    entityMetaArray[i] = columnName + this.COLNAMETYPESP + columnType;
                }
                break;
        }

        return entityMetaArray;
    }

    // SQL select
    public Map[] sqlSelect(String table, String columns, String clause, String group, String order, String limit, String offset, boolean isForced) {
        try {
            if (table != null && columns != null) {
                if (clause != null) {
                    clause = " WHERE " + clause + "";
                } else {
                    clause = "";
                }
                if (group != null) {
                    group = " GROUP BY " + group + "";
                } else {
                    group = "";
                }
                if (order != null) {
                    order = " ORDER BY " + order + "";
                } else {
                    order = "";
                }
                if (limit != null) {
                    limit = " LIMIT " + limit + "";
                } else {
                    limit = "";
                }
                if (offset != null) {
                    offset = " OFFSET " + offset + "";
                } else {
                    offset = "";
                }

                // building the query
                String localQuery = "SELECT " + columns
                        + " FROM " + table + clause
                        + group + order
                        + limit + offset + ";";

                //System.out.println("Query: " + localQuery);

                if (isForced) {
                    this.setQuery(table);
                } else {
                    this.setQuery(localQuery);
                }
                ResultSet rs = this.loadData();

                // getting the row and column count to initialize the resultset array
                ResultSet resultSet = this.loadData();
                String[] entityMetaArray = getMetaData(resultSet, 1); // 1 denotes the row & column count enquiry
                int numberOfColumns = Integer.parseInt(entityMetaArray[0]);
                int numberOfRows = Integer.parseInt(entityMetaArray[1]);
                resultset = new Map[numberOfRows];

                int i = 0;
                if (rs != null) { // if rs == null, then there is no ResultSet to view
                    while (rs.next()) { // this will step through the data row-by-row
                        map = new HashMap<String, String>();
                        for (int column = 1; column <= numberOfColumns; column++) {
                            map.put(rs.getMetaData().getColumnName(column), rs.getString(column));
                            //System.out.println("(" + i + "," + column + ") ['" + rs.getMetaData().getColumnName(column) + "'] = " + map.get(rs.getMetaData().getColumnName(column)));
                        }
                        resultset[i] = map;
                        i++;
                    }
                    return resultset;
                }
            }
        } catch (Exception error) {
            //System.out.println("\n" + ERROR + error);
            map = new HashMap<String, String>();
            map.put("ERROR", "" + error);
            resultset[0] = map;
            return resultset;
            //    System.exit(0);
        }
        return resultset;
    }

    public ResultSet loadData() throws Exception {
        Statement s = getDbConnection().createStatement();

        s.execute(this.query);
        ResultSet rs = s.getResultSet(); // get any ResultSet that came from the query

        return rs;
    }

    public String[] getDatabaseTableList() {

        try {
            Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", "TABLE_CATALOG = '" + dbname + "' OR TABLE_SCHEMA = '" + dbname + "'", null, null, null, null, false);
            //Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", "TABLE_CATALOG = '" + dbname + "'", null, null, null, null);
            //Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", "TABLE_SCHEMA = '" + dbname + "'", null, null, null, null);

            // set is_callable type codes in here to check for any plugins/functions in Entity to get a pre-defined tables to the 'dbtableArray'
            dbtableArray = new String[resultsets.length];

            for (int i = 0; i < resultsets.length; i++) {
                Map<String, String> rs = resultsets[i];
                // System.out.println("Table: "+ rs.get("TABLE_NAME"));
                dbtableArray[i] = rs.get("TABLE_NAME");
            }
        } catch (Exception error) {
            System.out.println("\n" + error);
            System.exit(0);
        }

        return dbtableArray;
    }

    public String[] getDatabaseViewList() {

        try {
            Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.VIEWS", "TABLE_NAME", "TABLE_CATALOG = '" + dbname + "' OR TABLE_SCHEMA = '" + dbname + "'", null, null, null, null, false);

            dbviewArray = new String[resultsets.length];

            for (int i = 0; i < resultsets.length; i++) {
                Map<String, String> rs = resultsets[i];
                // System.out.println("Table: "+ rs.get("TABLE_NAME"));
                dbviewArray[i] = rs.get("TABLE_NAME");
            }
        } catch (Exception error) {
            System.out.println("\n" + error);
            System.exit(0);
        }

        return dbviewArray;
    }

    /*
     * This is used only to call this.getDatabaseTableList();
     * TODO : this needs to be deprecated
     */
    public String getTables() {
        String[] tableArray = this.getDatabaseTableList();

        String tables = "";
        for (int i = 0; i < tableArray.length; i++) {
            tables += tableArray[i] + ", ";
        }
        tables = tables.substring(0, (tables.length()) - 2);
        return tables;
    }

    /*
     * This is used only to call this.getDatabaseViewList();
     * TODO : this needs to be deprecated
     */
    public String getViews() {
        String[] tableArray = this.getDatabaseViewList();

        String tables = "";
        for (int i = 0; i < tableArray.length; i++) {
            tables += tableArray[i] + ", ";
        }
        tables = tables.substring(0, (tables.length()) - 2);
        return tables;
    }

    public String[] getFilteredTables() {
        String table, view = "";

        if (this.getEntity().getSearchableTables() != null) {
            dbtableArray = this.getEntity().getSearchableTables();

            return dbtableArray;
        } else {
            // creating the search context by retrieving searchable tables from the database
            this.getTables();
            this.getViews();

            for (int j = 0; j < dbtableArray.length; j++) {
                //System.out.println("Table: "+ dbtableArray[j]);
                for (int i = 0; i < dbviewArray.length; i++) {
                    table = dbtableArray[j];
                    view = dbviewArray[i];
                    //System.out.println(table + " || "+ view);
                    if (table.equalsIgnoreCase(view)) {

                        //System.out.println("VIEWS: " + dbtableArray[j]);
                        dbtableArray[j] = "null";
                        //      dbtableArray2[j] = rs.get("TABLE_NAME");
                    }
                }
            }
            //   }
            int viewCount = 0;
            for (int i = 0; i < dbtableArray.length; i++) {
                if (dbtableArray[i].equalsIgnoreCase("null")) {
                    viewCount++;
                }
                //System.out.println(i + "). Table: " + dbtableArray[i]);
            }

            System.out.println("viewCount: " + viewCount);

            String[] finalArray = new String[dbtableArray.length - viewCount];
            int c = 0;
            for (int i = 0; i < dbtableArray.length; i++) {
                if (!dbtableArray[i].equalsIgnoreCase("null")) {
                    finalArray[c] = dbtableArray[i];
                    c++;
                    //System.out.println("Table: " + dbtableArray[i]);
                }
            }

            return finalArray;
        }
    }

    /*
     * @return (Entity)		: this returns an Entity instance to access the table specific data (ex: searchable attributes etc)
     */
    public Entity getEntity() {
        return new Entity();
    }
}
