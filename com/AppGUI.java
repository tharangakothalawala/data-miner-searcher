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

    private JButton btnSearch;
    private JLabel lblUsers, lblRsrc;
    private JLabel lblUsersName, lblRsrcName;
    private JTextField txtSearchKeyword;
    private JTextField txtUsersName; // USERS::name
    private JTextField txtRsrcName; // RSRC::name
    private JTextArea textarea;
    JScrollPane textareaScroll;//*/
    private Database db = new Database();
    Search searchTest = new Search();

    public AppGUI() {
        lblUsers = new JLabel("Users");
        lblRsrc = new JLabel("Resources");
        lblUsersName = new JLabel("name :");
        lblRsrcName = new JLabel("name :");

        btnSearch = new JButton("Search");
        txtSearchKeyword = new JTextField(5);
        txtUsersName = new JTextField(5);
        txtRsrcName = new JTextField(5);
        textarea = new JTextArea(10, 65);
        textareaScroll = new JScrollPane(textarea);

        btnSearch.addActionListener(this);

        btnSearch.setToolTipText("Search");
        txtSearchKeyword.setFont(new Font("Calibri", Font.BOLD, 14));
        txtSearchKeyword.setText("ad"); // only for the demo
        txtUsersName.setText("tha"); // only for the demo

        setPreferredSize(new Dimension(360, 400));
        setLayout(null);

        add(btnSearch).setBounds(255, 80, 90, 20);
        //add(txtSearchKeyword).setBounds(15, 15, 330, 20);
        add(lblUsers).setBounds(15, 15, 100, 20);
        add(lblRsrc).setBounds(165, 15, 100, 20);
        add(lblUsersName).setBounds(15, 40, 100, 20);
        add(lblRsrcName).setBounds(165, 40, 100, 20);

        add(txtUsersName).setBounds(50, 40, 100, 20);
        add(txtRsrcName).setBounds(200, 40, 100, 20);
        add(textareaScroll).setBounds(15, 110, 330, 260);

        searchTest.populateGraph();

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnSearch) {
            //textarea.setText("");
            String searchKeyword = txtSearchKeyword.getText();

            String entity = "";
            String entityField = "";
            if (!txtUsersName.getText().equalsIgnoreCase("")) {
                entity = "USERS";
                entityField = "name";
                searchKeyword = txtUsersName.getText();
            } else {
                if (!txtRsrcName.getText().equalsIgnoreCase("")) {
                    entity = "RSRC";
                    entityField = "rsrc_name";
                    searchKeyword = txtRsrcName.getText();
                }
            }

            // triggering the search method
            //Search searchTest = new Search();
            //searchTest.doSearch(searchKeyword);
            //searchTest.populateGraph();
            searchTest.doGraphSearch(entity, entityField, searchKeyword);

            textarea.setText(searchTest.searchResults);
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
