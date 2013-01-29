package com;

/**
 *
 * @author Tharanga
 * @description This is the gui which I developed for my second year cw1. This class will be removed in future!
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import database.*;

public class AppGUI extends JPanel implements ActionListener {

    private JTextField txtSearchKeyword;
    private JButton btnSearch;
    private JCheckBox chkSearchAll;
    private JCheckBox chkSearchUsers;
    private JCheckBox jcomp5;
    private JCheckBox chkSearchResources;
    private JCheckBox jcomp7;
    private JTextArea textarea;
    JScrollPane textareaScroll;//*/
    private Database db = new Database();
    Search searchTest = new Search();

    public AppGUI() {
        txtSearchKeyword = new JTextField (5);
        btnSearch = new JButton ("Search");
        chkSearchAll = new JCheckBox ("All");
        chkSearchUsers = new JCheckBox ("Users");
        jcomp5 = new JCheckBox ("with name");
        chkSearchResources = new JCheckBox ("Resources");
        jcomp7 = new JCheckBox ("with name");
        textarea = new JTextArea(10, 65);
        textareaScroll = new JScrollPane(textarea);

        btnSearch.addActionListener(this);

        jcomp5.setEnabled(false);
        jcomp7.setEnabled(false);
        btnSearch.setToolTipText("Search");
        txtSearchKeyword.setFont(new Font("Calibri", Font.BOLD, 14));
        txtSearchKeyword.setText("ad"); // only for the demo

        setPreferredSize(new Dimension(360, 400));
        setLayout(null);

        add(txtSearchKeyword).setBounds (15, 15, 220, 20);
        add(btnSearch).setBounds (250, 15, 100, 20);
        add(chkSearchAll).setBounds (15, 50, 100, 25);
        add(chkSearchUsers).setBounds (15, 75, 100, 25);
        add(jcomp5).setBounds (30, 95, 100, 25);
        add(chkSearchResources).setBounds (165, 50, 100, 25);
        add(jcomp7).setBounds (180, 70, 100, 25);
        add(textareaScroll).setBounds(15, 110, 330, 260);

        searchTest.populateGraph();

    }

    public void actionPerformed(ActionEvent event) {
        String[] entities = new String[2];
        String[] entityFields = new String[2];

        /* @TODO : need to handle this somewhere else then here in the view.
         * But this may be the place as we will need to anyhow know what categories are being considered
         */
        if (chkSearchUsers.isSelected()) {
            entities[0] = "USERS";
            entityFields[0] = "name";
        }
        if (chkSearchResources.isSelected()) {
            entities[1] = "RSRC";
            entityFields[1] = "rsrc_name";
        }
        if (event.getSource() == btnSearch) {
            String searchKeyword = txtSearchKeyword.getText();

            if (!searchKeyword.equalsIgnoreCase("")) {
                searchTest.doGraphSearch(entities, entityFields, searchKeyword);

                textarea.setText(searchTest.searchResults);
            }
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
