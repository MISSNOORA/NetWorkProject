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
import java.util.List;

public class ViewReservationsGUI extends JFrame {
    
    private String username;
    private DataStorage storage;
    private JPanel panel;

    public ViewReservationsGUI(String username) {
        this.username = username;

        setTitle("My Reservations");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        storage = new DataStorage();

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        loadReservations();

        JScrollPane scroll = new JScrollPane(panel);

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            new UserDashboardGUI(username).setVisible(true);
            dispose();
        });

        add(scroll, BorderLayout.CENTER);
        add(back, BorderLayout.SOUTH);
    }

    private void loadReservations() {
        List<Reservations> reservations = storage.getReservationsForUser(username);

        if (reservations.isEmpty()) {
            panel.add(new JLabel("You have no reservations."));
            return;
        }

        for (Reservations r : reservations) {
            JPanel card = new JPanel(new GridLayout(2, 1));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel info = new JLabel(
                "Library: " + r.getLibrary() +
                " | Topic: " + r.getTopic() +
                " | Book: " + r.getBook() +
                " | Date: " + r.getDate()
            );

            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Cancellation request sent!");
            });

            card.add(info);
            card.add(cancel);

            panel.add(card);
        }
    }
}
