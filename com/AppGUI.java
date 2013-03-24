package com;

/**
 *
 * @author Tharanga
 * @description This is the gui which I developed for my second year cw1. This class will be removed in future!
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import java.util.*;
import database.*;

public class AppGUI extends JPanel implements ActionListener, ItemListener {

    private static final int WIDTH = 760;
    private static final int HEIGHT = 600;

    private JButton btnSearch, btnSetData, btnTestSearch, btnReset;
    private JTextField txtSearchKeyword;
    private JTextArea textarea;
    JScrollPane textareaScroll;//*/
    private Database db = new Database();
    private Search search = new Search();
    private int categoryCount = 0;


    //JCheckBox[] relatedCheckboxes;
    JTextField[] relatedTextfieldArray;
    JCheckBox[] categoryCheckboxes;
    //JCheckBox relatedCheckbox;
    JCheckBox[] relatedCheckboxArray;
    int selectedCategoryIndex = 0;

    private boolean isCategorySelected = false;
    private int categorySelected = 0;

    public AppGUI() {
        btnSearch = new JButton("Search");
        btnSetData = new JButton("Set Test Data");
        btnTestSearch = new JButton("Test Search");
        btnReset = new JButton("Reset");
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

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);

        add(btnSearch).setBounds(350, 15, 90, 20);
        add(btnSetData).setBounds(630, 180, 120, 20);
        add(btnTestSearch).setBounds(630, 200, 120, 20);
        add(btnReset).setBounds(630, 10, 120, 20);
        add(txtSearchKeyword).setBounds(15, 15, 330, 20);
        add(textareaScroll).setBounds(10, 230, 740, 360);

        ////////////////////////////////////////////////////////////////////////
        categoryCount = search.showInitialView(true).length;
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
        //int relatedCategoryTextboxRenderCount = 0;
        for (int i = 0; i < categoryCheckboxes.length; i++) {
            if (labels[i] != null) {
                String[] entityData = labels[i].split(db.COLNAMETYPESP);
                String[] relatedEntities = new String[1];
                if (search.is_array(entityData)) {
                    relatedEntities = entityData[1].split(",");
                }/* else {
                    String[] relatedEntities = new String[1];
                }//*/

                // creating the checkboxes for related entities
                int startX = 60;
                if (relatedEntities.length > 1) {
                    //relatedEntities = entityData[1].split(",");
                    //System.out.println("11"+entityData[1]+":"+relatedEntities[0]+":"+relatedEntities.length);
                    //relatedCheckboxes = new JCheckBox[relatedEntities.length];
                    //relatedTextfieldArray = new JTextField[relatedEntities.length];
                    for (int ch = 0, xpos = 25, subCategoryYpos = 120; ch < relatedEntities.length; ch++) {
                        /*relatedCheckboxes[ch] = new JCheckBox(relatedEntities[ch]);
                        relatedCheckboxes[ch].setToolTipText(relatedEntities[ch]);
                        add(relatedCheckboxes[ch]).setBounds(xpos, startX+(40*i), 80, 20);//*/

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
                    //System.out.println("22"+entityData[1]);
                    /*relatedCheckbox = new JCheckBox(entityData[1]);
                    relatedCheckbox.setToolTipText(entityData[1]);
                    add(relatedCheckbox).setBounds(25, startX+(40*i), 80, 20);//*/

                    if (search.is_array(entityData)) {
                        try {
                            relatedCheckboxArray[relatedCategoryRenderCount] = new JCheckBox(db.getEntity().getEntityMeta(entityData[1], 1));
                            relatedCheckboxArray[relatedCategoryRenderCount].setToolTipText(db.getEntity().getEntityMeta(entityData[1], 1));
                            relatedCheckboxArray[relatedCategoryRenderCount].addItemListener(this);
                            add(relatedCheckboxArray[relatedCategoryRenderCount]).setBounds(25, startX+(40*i), 80, 20);
                            

                            relatedTextfieldArray[relatedCategoryRenderCount] = new JTextField(5);
                            add(relatedTextfieldArray[relatedCategoryRenderCount]).setBounds(120, startX+(40*i), 100, 20);

                            relatedCategoryRenderCount++;
                            //relatedCategoryTextboxRenderCount++;
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
        } // end of for loop
    }

    public void itemStateChanged(ItemEvent ie) {
        //try {
            String[] labels = search.showInitialView(true);
            //String[] relatedEntities = entityData[1].split(",");
            //if (relatedEntities.length > 1)
            if (!isCategorySelected) {
                for (int i = 0; i < categoryCheckboxes.length; i++) {
                    String[] entityData = labels[i].split(db.COLNAMETYPESP);
                    if (ie.getSource() != categoryCheckboxes[i] && !categoryCheckboxes[i].isSelected()) {
                        categoryCheckboxes[i].setEnabled(false);
                    } else {
                        isCategorySelected = true;
                        selectedCategoryIndex = i;
                        String clause = db.getEntity().makeClause(db.getEntity().getEntityMeta(entityData[0], 3), txtSearchKeyword.getText());
                        System.out.println(db.getEntity().getEntityMeta(entityData[0], 7) + "::" + clause);
                        search.eachSelectedTableClauseData[0] = db.getEntity().getEntityMeta(entityData[0], 7) + "::" + clause;
                    }
                }
            } else {
                for (int i = 0; i < categoryCheckboxes.length; i++) {
                    if (ie.getSource() != categoryCheckboxes[i] && !categoryCheckboxes[i].isSelected()) {
                        categoryCheckboxes[i].setEnabled(true);
                        isCategorySelected = false;
                    } else {
                        isCategorySelected = false;
                        //categorySelected = 0;
                    }
                }
            }

            //if (JOptionPane.showConfirmDialog(null, "Are you sure you need to logout without uploading the new data to the remote server!") == 0) {
            //}

            for (int i = 0; i < relatedCheckboxArray.length; i++) {
                if (ie.getSource() == relatedCheckboxArray[i] && relatedCheckboxArray[i].isSelected()) {
                    String relatedSearchText = "";
                    if (!relatedTextfieldArray[i].getText().equalsIgnoreCase("")) {
                        relatedSearchText = relatedTextfieldArray[i].getText();
                    } else {
                        relatedSearchText = txtSearchKeyword.getText();
                    }
                    String clause = db.getEntity().makeClause(db.getEntity().getEntityMeta(relatedCheckboxArray[i].getText(), 3), relatedSearchText);
                    System.out.println(relatedCheckboxArray[i].getText() + "::" + clause);
                    search.eachSelectedTableClauseData[1] = db.getEntity().getEntityMeta(relatedCheckboxArray[i].getText(), 7) + "::" + clause;
                }
            }

            /*if (categorySelected == 1) {
                //isRelatedEntitySelected = true;
                JCheckBox chk = (JCheckBox) ie.getSource();
                String clause = db.getEntity().makeClause(db.getEntity().getEntityMeta(chk.getText(), 3), txtSearchKeyword.getText());
                //String[] entityData = labels[selectedCategoryIndex].split(db.COLNAMETYPESP);
                System.out.println(chk.getText() + "::" + clause);
                search.eachSelectedTableClauseData[1] = db.getEntity().getEntityMeta(chk.getText(), 7) + "::" + clause;
            }//*/

            //categorySelected = 1;
        //} catch (Exception ex) {
        //    System.out.println("Error: (GUI) - Category Selection. Exception: " + ex);
        //}
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnSearch) {
            String searchKeyword = txtSearchKeyword.getText();

            // triggering the search method
            //Search searchTest = new Search();
            search.doSearch(searchKeyword);

            textarea.setText(search.searchResults);
        } else if (event.getSource() == btnSetData) {
            String searchKeyword = txtSearchKeyword.getText();

            search.eachSelectedTableClauseData[0] = "4images_images::image_name LIKE '%Texas%' OR image_description LIKE '%Texas%' OR image_keywords LIKE '%Texas%'";
            search.eachSelectedTableClauseData[1] = "4images_users::user_name LIKE '%sales@milezone.com%' OR user_email LIKE '%sales@milezone.com%'";

            //textarea.setText(search.searchResults);
        } else if (event.getSource() == btnTestSearch) {
            search.buildQuery(search.eachSelectedTableClauseData, true);
            textarea.setText(search.searchResults);
            search.searchResults = "";
        } else if (event.getSource() == btnReset) {
            search.initializeArray(search.eachSelectedTableClauseData);
        }
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
