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

public class SearchGUI extends JPanel implements ActionListener {

    private JButton btnLoadData;
    private JTextField txtTableName;
    private JTextArea textarea;
    JScrollPane textareaScroll;
    private Database db = new Database();

    public SearchGUI() {
        btnLoadData = new JButton("Load Data");
        txtTableName = new JTextField(5);
        textarea = new JTextArea(10, 65);
        textareaScroll = new JScrollPane(textarea);

        btnLoadData.addActionListener(this);

        btnLoadData.setToolTipText("Load Data");
        txtTableName.setFont(new Font("Calibri", Font.BOLD, 14));
        txtTableName.setText("CALENDAR");

        setPreferredSize(new Dimension(360, 390));
        setLayout(null);

        add(btnLoadData).setBounds(255, 40, 90, 20);
        add(txtTableName).setBounds(15, 15, 330, 20);
        add(textareaScroll).setBounds(15, 70, 330, 300);

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == btnLoadData) {
            String out = "";
            String table = txtTableName.getText();
            Map[] resultsets = db.sqlSelect(table, "*", null, null, null, null, null);
            // "clndr_id = '10' OR clndr_id = '20'"

            if (resultsets[0].get("ERROR") != null) {
                textarea.setText("ERROR:\n" + resultsets[0].get("ERROR"));
            } else {

                for (int i = 0; i < resultsets.length; i++) {
                    Map<String, String> resultset = resultsets[i];

                    //System.out.println("== ROW: "+ i +" ========================");
                    out += "\n== ROW: " + i + " ========================";
                    for (Map.Entry<String, String> entry : resultset.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        out += "\n ['" + key + "'] = " + value;
                        //System.out.println("['" + key + "'] = " + value);
                    }
                }
                textarea.setText(out);
                //System.out.println(out);
            }
        }
    }

    public void showSearchWindow() {
        // positioning the frame to the center of the screen
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        int xSize = ((int) toolKit.getScreenSize().getWidth() - 360) / 2;
        int ySize = ((int) toolKit.getScreenSize().getHeight() - 390) / 2;
        
        JFrame frame = new JFrame("Calculator - By Tharanga Kothalawala");
        // just giving the GUI a windows look, except the default java look
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ("Failed loading Look and Feel:\n " + ex), "Information", JOptionPane.PLAIN_MESSAGE);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // program process terminate when closes the program
        frame.getContentPane().add(new SearchGUI());
        frame.setLocation(xSize, ySize); // initially visible location
        //     frame.setResizable(false); // cannot maximize
        frame.pack();
        frame.setVisible(true); // set visible at the biginning
    }
}
