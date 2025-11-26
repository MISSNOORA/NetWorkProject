/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChooseLibraryGUI extends JFrame {
    private JButton nextBtn;
    private JComboBox<String> libraryBox;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;
    
     public ChooseLibraryGUI(String username) {
        this.username = username;
        setTitle("Choose a Library");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
     JLabel lbl = new JLabel("Choose a library");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBounds(80, 20, 240, 30);
        add(lbl);

        libraryBox = new JComboBox<>();
        libraryBox.setBounds(100, 70, 200, 30);
        add(libraryBox);

        nextBtn = new JButton("Next");
        nextBtn.setBounds(150, 130, 100, 35);
        add(nextBtn);

        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("GETLIBRARIES");// sends command so the server can display the list of librarys
            String reply = in.readLine();
            for (String lib : reply.split("\\|")) libraryBox.addItem(lib);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server.");
        }

        nextBtn.addActionListener(e -> { // clicking next will take the chosen library and sends it to the book gui to displays its topics,books and dates
            String selectedLibrary = (String) libraryBox.getSelectedItem();
            if (selectedLibrary != null) {
                new ChooseBookGUI(username, selectedLibrary);
                dispose();
            }
        });

        setVisible(true);   
     }
     
    
}