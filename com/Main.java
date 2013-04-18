
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package com;

public class Main {

    private static Search search = new Search();
    private static AppTest apptest = new AppTest(); // for testing the app

    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
	// Unit Tests to demonstrate the application functionlity
	apptest.testSearch(); // a normal search
	apptest.testJoinSearch(); // joined search
	apptest.testFacetedSearch(); // faceted search
	apptest.testWithURLFormat(); // just a demo to show how we can improve this application to run as a web service
	System.exit(0);//*/

	// Starting the Application
	search.doSearch();
    }
}
