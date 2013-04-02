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

    public String[] getSearchableTables (int configValueIndex) {
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
                File entityConfigFile = new File("src/database/plugins/" + db.dbname + "_entity_config.xml");
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

                                String description = element.getElementsByTagName("description").item(0).getTextContent();
                                if (description.equalsIgnoreCase(""))
                                    description = null;
                                String related_entities = element.getElementsByTagName("related_entities").item(0).getTextContent();
                                if (related_entities.equalsIgnoreCase(""))
                                    related_entities = null;
                                String isInSuggessionList = element.getElementsByTagName("isInSuggessionList").item(0).getTextContent();
                                if (isInSuggessionList.equalsIgnoreCase(""))
                                    isInSuggessionList = null;

                                searchableTables[i] =
                                        element.getElementsByTagName("real_name").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("display_name").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("is_a_join").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("searchable_attributes").item(0).getTextContent() +"::"+
                                        description +"::"+
                                        related_entities + "::"+
                                        isInSuggessionList;
                        }
                }
                return searchableTables;
            } catch (Exception e) {
                System.out.println ("Error: " + e);
                e.printStackTrace();
            }
        return null;
    }

    public String getEntityMeta (String table, int key) {
        String[] definedTableData = this.loadEntityConfig();

        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            String eachEntity = tableData[0];
            String eachEntityName = tableData[1]; // readable/displayable entity name
            String isAJoin = tableData[2]; // a direct or a join candidate entity
            String searchableAttributes = tableData[3];
            String eachTableDescription = tableData[4];
            String relatedEntities = tableData[5];
            String isDisplayable = tableData[6];

            //System.out.println (eachTable +"|"+ searchableAttributes +"|"+ eachTableDescription);
            if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 1) {
                return eachEntityName;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 2) {
                return isAJoin;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 3) {
                // sending only the attributes names in the form of <attribute1>,<attribute2>,<attribute3>,...
                String finalAttributeList = "";
                String[] searchableAttributeData = searchableAttributes.split(",");
                for (int a = 0; a < searchableAttributeData.length; a++) {
                    String[] attributeData = searchableAttributeData[a].split(":");
                    finalAttributeList += attributeData[0] + ",";
                }
                finalAttributeList = finalAttributeList.substring(0, finalAttributeList.length()-1);
                return finalAttributeList;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 4) {
                return eachTableDescription;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 5) {
                // same as the key# 3, but with their descriptions
                return searchableAttributes;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 6) {
                return relatedEntities;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 7) { // returns the real_name
                return eachEntity;
            } else if ((eachEntity.equalsIgnoreCase(table) || eachEntityName.equalsIgnoreCase(table)) && key == 8) {
                return isDisplayable;
            }
        }
        return null;
    }

    public int getSugesstableEntityCount () {
        int sugesstableEntityCount = 0;
        for (int i = 0; i < this.getSearchableTables(0).length; i++) {
            if (this.getEntityMeta(this.getSearchableTables(0)[i], 8).equalsIgnoreCase("1")) {
                sugesstableEntityCount++;
            }
        }
        return sugesstableEntityCount;
    }
}