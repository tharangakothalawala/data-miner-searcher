package database;

import java.sql.*;
import java.util.*;
import database.plugins.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Database {

    // database parameters
    private String dbdriver;
    private String connurl;
    public String dbname;
    private String dbuser;
    private String dbpasswd;//*/

    // query parameters
    private String query = "";
    private Map[] resultset; // to return all the loaded data
    private Map<String, String> map; // to hold each row
    // other parameters
    public final String COLNAMETYPESP = ":"; // to be used to identify metadata ex: username:nvarchar
    private String[] dbtableArray;
    private String[] dbviewArray;
    public int entityDisplayLimit;

    public Database() {
        boolean isFoundEnabledDB = false;
        try {
            File DBConfigFile = new File("src/database/configuration.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(DBConfigFile);

            document.getDocumentElement().normalize();

            NodeList databaseList = document.getElementsByTagName("database"); // each database

            for (int i = 0; i < databaseList.getLength(); i++) {
                if (!isFoundEnabledDB) {
                    Node database = databaseList.item(i);

                    if (database.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) database;

                        if (element.getElementsByTagName("isEnabled").item(0).getTextContent().equalsIgnoreCase("1")) {
                            isFoundEnabledDB = true;
                            this.dbdriver = element.getElementsByTagName("dbdriver").item(0).getTextContent();
                            this.connurl = element.getElementsByTagName("connurl").item(0).getTextContent();
                            this.dbname = element.getElementsByTagName("dbname").item(0).getTextContent();
                            this.dbuser = element.getElementsByTagName("dbuser").item(0).getTextContent();
                            this.dbpasswd = element.getElementsByTagName("dbpasswd").item(0).getTextContent();
                            this.entityDisplayLimit = Integer.parseInt(element.getElementsByTagName("entityDisplayLimit").item(0).getTextContent());
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isFoundEnabledDB) { // if it is not found an enabled db configuration
            System.out.println("Error : No database connection parameters found!");
            System.exit(0);
        }
    }

    // establish a connection between database and returns the connection
    public Connection getDbConnection() {
        try {
        // loading the driver for the MS SQL server
        Class.forName(this.dbdriver);
        //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        //return DriverManager.getConnection("jdbc:sqlserver://" + this.host + ":" + this.port + ";databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd);
        return DriverManager.getConnection(this.connurl, this.dbuser, this.dbpasswd);
        //return DriverManager.getDbConnection("jdbc:sqlserver://176.250.128.96:1433;databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd); // this is to connect to the database at home remotely
        } catch (Exception ex) {
            System.out.println("Error : Cannot connect to the database, '" + this.dbname + "'");
            System.exit(0);
        }
        return null;
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
    public Map[] sqlSelect(String table, String columns, String clause, String group, String order, String limit, boolean isForced) {
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

                // building the query
                String localQuery = "SELECT " + columns
                        + " FROM " + table + clause
                        + group + order
                        + limit + ";";

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
            return null;
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
            Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.TABLES", "TABLE_NAME", "TABLE_CATALOG = '" + dbname + "' OR TABLE_SCHEMA = '" + dbname + "'", null, null, null, false);
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
            Map[] resultsets = this.sqlSelect("INFORMATION_SCHEMA.VIEWS", "TABLE_NAME", "TABLE_CATALOG = '" + dbname + "' OR TABLE_SCHEMA = '" + dbname + "'", null, null, null, false);

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

        if (this.getEntity().getSearchableTables(0) != null) {
            dbtableArray = this.getEntity().getSearchableTables(0);

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
