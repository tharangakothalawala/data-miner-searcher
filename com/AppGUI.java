
/**
 * @Author	Tharanga S Kothalawala <tharanga.kothalawala@my.westminster.ac.uk>
 * @StudentNo	w1278462
 */

package com;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import database.*;

public class AppGUI extends JPanel implements ActionListener, ItemListener {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 600;

    /*private JButton btnSearch, btnSetData, btnTestSearch, btnReset;
    private JLabel lblSearchResultInfo;
    private JTextField txtSearchKeyword;
    private JTextArea textarea;
    JScrollPane textareaScroll;//*/
    private Database db = new Database();
    private Search search = new Search();
    Query query = new Query(search.primaryKeyArray, search.foreignKeyArray);

    private AppTest apptest = new AppTest(); // for testing the app
    //private int categoryCount = 0;


    /*JTextField[] relatedTextfieldArray;
    JCheckBox[] categoryCheckboxes;
    JCheckBox[] relatedCheckboxArray;
    int selectedCategoryIndex = 0;

    private boolean isCategorySelected = false;//*/

    public AppGUI() {
        // starting the AppLogic here as I don't need the GUI right now!
        search.doSearch();

        // a simple test demonstration
        /*apptest.testJoinSearch();
        apptest.testSearch();
        System.exit(0);//*/

        /*btnSearch = new JButton("go cmd line");
        btnSetData = new JButton("Set Test Data");
        btnTestSearch = new JButton("Test Search");
        btnReset = new JButton("Reset");
        lblSearchResultInfo = new JLabel("...");
        txtSearchKeyword = new JTextField(5);
        textarea = new JTextArea(12, 65);
        textareaScroll = new JScrollPane(textarea);

        btnSearch.addActionListener(this);
        btnSetData.addActionListener(this);
        btnTestSearch.addActionListener(this);
        btnReset.addActionListener(this);

        btnSearch.setToolTipText("Search");
        txtSearchKeyword.setFont(new Font("Calibri", Font.BOLD, 14));
        txtSearchKeyword.setText("ad"); // only for the demo
        lblSearchResultInfo.setBackground(Color.lightGray);
        lblSearchResultInfo.setOpaque(true);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);

        add(btnSearch).setBounds(350, 15, 150, 20);
        //add(btnSetData).setBounds(630, 180, 120, 20);
        //add(btnTestSearch).setBounds(630, 200, 120, 20);
        //add(btnReset).setBounds(630, 10, 120, 20);
        add(lblSearchResultInfo).setBounds((WIDTH/2) - 100, 200, 200, 20);
        add(txtSearchKeyword).setBounds(15, 15, 330, 20);
        add(textareaScroll).setBounds(10, 230, 740, 360);//*/

        ////////////////////////////////////////////////////////////////////////
        /*categoryCount = search.showInitialView(true).length;
        categoryCheckboxes = new JCheckBox[categoryCount];
        String[] labels = search.showInitialView(true);

        int relatedEntityCount = 0;
        for (int c = 0; c < categoryCheckboxes.length; c++) {
            if (labels[c] != null) {
                String[] entityData = labels[c].split(db.COLNAMETYPESP);
                if (search.is_array(entityData)) {
                    String[] relatedEntities = entityData[1].split(",");

                    if (relatedEntities.length > 1) {
                        relatedEntityCount += relatedEntities.length;
                    } else {
                        relatedEntityCount++;
                    }
                }
            }
        }
        relatedCheckboxArray = new JCheckBox[relatedEntityCount];
        relatedTextfieldArray = new JTextField[relatedEntityCount];

        int relatedCategoryRenderCount = 0;
        for (int i = 0; i < categoryCheckboxes.length; i++) {
            if (labels[i] != null) {
                String[] entityData = labels[i].split(db.COLNAMETYPESP);
                String[] relatedEntities = new String[1];
                if (search.is_array(entityData)) {
                    relatedEntities = entityData[1].split(",");
                }

                // creating the checkboxes for related entities
                int startX = 60;
                if (relatedEntities.length > 1) {
                    for (int ch = 0, xpos = 25, subCategoryYpos = 120; ch < relatedEntities.length; ch++) {

                        relatedCheckboxArray[relatedCategoryRenderCount] = new JCheckBox(relatedEntities[ch]);
                        relatedCheckboxArray[relatedCategoryRenderCount].setToolTipText(relatedEntities[ch]);
                        relatedCheckboxArray[relatedCategoryRenderCount].addItemListener(this);
                        add(relatedCheckboxArray[relatedCategoryRenderCount]).setBounds(xpos, startX+(40*i), 80, 20);

                        relatedTextfieldArray[relatedCategoryRenderCount] = new JTextField(5);
                        add(relatedTextfieldArray[relatedCategoryRenderCount]).setBounds(subCategoryYpos, startX+(40*i), 100, 20);
                        //System.out.println("1.5("+ xpos+","+(20*i)+","+100+","+20+")");
                        xpos = xpos + 200;
                        subCategoryYpos = subCategoryYpos + 200;
                        relatedCategoryRenderCount++;
                    }
                } else {
                    if (search.is_array(entityData)) {
                        try {
                            relatedCheckboxArray[relatedCategoryRenderCount] = new JCheckBox(db.getEntity().getEntityMeta(entityData[1], 1));
                            relatedCheckboxArray[relatedCategoryRenderCount].setToolTipText(db.getEntity().getEntityMeta(entityData[1], 1));
                            relatedCheckboxArray[relatedCategoryRenderCount].addItemListener(this);
                            add(relatedCheckboxArray[relatedCategoryRenderCount]).setBounds(25, startX+(40*i), 80, 20);
                            

                            relatedTextfieldArray[relatedCategoryRenderCount] = new JTextField(5);
                            add(relatedTextfieldArray[relatedCategoryRenderCount]).setBounds(120, startX+(40*i), 100, 20);

                            relatedCategoryRenderCount++;
                        } catch (ArrayIndexOutOfBoundsException ex) {

                        }
                    }
                }

                // creating the main checkboxes
                categoryCheckboxes[i] = new JCheckBox(entityData[0]);
                categoryCheckboxes[i].setToolTipText(entityData[0]);
                categoryCheckboxes[i].addItemListener(this);
                add(categoryCheckboxes[i]).setBounds(10, 40+(40*i), 200, 20);
            }
        } // end of for loop //*/
    }

    public void itemStateChanged(ItemEvent ie) {
            /*String[] labels = search.showInitialView(true);
            if (!isCategorySelected) {
		for (int i = 0; i < categoryCheckboxes.length; i++) {
                    String[] entityData = labels[i].split(db.COLNAMETYPESP);
                    if (ie.getSource() != categoryCheckboxes[i] && !categoryCheckboxes[i].isSelected()) {
			categoryCheckboxes[i].setEnabled(false);
                    } else {
			isCategorySelected = true;
			selectedCategoryIndex = i;
			String clause = query.makeClause(db.getEntity().getEntityMeta(entityData[0], 3), txtSearchKeyword.getText());
			System.out.println(db.getEntity().getEntityMeta(entityData[0], 7) + "::" + clause);
			search.rawUserInputData[0] = db.getEntity().getEntityMeta(entityData[0], 7) + "::" + clause;
			search.selectedMainCategory = db.getEntity().getEntityMeta(entityData[0], 7);

			//query.buildQuery(search.rawUserInputData, true, true);
			//System.out.println(query.getSqlQuery());
                    }
		}
            } else {
		for (int i = 0; i < categoryCheckboxes.length; i++) {
                    if (ie.getSource() != categoryCheckboxes[i] && !categoryCheckboxes[i].isSelected()) {
                        categoryCheckboxes[i].setEnabled(true);
                        isCategorySelected = false;
                    } else {
                        isCategorySelected = false;
                    }
		}
            }

            for (int i = 0; i < relatedCheckboxArray.length; i++) {
		if (ie.getSource() == relatedCheckboxArray[i] && relatedCheckboxArray[i].isSelected()) {
                    String relatedSearchText = "";
                    if (!relatedTextfieldArray[i].getText().equalsIgnoreCase("")) {
                        relatedSearchText = relatedTextfieldArray[i].getText();
                    } else {
                        relatedSearchText = txtSearchKeyword.getText();
                    }
                    String clause = query.makeClause(db.getEntity().getEntityMeta(relatedCheckboxArray[i].getText(), 3), relatedSearchText);
                    System.out.println(relatedCheckboxArray[i].getText() + "::" + clause);
                    search.rawUserInputData[1] = db.getEntity().getEntityMeta(relatedCheckboxArray[i].getText(), 7) + "::" + clause;

                    //query.buildQuery(search.rawUserInputData, true, true);
                    //System.out.println(query.getSqlQuery());
		}
            }//*/
    }

    public void actionPerformed(ActionEvent event) {
        /*if (event.getSource() == btnSearch) {
            String searchKeyword = txtSearchKeyword.getText();

            // triggering the search method
            //Search searchTest = new Search();
            search.doSearch(searchKeyword);

            textarea.setText(search.searchResults);
        } else if (event.getSource() == btnSetData) {

            search.rawUserInputData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
            search.rawUserInputData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";
            search.selectedMainCategory = "4images_images";

            //textarea.setText(search.searchResults);
        } else if (event.getSource() == btnTestSearch) {
            String searchResults = "";
            String searchKeyword = txtSearchKeyword.getText();
            //search.buildQuery(search.rawUserInputData, true);
            //textarea.setText(search.searchResults);

            //String value = "ad";
            // traversing through all the available/defined seachable tables
                int entityCount = db.getEntity().getDefinedSearchableTables(0).length;
                String[] entities = db.getEntity().getDefinedSearchableTables(0);

                // counting the tables which have got a meta keyword
                int countOfEntitiesWithMetaKeyword = 0;
                for (int i = 0; i < entityCount; i++) {
                    String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);
                    // checking for the enity description for the meta keyword/s
                    if (eachEntityDescription.toLowerCase().contains("@\""+search.selectedMainCategory.toLowerCase()+"@\"")) {
                        countOfEntitiesWithMetaKeyword++;
                    }
                }

                //String userSelectionExtraSearch = "";
                boolean userSelectionExtraSearch = false;
                if (countOfEntitiesWithMetaKeyword > 0) {
                    if (JOptionPane.showConfirmDialog(null, "Click 'yes' to continue & consider the " + countOfEntitiesWithMetaKeyword + " extra related category/ies found, or Click 'no' to Skip: ") == 0) {
                        userSelectionExtraSearch = true;
                    }
                }
                if (countOfEntitiesWithMetaKeyword > 0 && userSelectionExtraSearch) {
                    for (int i = 0; i < entityCount; i++) {
                        String eachEntityDescription = db.getEntity().getEntityMeta(entities[i], 4);

                        // checking for the enity description for the meta keyword/s
                        if (eachEntityDescription.toLowerCase().contains("@\""+search.selectedMainCategory.toLowerCase()+"@\"")) {
                            //System.out.println("\n##2" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                            //String entityPreferance = search.promptMessage("\nConsider " + eachEntityDescription.replace("@\"", "") + "? (yes|no)");
                            boolean entityPreferance = false;
                            if (JOptionPane.showConfirmDialog(null, "Consider " + eachEntityDescription.replace("@\"", "") + "? (yes|no)") == 0) {
                                entityPreferance = true;
                            }
                            if (entityPreferance && !search.isTableSelected(search.irrelationalRawUserInputData, entities[i])) {
                                //String relatedEntityClause = db.getEntity().makeClause(db.getEntity().getEntityMeta(entities[i], 3), value);
                                //irrelationalRawUserInputData[this.nextAvailableArrayIndex(irrelationalRawUserInputData)] = entities[i] + "::" + relatedEntityClause;

                                //////////////////////////////// Each entity attribute description //////////////
                                String searchableAttributes = db.getEntity().getEntityMeta(entities[i], 5);
                                String[] searchableAttributeData = searchableAttributes.split(",");

                                //-----
                                int countOfAttributesWithMetaKeyword = 0;
                                for (int a = 0; a < searchableAttributeData.length; a++) {
                                    String[] attributeData = searchableAttributeData[a].split(":");
                                    try {
                                        if (attributeData[1].toLowerCase().contains("@\""+search.selectedMainCategory.toLowerCase()+"@\"")) {
                                            countOfAttributesWithMetaKeyword++;
                                        }
                                    } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description * }
                                }
                                //-----
                                if (countOfAttributesWithMetaKeyword > 1) {
                                    JOptionPane.showMessageDialog(null, "Found " + countOfAttributesWithMetaKeyword + " search criteria under this category.", "Information", JOptionPane.INFORMATION_MESSAGE);
                                    String searchables = "";
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        String[] attributeData = searchableAttributeData[a].split(":");
                                        try {
                                        if (attributeData[1].toLowerCase().contains("@\""+search.selectedMainCategory.toLowerCase()+"@\"")) {
                                            //System.out.println("\n##3" + eachEntityDescription + "\n" + entities[i] + db.getEntity().getEntityMeta(entities[i], 3));
                                            //String preferance = search.promptMessage("\nConsider " + attributeData[1].replace("@\"", "") + "? (yes|no)");
                                            boolean preferance = false;
                                            if (JOptionPane.showConfirmDialog(null, "Consider " + attributeData[1].replace("@\"", "") + "? (yes|no)") == 0) {
                                                preferance = true;
                                            }
                                            if (preferance) {
                                                searchables += attributeData[0] + ",";
                                            } else {
                                                searchableAttributeData[a] = null; // unset the value (this will avoid considering this attribute later)
                                            }
                                        }
                                        } catch (Exception ex) { /* caught ArrayIndexOutOfBoundsException for attributes which got no meta description * }
                                    }
                                    // going through the  attributes again to get any attribute which did't get considered above due to the user input
                                    for (int a = 0; a < searchableAttributeData.length; a++) {
                                        try {
                                            String[] attributeData = searchableAttributeData[a].split(":");
                                            if (!searchables.toLowerCase().contains(attributeData[0].toLowerCase())) {
                                                searchables += attributeData[0] + ",";
                                            }
                                        } catch (Exception ex) { }
                                    }
                                    ///////////////*
                                    if (!searchables.equalsIgnoreCase("")) {
                                        searchables = searchables.substring(0, (searchables.length()) - 1);
                                        if (!search.isTableSelected(search.irrelationalRawUserInputData, entities[i])) {
                                            String relatedEntityClause = query.makeClause(searchables, searchKeyword);
                                            search.irrelationalRawUserInputData[search.nextAvailableArrayIndex(search.irrelationalRawUserInputData)] = entities[i] + "::" + relatedEntityClause;
                                        }
                                    }
                                } else {
                                    if (!search.isTableSelected(search.irrelationalRawUserInputData, entities[i])) {
                                        String relatedEntityClause = query.makeClause(db.getEntity().getEntityMeta(entities[i], 3), searchKeyword);
                                        search.irrelationalRawUserInputData[search.nextAvailableArrayIndex(search.irrelationalRawUserInputData)] = entities[i] + "::" + relatedEntityClause;
                                    }
                                }
                                //////////////////////////////////////*
                            }
                        }
                    }
                }

            // getting the result count
            //String sqlCountQuery = query.buildQuery(search.rawUserInputData, true, true);
            //lblSearchResultInfo.setText(query.getCount(sqlCountQuery) + " results found");

            String sqlQuery = query.buildQuery(search.rawUserInputData, true);
            search.getRealData(sqlQuery, null);
            searchResults = search.searchResults;
            search.searchResults = "";
            //sqlQuery = search.buildQuery(search.irrelationalRawUserInputData);
            search.getRealData(null, search.irrelationalRawUserInputData);
            searchResults += search.searchResults;
            textarea.setText(searchResults);
            search.searchResults = "";
        } else if (event.getSource() == btnReset) {
            search.rawUserInputData = search.initializeArray(search.rawUserInputData);
            search.irrelationalRawUserInputData = search.initializeArray(search.irrelationalRawUserInputData);
            search.searchResults = "";
            textarea.setText("");
        }//*/
    }

    public static void showSearchGUI() {
        // positioning the frame to the center of the screen
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        int xSize = ((int) toolKit.getScreenSize().getWidth() - WIDTH) / 2;
        int ySize = ((int) toolKit.getScreenSize().getHeight() - HEIGHT) / 2;

        JFrame frame = new JFrame("Communigram Search - Tharanga Kothalawala");
        // just giving the GUI a windows look, except the default java look
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ("Failed loading Look and Feel:\n " + ex), "Information", JOptionPane.PLAIN_MESSAGE);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // program process terminate when closes the program
        frame.getContentPane().add(new AppGUI());
        frame.setLocation(xSize, ySize); // initially visible location
        //     frame.setResizable(false); // cannot maximize
        frame.pack();
        frame.setVisible(true); // set visible at the biginning
    }
}
