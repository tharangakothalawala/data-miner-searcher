
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package database;

import java.sql.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Entity {

    Database db = new Database();

    /*
     * @param (String)      table           : The table which it going to search
     * @param (boolean)     appendTableName : true, if we need in the form of <table>.<attribute>, <table>.<attribute> ...
     * @return (String)     attributes      : returns a set of possible searchable attributes to a given table
     */
    public String getSearchables(String table, boolean appendTableName) {
        String attributes = "";
        boolean getPredefined = false;

        try {
            db.setQuery("SELECT * FROM " + table);
            ResultSet rs = db.loadData();

            String[] arr = {""};

            if (this.getEntityMeta(table, 6, true).equalsIgnoreCase("null")) {
                arr = db.getMetaData(rs, 2); // get entity coloumns with their datatypes
            } else {
                arr = this.getEntityMeta(table, 6, true).split(","); // get the defined searchable attributes
                getPredefined = true;
            }

            for (int i = 0; i < arr.length; i++) {
                if (!getPredefined) {
                    String[] splits = arr[i].split(db.COLNAMETYPESP);
                    if (this.isSearchable(splits[1])) { // column type
                        arr[i] = splits[0]; // column name
                        if (appendTableName) {
                            attributes += table + "." + arr[i] + ", ";
                        } else {
                            attributes += arr[i] + ", ";
                        }
                    }
                } else {
                    if (appendTableName) {
                        attributes += table + "." + arr[i] + ", ";
                    } else {
                        attributes += arr[i] + ", ";
                    }
                }
            }

            if (!attributes.equalsIgnoreCase("")) {
                attributes = attributes.substring(0, (attributes.length()) - 2); // trims the extra comma at the end: ", "
            }
        } catch (Exception error) {
            System.out.println("\n" + error);
            error.printStackTrace();
            System.exit(0);
        }

        return attributes;
    }

    public boolean isSearchable(String datatype) {
        String[] searchableTypes = this.db.searchable_data_types.split(",");
        boolean isSearchable = false;

        for (int i = 0; i < searchableTypes.length; i++) {
            if (searchableTypes[i].equalsIgnoreCase(datatype)) {
                isSearchable = true;
            }
        }
        return isSearchable;
    }

    /*
     * @param (int)	configValueIndex	: get only the values at the specified index from the xml xonfig array
     * @return (String)	searchableTableData	: returns the array of values at index, <configValueIndex>
     */
    public String[] getEntityConfigValuesAtIndex(int configValueIndex) {
        String[] definedTableData = this.loadEntityConfig(true);
        String[] searchableTableData = new String[definedTableData.length];
        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP + db.COLNAMETYPESP);
            searchableTableData[i] = tableData[configValueIndex];
        }

        return searchableTableData;
    }

    /*
     * @param (boolean)	isInit			: load data from the XML according to the formal way.
						  If false, it will load restricted data even they are hidden
     * @return (String)	searchableTableData	: returns the full db entity configuration information as a form of an array
     */
    public String[] loadEntityConfig(boolean isInit) {
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
                    if (table_name.equalsIgnoreCase("")) {
                        table_name = null;
                    }
                    String displayName = element.getElementsByTagName("display_name").item(0).getTextContent();
                    if (displayName.equalsIgnoreCase("")) {
                        displayName = null;
                    }
                    String implicit_annotation = element.getElementsByTagName("implicit_annotation").item(0).getTextContent();
                    if (implicit_annotation.equalsIgnoreCase("")) {
                        implicit_annotation = null;
                    }
                    String aliases = element.getElementsByTagName("aliases").item(0).getTextContent();
                    if (aliases.equalsIgnoreCase("")) {
                        aliases = null;
                    }
                    String related_tables = element.getElementsByTagName("related_tables").item(0).getTextContent();
                    if (related_tables.equalsIgnoreCase("")) {
                        related_tables = null;
                    }
                    String searchable_attributes = element.getElementsByTagName("searchable_attributes").item(0).getTextContent();
                    if (searchable_attributes.equalsIgnoreCase("") || (element.getElementsByTagName("searchable_attributes").item(0).getAttributes().getNamedItem("force").getNodeValue().equalsIgnoreCase("0") && isInit)) {
                        searchable_attributes = null;
                    }

                    searchableTables[i] =
                            table_name + "::"
                            + displayName + "::"
                            + implicit_annotation + "::"
                            + aliases + "::"
                            + related_tables + "::"
                            + searchable_attributes;
                }
            }
            return searchableTables;
        } catch (Exception e) {
            System.out.println("XML Parser Error: Please check the DB Entity config file, 'config/databases/" + db.dbname + "_entity_config.xml'\n\n" + e);
            e.printStackTrace();
        }
        return null;
    }

    public String getEntityMeta(String table, int key, boolean isInit) {
        String[] definedTableData = this.loadEntityConfig(isInit);

        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP + db.COLNAMETYPESP);
            String tableName = tableData[0];
            String displayName = tableData[1]; // what this table about
            String implicitAnnotation = tableData[2]; // meta description
            String aliases = tableData[3];
            String relatedTables = tableData[4];
            String searchableAttributes = tableData[5];

            //System.out.println (eachTable +"|"+ searchableAttributes +"|"+ eachTableDescription);
            if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 1) {
                return tableName;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 2) {
                return displayName;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 3) {
                return implicitAnnotation;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 4) {
                return aliases;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 5) {
                return relatedTables;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 6) {
                // sending only the attributes names in the form of <attribute1>,<attribute2>,<attribute3>,...
                String finalAttributeList = "";
                String[] searchableAttributeData = searchableAttributes.split(",");
                for (int a = 0; a < searchableAttributeData.length; a++) {
                    String[] attributeData = searchableAttributeData[a].split(":");
                    finalAttributeList += attributeData[0] + ",";
                }
                finalAttributeList = finalAttributeList.substring(0, finalAttributeList.length() - 1);
                return finalAttributeList;
            } else if ((tableName.equalsIgnoreCase(table) || displayName.equalsIgnoreCase(table)) && key == 7) {
                // same as the key# 6, but with their descriptions
                return searchableAttributes;
            }
        }
        return null;
    }

    public int getSugesstableEntityCount() {
        int sugesstableEntityCount = 0;
        for (int i = 0; i < this.getEntityConfigValuesAtIndex(0).length; i++) {
            if (this.getEntityMeta(this.getEntityConfigValuesAtIndex(0)[i], 8, true).equalsIgnoreCase("1")) {
                sugesstableEntityCount++;
            }
        }
        return sugesstableEntityCount;
    }
}
