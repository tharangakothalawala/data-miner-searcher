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
                    arr = db.getMetaData(rs, 3); // get entity coloumns with their datatypes
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
            try {
                File entityConfigFile = new File("src/database/plugins/entity_config.xml");
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

                                searchableTables[i] =
                                        element.getElementsByTagName("real_name").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("display_name").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("is_a_join").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("searchable_attributes").item(0).getTextContent() +"::"+
                                        element.getElementsByTagName("description").item(0).getTextContent();

                                /*System.out.println("real_name : " + element.getElementsByTagName("real_name").item(0).getTextContent());
                                System.out.println("display_name : " + element.getElementsByTagName("display_name").item(0).getTextContent());
                                System.out.println("is_a_join : " + element.getElementsByTagName("is_a_join").item(0).getTextContent());
                                System.out.println("searchable_attributes : " + element.getElementsByTagName("searchable_attributes").item(0).getTextContent());
                                System.out.println("description : " + element.getElementsByTagName("description").item(0).getTextContent());//*/
                        }
                }
                return searchableTables;
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* This is the manual, table config entry. This code will be removed in future */
            // @NOTE: don't specify the tables if they don't have searchable attributes
            /*String[] searchableTables = {
                "PROFILE::Profile::"+
                        "false::"+ // correct - false because, there is no attribute to search against
                        "prof_name::"+
                        "no description",
                "USERS::Users::"+
                        "true::"+ // correct
                        "name::"+
                        "This contains the users of the system.",
                "RSRC::Resources::"+
                        "true::"+ // correct
                        "rsrc_name,office_phone,email_addr::"+
                        "This contains all the resources or in other words the people or the @"users@" who is involved in several projects of the system. You can search for their email addresses as well.",
                "PROJECT::Project::"+
                        "false::"+ // correct - false because, there is no exact attribute to search on
                        "plan_start_date::"+
                        "no description",
                "CALENDAR::Calendar::"+
                        "false::"+ // correct
                        "clndr_data::"+
                        "no description",
                "ROLES::Roles::"+
                        "false::"+ // correct - false because, this is not directly invloved with anything
                        "name,short_name::"+
                        "no description",
                "DOCUMENT::Document::"+
                        "true::"+ // not sure yet but seems important
                        "name,short_name,author_name::"+
                        "This contains the documents created by @"users@" of the system.",
                "TASK::Task::"+
                        "true::"+
                        "name::"+
                        "This contains all the tasks which are defined in all projects. So you can search for task"
            };
            return searchableTables;
            //////////////////////////////////////////////////////////////////*/
        }
        return null;
    }

    public String getEntityMeta (String table, int key) {
        String[] definedTableData = this.getSearchableTables(2);

        for (int i = 0; i < definedTableData.length; i++) {
            String[] tableData = definedTableData[i].split(db.COLNAMETYPESP+db.COLNAMETYPESP);
            String eachEntity = tableData[0];
            String eachEntityName = tableData[1]; // readable/displayable entity name
            String isAJoin = tableData[2]; // a direct or a join candidate entity
            String searchableAttributes = tableData[3];
            String eachTableDescription = tableData[4];

            //System.out.println (eachTable +"|"+ searchableAttributes +"|"+ eachTableDescription);
            if (eachEntity.equalsIgnoreCase(table) && key == 1) {
                return eachEntityName;
            } else if (eachEntity.equalsIgnoreCase(table) && key == 2) {
                return isAJoin;
            } else if (eachEntity.equalsIgnoreCase(table) && key == 3) {
                // sending only the attributes names in the form of <attribute1>,<attribute2>,<attribute3>,...
                String finalAttributeList = "";
                String[] searchableAttributeData = searchableAttributes.split(",");
                for (int a = 0; a < searchableAttributeData.length; a++) {
                    String[] attributeData = searchableAttributeData[a].split(":");
                    finalAttributeList += attributeData[0] + ",";
                }
                finalAttributeList = finalAttributeList.substring(0, finalAttributeList.length()-1);
                return finalAttributeList;
            } else if (eachEntity.equalsIgnoreCase(table) && key == 4) {
                return eachTableDescription;
            } else if (eachEntity.equalsIgnoreCase(table) && key == 5) {
                // same as the key# 3, but with their descriptions
                return searchableAttributes;
            }
        }
        return null;
    }
}