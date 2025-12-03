/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;

public class UserDashboardGUI extends JFrame {

    private String username;

    // Theme
    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public UserDashboardGUI(String username) {
        this.username = username;

        setTitle("User Dashboard");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        // Welcome
        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(TITLE);
        welcome.setForeground(PRIMARY);
        add(welcome, BorderLayout.NORTH);

        // Buttons panel
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton viewBtn = new JButton("View My Reservations");
        JButton reserveBtn = new JButton("Reserve a New Book");
        JButton logoutBtn = new JButton("Logout");

        styleButton(viewBtn);
        styleButton(reserveBtn);
        styleButton(logoutBtn);

        panel.add(viewBtn);
        panel.add(reserveBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        // Actions
        viewBtn.addActionListener(e -> {
            new ViewReservationsGUI(username).setVisible(true);
            dispose();
        });

        reserveBtn.addActionListener(e -> {
            new ChooseLibraryGUI(username).setVisible(true);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });
    }

    private void styleButton(JButton btn) {
        btn.setFont(BUTTON);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY));
    }
}