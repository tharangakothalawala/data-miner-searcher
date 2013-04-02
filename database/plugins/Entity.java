package database.plugins;

/**
 *
 * @author Tharanga
 */
import database.Database;
import java.sql.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Entity {

    private boolean isEnabledSearchInAllAttributes = false;
    private boolean isEnabledSearchInAllTables = false;
    Database db = new Database();

    /*
     * @param (String)      table           : The table which it going to search
     * @param (boolean)     appendTableName : true, if we need in the form of <table>.<attribute>, <table>.<attribute> ...
     * @param (boolean)     getPredefined   : true, to get the predefined searchable table attributes
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
                    arr = this.getEntityMeta(table, 3).split(",");
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

    public String[] getDefinedSearchableTables (int configValueIndex) {
        //String[] searchableTables = {"PROFILE", "USERS", "RSRC", "PROJECT", "CALENDAR", "ROLES", "DOCUMENT", "TASK"};
        String[] definedTableData = this.loadEntityConfig();
        String[] searchableTables = new String[definedTableData.length];
        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            searchableTables[i] = tableData[configValueIndex];
        }
        
        if (this.isEnabledSearchInAllTables)
            return null;
        else
            return searchableTables;
    }

    public String[] loadEntityConfig () {
            try {
                File entityConfigFile = new File("config/databases/" + db.dbname + "_entity_config.xml");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document document = dBuilder.parse(entityConfigFile);

                document.getDocumentElement().normalize();

                NodeList nodeList = document.getElementsByTagName("table"); // each table
                String[] searchableTables = new String[nodeList.getLength()];

                for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);

                        if (node.getNodeType() == Node.ELEMENT_NODE) {

                                Element element = (Element) node;

                                String table_name = element.getElementsByTagName("table_name").item(0).getTextContent();
                                if (table_name.equalsIgnoreCase(""))
                                    table_name = null;
                                String key_concepts = element.getElementsByTagName("key_concepts").item(0).getTextContent();
                                if (key_concepts.equalsIgnoreCase(""))
                                    key_concepts = null;
                                String implicit_annotation = element.getElementsByTagName("implicit_annotation").item(0).getTextContent();
                                if (implicit_annotation.equalsIgnoreCase(""))
                                    implicit_annotation = null;
                                String aliases = element.getElementsByTagName("aliases").item(0).getTextContent();
                                if (aliases.equalsIgnoreCase(""))
                                    aliases = null;
                                String related_tables = element.getElementsByTagName("related_tables").item(0).getTextContent();
                                if (related_tables.equalsIgnoreCase(""))
                                    related_tables = null;
                                String searchable_attributes = element.getElementsByTagName("searchable_attributes").item(0).getTextContent();
                                if (searchable_attributes.equalsIgnoreCase(""))
                                    searchable_attributes = null;

                                searchableTables[i] =
                                        table_name +"::"+
                                        key_concepts +"::"+
                                        implicit_annotation +"::"+
                                        aliases +"::"+
                                        related_tables +"::"+
                                        searchable_attributes;
                        }
                }
                return searchableTables;
            } catch (Exception e) {
                System.out.println ("XML Parser Error: Please check the DB Entity config file, 'config/databases/" + db.dbname + "_entity_config.xml'\n\n" + e);
                e.printStackTrace();
            }
        return null;
    }

    public String getEntityMeta (String table, int key) {
        String[] definedTableData = this.loadEntityConfig();

        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            String tableName = tableData[0];
            String keyConcepts = tableData[1]; // what this table about
            String implicitAnnotation = tableData[2]; // meta description
            String aliases = tableData[3];
            String relatedTables = tableData[4];
            String searchableAttributes = tableData[5];

            //System.out.println (eachTable +"|"+ searchableAttributes +"|"+ eachTableDescription);
            if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 1) {
                return tableName;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 2) {
                return keyConcepts;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 3) {
                return implicitAnnotation;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 4) {
                return aliases;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 5) {
                return relatedTables;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 6) {
                // sending only the attributes names in the form of <attribute1>,<attribute2>,<attribute3>,...
                String finalAttributeList = "";
                String[] searchableAttributeData = searchableAttributes.split(",");
                for (int a = 0; a < searchableAttributeData.length; a++) {
                    String[] attributeData = searchableAttributeData[a].split(":");
                    finalAttributeList += attributeData[0] + ",";
                }
                finalAttributeList = finalAttributeList.substring(0, finalAttributeList.length()-1);
                return finalAttributeList;
            } else if ((tableName.equalsIgnoreCase(table) || keyConcepts.equalsIgnoreCase(table)) && key == 7) {
                // same as the key# 6, but with their descriptions
                return searchableAttributes;
            }
        }
        return null;
    }

    public int getSugesstableEntityCount () {
        int sugesstableEntityCount = 0;
        for (int i = 0; i < this.getDefinedSearchableTables(0).length; i++) {
            if (this.getEntityMeta(this.getDefinedSearchableTables(0)[i], 8).equalsIgnoreCase("1")) {
                sugesstableEntityCount++;
            }
        }
        return sugesstableEntityCount;
    }
}