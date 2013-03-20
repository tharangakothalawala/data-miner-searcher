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

    private JButton btnSearch;
    private JTextField txtSearchKeyword;
    private JTextArea textarea;
    JScrollPane textareaScroll;//*/
    private Database db = new Database();
    private Search search = new Search();
    private int categoryCount = 0;

    public AppGUI() {
        btnSearch = new JButton("Search");
        txtSearchKeyword = new JTextField(5);
            textarea = new JTextArea(10, 65);
            textareaScroll = new JScrollPane(textarea);

        btnSearch.addActionListener(this);

        btnSearch.setToolTipText("Search");
        txtSearchKeyword.setFont(new Font("Calibri", Font.BOLD, 14));
        txtSearchKeyword.setText("ad"); // only for the demo

        setPreferredSize(new Dimension(760, 400));
        setLayout(null);

        add(btnSearch).setBounds(255, 40, 90, 20);
        add(txtSearchKeyword).setBounds(15, 15, 330, 20);
            add(textareaScroll).setBounds(15, 70, 330, 300);

        ////////////////////////////////////////////////////////////////////////
        categoryCount = search.showInitialView(true).length;
        JCheckBox[] categoryCheckboxes = new JCheckBox[categoryCount];
        String[] labels = search.showInitialView(true);

        for (int i = 0; i < categoryCheckboxes.length; i++) {
            if (labels[i] != null) {
                String[] entityData = labels[i].split(db.COLNAMETYPESP);
                String[] relatedEntities = entityData[1].split(",");

                // creating the checkboxes for related entities
                if (relatedEntities.length > 1) {
                    //System.out.println("11"+entityData[1]+":"+relatedEntities[0]+":"+relatedEntities.length);
                    JCheckBox[] checkbox = new JCheckBox[relatedEntities.length];
                    for (int ch = 0, xpos = 460; ch < relatedEntities.length; ch++) {
                        checkbox[ch] = new JCheckBox(relatedEntities[ch]);
                        add(checkbox[ch]).setBounds(xpos, (20*i), 100, 20);
                        //System.out.println("1.5("+ xpos+","+(20*i)+","+100+","+20+")");
                        xpos = xpos + 100;
                    }
                } else {
                    //System.out.println("22"+entityData[1]);
                    add(new JCheckBox(entityData[1])).setBounds(460, (20*i), 100, 20);
                }

                // creating the main checkboxes
                categoryCheckboxes[i] = new JCheckBox(entityData[0]);
                categoryCheckboxes[i].addItemListener(this);
                add(categoryCheckboxes[i]).setBounds(360, (20*i), 100, 20);
            }
        } // end of for loop
    }

    public void itemStateChanged(ItemEvent ie) {
        System.out.println(ie.getItem().toString());
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnSearch) {
            String searchKeyword = txtSearchKeyword.getText();

            // triggering the search method
            //Search searchTest = new Search();
            search.doSearch(searchKeyword);

            textarea.setText(search.searchResults);
        }
    }

    public static void showSearchGUI() {
        // positioning the frame to the center of the screen
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        int xSize = ((int) toolKit.getScreenSize().getWidth() - 360) / 2;
        int ySize = ((int) toolKit.getScreenSize().getHeight() - 390) / 2;

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
