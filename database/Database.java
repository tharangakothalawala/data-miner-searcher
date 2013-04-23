
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 * @Purpose	This class is to do the database specific functions.
 */

package database;

import java.sql.*;
import java.util.*;

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
    public String connurl;
    public String dbname;
    private String dbuser;
    private String dbpasswd;

    // query parameters
    private String query = "";
    private Map[] resultset; // to return all the loaded data
    private Map<String, String> map; // to hold each row

    // other configuration parameters
    public final String COLNAMETYPESP = ":"; // to be used to identify metadata ex: username:nvarchar. And also act as data seperator
    public String searchable_data_types;
    public int entityDisplayLimit;
    public boolean considerUserAttributeSelectionForWhereClause;

    /*
     * Constructor intializes the database configuration
     */
    public Database() {
        this.considerUserAttributeSelectionForWhereClause = false;
        boolean isFoundEnabledDB = false;
        try {
            File DBConfigFile = new File("config/configuration.xml");
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
                            this.dbdriver = element.getElementsByTagName("dbDriver").item(0).getTextContent();
                            this.connurl = element.getElementsByTagName("connectionUrl").item(0).getTextContent();
                            this.dbname = element.getElementsByTagName("dbName").item(0).getTextContent();
                            this.dbuser = element.getElementsByTagName("dbUser").item(0).getTextContent();
                            this.dbpasswd = element.getElementsByTagName("dbPasswd").item(0).getTextContent();
                            this.searchable_data_types = element.getElementsByTagName("searchableDataTypes").item(0).getTextContent();
                            this.entityDisplayLimit = Integer.parseInt(element.getElementsByTagName("entityDisplayLimit").item(0).getTextContent());
                            if (element.getElementsByTagName("acceptAttributeRequestValuesForClause").item(0).getTextContent().equalsIgnoreCase("1")) {
                                this.considerUserAttributeSelectionForWhereClause = true;
                            }
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Error : The system cannot find the configuration.xml file! Check the file at 'config/configuration.xml'\n\n" + e);
            System.exit(0);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error : XML configuration file error! Check the content of the file at 'config/configuration.xml'\n\n" + e);
            System.exit(0);
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

            return DriverManager.getConnection(this.connurl, this.dbuser, this.dbpasswd);
        } catch (Exception ex) {
            System.out.println("Error : Unable to Connect to the Database, '" + this.dbname + "'.\nPlease look at the configuration.xml for the connection parameters.");
            System.exit(0);
        }
        return null;
    }

    /*
     * @param (String)	query	: SQL query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /*
     * @return (String)	query	: return the SQL query
     */
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
            case 1: // will get the number of attributes per table
                entityMetaArray = new String[2];
                entityMetaArray[0] = numberOfColumns + "";

                // will get the count of data rows
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                }

                entityMetaArray[1] = rowCount + "";
                break;
            case 2: // will get the data type for each attribute
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

    /*
     * @desc				: SQL statement creation and database query function
     * @param (String)	table		: the table name. Also when the "isForced" is true, it identifies this param, "table" as the pre-created SQL statement to execute
     * @param (String)	columns		: the SQL SELECT columns
     * @param (String)	clause		: the SQL WHERE clause
     * @param (String)	group		: the SQL GROUP BY
     * @param (String)	order		: the SQL ORDER BY
     * @param (String)	limit		: the SQL LIMIT
     * @param (boolean)	isForced	: send true to force the pre-created SQL statment to excute
     * @return (Map[])	resultset	: the results came out of the database
     */
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

    /*
     * @return (ResultSet)	rs	: the results came directly out of the database after the query execution
     */
    public ResultSet loadData() throws Exception {
        Statement s = getDbConnection().createStatement();

        s.execute(this.query);
        ResultSet rs = s.getResultSet(); // get any ResultSet that came from the query

        return rs;
    }

    /*
     * @return (Entity)		: this returns an Entity instance to access the table specific data (ex: searchable attributes etc)
     */
    public Entity getEntity() {
        return new Entity();
    }

    /*
     * Validating the defined related_tables in the XML file with the existing relations in the database
     * @param (String[])	foreignKeyArray	: array of foreign keys detected from the database to valiate against the defined XML data
     */
    public void ensureRelations(String[] foreignKeyArray) {
        int detectedRelationCount = 0;
        int definedRelationCount = 0;

        String[] tableRelationArray = this.getEntity().getEntityConfigValuesAtIndex(4);
        for (int r = 0; r < tableRelationArray.length; r++) {
            if (!tableRelationArray[r].equalsIgnoreCase("null")) {
                String[] arrayValue = tableRelationArray[r].split(",");
                definedRelationCount += arrayValue.length;
            }
        }
        String[] tableArray = this.getEntity().getEntityConfigValuesAtIndex(0);
        for (int t = 0; t < tableArray.length; t++) {
            String relatedTables = this.getEntity().getEntityMeta(tableArray[t], 5, false);
            if (!relatedTables.equalsIgnoreCase("null")) { // check only the tables which related_table have been defined
                for (int i = 0; i < foreignKeyArray.length; i++) {
                    if (foreignKeyArray[i] != null) {
                        String[] arrayValue = foreignKeyArray[i].split(this.COLNAMETYPESP);
                        String[] related = relatedTables.split(",");
                        if (arrayValue[0].toString().equalsIgnoreCase(tableArray[t]) && relatedTables.equalsIgnoreCase(arrayValue[2]) && !Functions.is_array(related)) {
                            //System.out.println(tableArray[t] + "(SINGLE) - foreignKeyArray: " + foreignKeyArray[i]);
                            detectedRelationCount++;
                        } else if (Functions.is_array(related) && relatedTables.contains(arrayValue[2])) {
                            for (int b = 0; b < related.length; b++) {
                                if (arrayValue[0].toString().equalsIgnoreCase(tableArray[t]) && arrayValue[2].toString().equalsIgnoreCase(related[b])) {
                                    //System.out.println(tableArray[t] + "(MULTIPLE-" + related.length + ") - foreignKeyArray:" + foreignKeyArray[i]);
                                    detectedRelationCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (definedRelationCount != detectedRelationCount) {
            System.out.println("Error : Undefined entity relationships found! Check the foreign key constraints in the database schema!" +
		"\n\nRun the following SQL as user, 'root' in the database to see the existing relations and " +
                "compare them with the DB definitions in 'config/databases/" + this.dbname + "_entity_config.xml' file. (<related_tables>)" +
		"\n\n> SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE (CONSTRAINT_SCHEMA = '" + this.dbname + "' OR CONSTRAINT_CATALOG = '" + this.dbname + "');");
            System.exit(0);
        }
    }
}
