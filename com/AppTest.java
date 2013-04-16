
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package com;

import database.*;
import java.net.*;

public class AppTest {

    private Search search = new Search();
    private Query query = new Query(search.primaryKeyArray, search.foreignKeyArray);

    public void testSearch() {
	String[] rawUserInputData = new String[2];

	// This is a single query statment without JOINs
	rawUserInputData[0] = search.getQueryRawData("Images", "image_name,image_description,image_keywords", "Texas");

	search.getRealData(null, rawUserInputData);
    }

    public void testJoinSearch() {
	String[] rawUserInputData = new String[2];

	/*
	 * example QueryRawData for the two tables, "4images_users" with "4images_images" (a JOIN)
	 * rawUserInputData[0] = "4images_images::a::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
	 * rawUserInputData[1] = "4images_users::user_name,user_email,user_location::";
	 */
	rawUserInputData[0] = search.getQueryRawData("Images", "a", "Texas");
	rawUserInputData[1] = search.getQueryRawData("Users", "user_name,user_email,user_location", "");

	String sqlQuery = query.buildQuery(rawUserInputData, false);
	search.getRealData(sqlQuery, null);//*/








	// *********************************************************************
	// The following is just a demo to show how we can integrate this API as a web service to provide search facility
	/*try {
            URL url1 = new URL("http://localhost:80/fproject/?collection=Images&attributes=a&q=Texas");
            URL url2 = new URL("http://localhost:80/fproject/?collection=Users&attributes=user_name,user_email,user_location&q=Texas");

            String[] query1 = url1.getQuery().split("&");
            String[] query2 = url2.getQuery().split("&");

            rawUserInputData[0] = search.getQueryRawData(query1[0].split("=")[1], query1[1].split("=")[1], query1[2].split("=")[1]);
            rawUserInputData[1] = search.getQueryRawData(query2[0].split("=")[1], query2[1].split("=")[1], query2[2].split("=")[1]);

            String sqlQuery = query.buildQuery(rawUserInputData, false);
            search.getRealData(sqlQuery, null);

	} catch (Exception ex) { }//*/
    }
}
