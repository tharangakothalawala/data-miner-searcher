package database;

import java.sql.*;
import java.util.*;

public class Database {

    private final String ERROR = "Error... ";
    // database parameters
    private String dbname = "CGDB";
    private String dbuser = "sa";
    private String dbpasswd = "qwerty1";
    // query
    private String query = "";
    private Map[] resultset;
    private Map<String, String> map;

    // establish a connection between database and returns the connection
    public Connection getDbConnection() throws Exception {
        // loading the driver for the MS SQL server
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        return DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd);
        //return DriverManager.getDbConnection("jdbc:sqlserver://176.250.128.96:1433;databaseName=" + this.dbname + ";", this.dbuser, this.dbpasswd); // this is to connect to the database at home remotely
    }

    public void setQuery(String query) {
        this.query = query;
    }

    // extended select
    public Map[] sqlSelectExtended(String table, String columns, String clause, String group, String order, String limit, String offset) {
        try {
            if (table != null && columns != null) {
                if (clause != null) { clause = " WHERE " + clause + ""; } else { clause = ""; }
                if (group != null) { group = " GROUP BY " + group + ""; } else { group = ""; }
                if (order != null) { order = " ORDER BY " + order + ""; } else { order = ""; }
                if (limit != null) { limit = " LIMIT " + limit + ""; } else { limit = ""; }
                if (offset != null) { offset = " OFFSET " + offset + ""; } else { offset = ""; }

                // building the query
                String localQuery = "SELECT " + columns
                        + " FROM " + table + clause
                        + group + order
                        + limit + offset + ";";

                System.out.println("Query: " + localQuery);

                this.setQuery(localQuery);
                ResultSet rs = this.loadData();

                // getting the row and column count to initialize the resultset array
                int numberOfColumns = rs.getMetaData().getColumnCount();
                this.setQuery("SELECT COUNT(*) FROM " + table + clause);
                ResultSet rs1 = this.loadData();
                rs1.next();
                resultset = new Map[rs1.getInt(1)];

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
            System.out.println("\n" + ERROR + error);
            System.exit(0);
        }
        return resultset;
    }

    private ResultSet loadData() throws Exception {
        Statement s = getDbConnection().createStatement();

        s.execute(this.query);
        ResultSet rs = s.getResultSet(); // get any ResultSet that came from the query

        return rs;
    }
}
