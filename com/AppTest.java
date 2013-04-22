
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 * @Purpose	This Application Test is to check & demonstrate the available functionalities of this Search Application.
 */

package com;

import database.*;
import java.net.*;

public class AppTest {

    private Search search = new Search();
    private Query query = new Query(search.primaryKeyArray, search.foreignKeyArray);

    /*
     * This is to test normal SQL statements. That is, SELECT queries without any joins.
     */
    public void testSearch() {
	System.out.println("\n\n############################### *** Normal Search *** ");
	String[] rawUserInputData = new String[2];

	// This is a single query statment without JOINs
	rawUserInputData[0] = search.getQueryRawData("Images", "image_name,image_description,image_keywords", "Texas");

	search.displayRealData(null, rawUserInputData);
    }

    /*
     * This is to test the generation of SQL JOIN queries
     */
    public void testJoinSearch() {
	System.out.println("\n\n############################### *** Joined Search 1 (Data from 2 tables) *** ");
	String[] rawUserInputData = new String[3];

	/*example QueryRawData for the two tables, "fproject_users" with "fproject_images" (a JOIN)
	rawUserInputData[0] = "fproject_images::image_name::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
	rawUserInputData[1] = "fproject_users::user_name,user_email,user_location::";//*/

	// Data for 2 tables
	rawUserInputData[0] = search.getQueryRawData("Images", "a", "Texas"); // "a" means to fetch all available data attributes
	rawUserInputData[1] = search.getQueryRawData("Users", "user_name,user_email,user_location", ""); // here the required data attributes are defined

	// This is a join query statment with a JOIN
	String sqlQuery = query.buildQuery(rawUserInputData, false);
	search.displayRealData(sqlQuery, null);


	System.out.println("\n\n############################### *** Joined Search 2 (Data from 3 tables) *** ");
	// Data for 3 tables
	rawUserInputData[0] = search.getQueryRawData("Comments", "comment_id,fproject_comments.user_name,comment_headline, comment_text", "nice");
	rawUserInputData[1] = search.getQueryRawData("Images", "fproject_images.image_id,image_name", "");
        rawUserInputData[2] = search.getQueryRawData("Users", "fproject_users.user_id,user_email", "");

	// This is a join query statment with two JOINs
	sqlQuery = query.buildQuery(rawUserInputData, false);
	search.displayRealData(sqlQuery, null);
    }

    /*
     * This is also to test the generation of SQL JOIN queries, but with extra conditions. (Facets)
     */
    public void testFacetedSearch () {
	System.out.println("\n\n############################### *** Faceted Search *** ");
	String[] rawUserInputData = new String[3];

	// the following is an example of a raw data for a SQL join query which contains two Facets, "user_name" and "user_email"
	rawUserInputData[0] = "fproject_images::image_name,image_description::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%'";
	rawUserInputData[1] = "fproject_users::user_name,user_email::user_name LIKE '%rb808%' OR user_email LIKE '%rb808%'";

	// This is a join query statment with a JOIN
	// Second parameter sends a "true" value to create the SQL JOIN with Facets
	String sampleDemo = query.buildQuery (rawUserInputData, true);
	search.displayRealData(sampleDemo, null);
    }

    /*
     * This is just a demo to show how we can modify this Application API as a web service to provide search facility. (To provide RESTFul ness)
     */
    public void testWithURLFormat () {
	String url1Value = "http://localhost:80/fproject_test/?collection=Images&attributes=a&q=Texas";
	String url2Value = "http://localhost:80/fproject_test/?collection=Users&attributes=user_name,user_email,user_location&q=Texas";
	System.out.println("\n\n############################### *** URL Demo Joined Search ***\n" + url1Value + "\n" + url2Value + "\n");
	String[] rawUserInputData = new String[2];

	try {
            URL url1 = new URL(url1Value);
            URL url2 = new URL(url2Value);

            String[] query1 = url1.getQuery().split("&");
            String[] query2 = url2.getQuery().split("&");

            // To create the raw data and to parse any data as well. E.g.: "Images" into real table name, "fproject_images"
            rawUserInputData[0] = search.getQueryRawData(query1[0].split("=")[1], query1[1].split("=")[1], query1[2].split("=")[1]);
            rawUserInputData[1] = search.getQueryRawData(query2[0].split("=")[1], query2[1].split("=")[1], query2[2].split("=")[1]);

            String sqlQuery = query.buildQuery(rawUserInputData, false);
            search.displayRealData(sqlQuery, null);

	} catch (Exception ex) { }
    }
}
