
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 * @Purpose	This Unit Test Class is to check & validate the generated SQL queries
 */

package com;

/*import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;//*/
import org.junit.Test;
import static org.junit.Assert.*;

import database.*;
import java.net.*;

public class SearchTest {

    private Search search;
    private Query query;

    public SearchTest() {
        search = new Search();
        query = new Query(search.primaryKeyArray, search.foreignKeyArray);
    }

    /*@BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }//*/

    /*
     * @Purpose : To test normal SQL statements. That is, SELECT queries without any joins.
     */
    @Test
    public void testSimpleSearch() {
	System.out.println("\n\n############################### *** Normal Search *** ");
	String[] rawUserInputData = new String[1];
        String tableName = "Images";
        String requiredDataFields = "image_name,image_description,image_keywords";
        String searchKeyword = "Texas";
        String expectedResult = "SELECT image_name,image_description,image_keywords FROM fproject_images WHERE fproject_images.image_name LIKE '%Texas%' OR  fproject_images.image_description LIKE '%Texas%' OR  fproject_images.image_keywords LIKE '%Texas%' OR  fproject_images.image_media_file LIKE '%Texas%' OR  fproject_images.image_thumb_file LIKE '%Texas%' OR  fproject_images.image_download_url LIKE '%Texas%';";

        rawUserInputData[0] = search.getQueryRawData(tableName, requiredDataFields, searchKeyword, false);

	// This is a single query statment without JOINs
	String sqlQuery = query.buildQuery(rawUserInputData, false);
        assertEquals(expectedResult, sqlQuery);
    }

    /*
     * @Purpose : To test the generation of SQL for a JOIN
     */
    @Test
    public void testJoinSearchOne() {
	System.out.println("\n\n############################### *** Joined Search 1 (Data from 2 tables) *** ");
	String[] rawUserInputData = new String[2];
	String rootTableName = "Images";
	String rootTableRequiredDataFields = "a"; // "a" means to fetch all available data attributes
	String rootTableSearchKeyword = "Texas";

	String joinTableName = "Users";
	String joinTableRequiredDataFields = "user_name,user_email,user_location"; // here the required data attributes are defined
	String joinTableSearchKeyword = ""; // no need a keyword for the join table as this is not Faceted Search
	String expectedResult = "SELECT image_name, image_description, image_keywords, image_media_file, image_thumb_file, image_download_url, user_name,user_email,user_location FROM fproject_images JOIN fproject_users ON fproject_users.user_id = fproject_images.user_id WHERE fproject_images.image_name LIKE '%Texas%' OR  fproject_images.image_description LIKE '%Texas%' OR  fproject_images.image_keywords LIKE '%Texas%' OR  fproject_images.image_media_file LIKE '%Texas%' OR  fproject_images.image_thumb_file LIKE '%Texas%' OR  fproject_images.image_download_url LIKE '%Texas%';";

	// Data for 2 tables
	rawUserInputData[0] = search.getQueryRawData(rootTableName, rootTableRequiredDataFields, rootTableSearchKeyword, false);
	rawUserInputData[1] = search.getQueryRawData(joinTableName, joinTableRequiredDataFields, joinTableSearchKeyword, false);

	// This is a join query statment with a JOIN
	String sqlQuery = query.buildQuery(rawUserInputData, false);
	assertEquals(expectedResult, sqlQuery);
    }

    /*
     * @Purpose : To test the generation of SQL for two JOINs
     */
    @Test
    public void testJoinSearchTwo() {
	System.out.println("\n\n############################### *** Joined Search 2 (Data from 3 tables) *** ");
	String[] rawUserInputData = new String[3];
	String rootTableName = "Comments";
	String rootTableRequiredDataFields = "comment_id,fproject_comments.user_name,comment_headline, comment_text";
	String rootTableSearchKeyword = "nice";

	String joinTableNameOne = "Images";
	String joinTableRequiredDataFieldsOne = "fproject_images.image_id,image_name";
	String joinTableSearchKeywordOne = "";

	String joinTableNameTwo = "Users";
	String joinTableRequiredDataFieldsTwo = "fproject_users.user_id,user_email";
	String joinTableSearchKeywordTwo = "";
	String expectedResult = "SELECT comment_id,fproject_comments.user_name,comment_headline, comment_text, fproject_images.image_id,image_name, fproject_users.user_id,user_email FROM fproject_comments JOIN fproject_images ON fproject_images.image_id = fproject_comments.image_id JOIN fproject_users ON fproject_users.user_id = fproject_comments.user_id WHERE fproject_comments.user_name LIKE '%nice%' OR  fproject_comments.comment_headline LIKE '%nice%' OR  fproject_comments.comment_text LIKE '%nice%' OR  fproject_comments.comment_ip LIKE '%nice%';";

	// Data for 3 tables
	rawUserInputData[0] = search.getQueryRawData(rootTableName, rootTableRequiredDataFields, rootTableSearchKeyword, false);
	rawUserInputData[1] = search.getQueryRawData(joinTableNameOne, joinTableRequiredDataFieldsOne, joinTableSearchKeywordOne, false);
	rawUserInputData[2] = search.getQueryRawData(joinTableNameTwo, joinTableRequiredDataFieldsTwo, joinTableSearchKeywordTwo, false);

	// This is a join query statment with two JOINs
	String sqlQuery = query.buildQuery(rawUserInputData, false);
	assertEquals(expectedResult, sqlQuery);
        System.out.println(sqlQuery);
    }

    /*
     * @Purpose : To test the generation of SQL JOIN queries, but with extra conditions. (Facets)
     */
    @Test
    public void testFacetedSearch () {
	System.out.println("\n\n############################### *** Faceted Search *** ");
	String[] rawUserInputData = new String[2];
	String rootTableName = "Images";
	String rootTableRequiredDataFields = "image_name,image_description";
	String rootTableSearchKeyword = "Texas";

	String joinTableName = "Users";
	String joinTableRequiredDataFields = "user_name,user_email"; // Two facets, "user_name" and "user_email"
	String joinTableSearchKeyword = "rb808";
	String expectedResult = "SELECT image_name,image_description, user_name,user_email FROM fproject_images JOIN fproject_users ON fproject_users.user_id = fproject_images.user_id AND (fproject_users.user_name LIKE '%rb808%' OR  fproject_users.user_password LIKE '%rb808%' OR  fproject_users.user_email LIKE '%rb808%' OR  fproject_users.user_activationkey LIKE '%rb808%' OR  fproject_users.user_location LIKE '%rb808%' OR  fproject_users.user_homepage LIKE '%rb808%' OR  fproject_users.user_icq LIKE '%rb808%') WHERE fproject_images.image_name LIKE '%Texas%' OR  fproject_images.image_description LIKE '%Texas%' OR  fproject_images.image_keywords LIKE '%Texas%' OR  fproject_images.image_media_file LIKE '%Texas%' OR  fproject_images.image_thumb_file LIKE '%Texas%' OR  fproject_images.image_download_url LIKE '%Texas%';";

	// the following is an example of a raw data for a SQL join query which contains two Facets, "user_name" and "user_email"
	rawUserInputData[0] = search.getQueryRawData(rootTableName, rootTableRequiredDataFields, rootTableSearchKeyword, false);
	rawUserInputData[1] = search.getQueryRawData(joinTableName, joinTableRequiredDataFields, joinTableSearchKeyword, false);

	// This is a join query statment with a JOIN
	// Second parameter sends a "true" value to create the SQL JOIN with Facets
	String sqlQuery = query.buildQuery(rawUserInputData, true);
	assertEquals(expectedResult, sqlQuery);
    }

    /*
     * @Purpose : Just a demo to show how we can modify this Application API as a web service to provide search facility. (To provide RESTFul ness)
     */
    @Test
    public void testWithURLFormat () {
	String url1Value = "http://localhost:80/fproject_test/?collection=Images&attributes=a&q=Texas";
	String url2Value = "http://localhost:80/fproject_test/?collection=Users&attributes=user_name,user_email,user_location&q=Texas";
	System.out.println("\n\n############################### *** URL Demo Joined Search ***\nURL 1: " + url1Value + "\nURL 2: " + url2Value + "\n");
	String[] rawUserInputData = new String[2];
	String expectedResult = "SELECT image_name, image_description, image_keywords, image_media_file, image_thumb_file, image_download_url, user_name,user_email,user_location FROM fproject_images JOIN fproject_users ON fproject_users.user_id = fproject_images.user_id WHERE fproject_images.image_name LIKE '%Texas%' OR  fproject_images.image_description LIKE '%Texas%' OR  fproject_images.image_keywords LIKE '%Texas%' OR  fproject_images.image_media_file LIKE '%Texas%' OR  fproject_images.image_thumb_file LIKE '%Texas%' OR  fproject_images.image_download_url LIKE '%Texas%';";

	try {
            URL url1 = new URL(url1Value);
            URL url2 = new URL(url2Value);

            String[] query1 = url1.getQuery().split("&");
            String[] query2 = url2.getQuery().split("&");

            // To create the raw data and to parse any data as well. E.g.: "Images" into real table name, "fproject_images"
            rawUserInputData[0] = search.getQueryRawData(query1[0].split("=")[1], query1[1].split("=")[1], query1[2].split("=")[1], false);
            rawUserInputData[1] = search.getQueryRawData(query2[0].split("=")[1], query2[1].split("=")[1], query2[2].split("=")[1], false);

            String sqlQuery = query.buildQuery(rawUserInputData, false);
            assertEquals(expectedResult, sqlQuery);

	} catch (Exception ex) { }
    }

}