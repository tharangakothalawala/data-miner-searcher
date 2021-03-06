
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 * @Purpose	To launch the Search Application.
 */

package com;

public class Main {

    private static Search search = new Search();

    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
        /*
         * Unit Tests to demonstrate the application functionality
         * Proper Unit Testing has been carried out. Look inside the Test Packages
         */
	/*AppTest apptest = new AppTest(); // for testing the app
        apptest.testSearch(); // a normal search
	apptest.testJoinSearch(); // joined search
	apptest.testFacetedSearch(); // faceted search
	apptest.testWithURLFormat(); // just a demo to show how we can improve this application to run as a web service
	System.exit(0);//*/

	// Starting the Application
	search.doSearch();
    }
}
