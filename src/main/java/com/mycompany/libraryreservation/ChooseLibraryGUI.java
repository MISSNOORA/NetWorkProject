package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChooseLibraryGUI extends JFrame {

    private JButton nextBtn;
    private JComboBox<String> libraryBox;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    // Theme
    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public ChooseLibraryGUI(String username) {
        this.username = username;

        setTitle("Choose a Library");
        setSize(420, 260);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);

        // عنوان النظام
        JLabel lblSystem = new JLabel("Library Booking System", SwingConstants.CENTER);
        lblSystem.setFont(TITLE);
        lblSystem.setForeground(PRIMARY);
        lblSystem.setBounds(60, 15, 300, 25);
        add(lblSystem);

        // عنوان الشاشة
        JLabel lbl = new JLabel("Choose a Library", SwingConstants.CENTER);
        lbl.setFont(LABEL);
        lbl.setBounds(90, 45, 240, 25);
        add(lbl);

        // ComboBox المكتبات
        libraryBox = new JComboBox<>();
        libraryBox.setFont(LABEL);
        libraryBox.setBounds(100, 85, 220, 30);
        add(libraryBox);

        // زر Next
        nextBtn = new JButton("Next");
        nextBtn.setFont(BUTTON);
        nextBtn.setBackground(PRIMARY);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setOpaque(true);
        nextBtn.setBorder(BorderFactory.createLineBorder(PRIMARY));
        nextBtn.setBounds(160, 135, 100, 32);
        add(nextBtn);

        // الاتصال بالسيرفر وجلب المكتبات
        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("GETLIBRARIES");
            String reply = in.readLine();
            if (reply != null) {
                for (String lib : reply.split("\\|")) {
                    libraryBox.addItem(lib.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server.");
        }

        // الحدث
        nextBtn.addActionListener(e -> {
            String selectedLibrary = (String) libraryBox.getSelectedItem();
            if (selectedLibrary != null) {
                new ChooseBookGUI(username, selectedLibrary);
                dispose();
            }
        });

        setVisible(true);
    }
}