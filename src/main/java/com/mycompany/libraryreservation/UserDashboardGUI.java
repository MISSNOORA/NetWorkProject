/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

/**
 *
 * @author noora
 */

import javax.swing.*;
import java.awt.*;
public class UserDashboardGUI extends JFrame {
    
    private String username;

    public UserDashboardGUI(String username) {
        this.username = username;

        setTitle("User Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));

        JButton viewBtn = new JButton("View My Reservations");
        JButton reserveBtn = new JButton("Reserve a New Book");
        JButton logoutBtn = new JButton("Logout");

        panel.add(welcome);
        panel.add(viewBtn);
        panel.add(reserveBtn);
        panel.add(logoutBtn);

        add(panel);

        //vieww past reservations
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
    
    
}
