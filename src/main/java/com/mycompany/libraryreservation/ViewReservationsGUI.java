package com.mycompany.libraryreservation;

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

        // load reservations into panel
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
        // clear current cards (useful when reloading after cancel)
        panel.removeAll();

        List<Reservations> reservations = storage.getReservationsForUser(username);

        if (reservations == null || reservations.isEmpty()) {
            panel.add(new JLabel("You have no reservations."));
            panel.revalidate();
            panel.repaint();
            return;
        }

        for (Reservations r : reservations) {
            JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // let cards stretch horizontally

            JLabel info = new JLabel(
                    "Library: " + r.getLibrary() +
                            " | Topic: " + r.getTopic() +
                            " | Book: " + r.getBook() +
                            " | Date: " + r.getDate()
            );

            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Cancellation request sent!");
                int confirm = JOptionPane.showConfirmDialog(
                        ViewReservationsGUI.this,
                        "Are you sure you want to cancel this reservation?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) return;

                // call DataStorage.cancelReservation directly (you provided this method)
                String result = storage.cancelReservation(username, r.getLibrary(), r.getTopic(), r.getBook(), r.getDate());

                // Map the returned codes to friendly messages
                String message;
                if (result == null) {
                    message = "Unknown response from storage.";
                } else if (result.startsWith("CANCEL|SUCCESS")) {
                    message = "Reservation cancelled successfully.";
                } else if (result.startsWith("CANCEL|NOTFOUND")) {
                    message = "Reservation not found.";
                } else if (result.startsWith("CANCEL|ERROR")) {
                    message = "An error occurred while cancelling.";
                } else {
                    // fallback: show raw result
                    message = result;
                }

                JOptionPane.showMessageDialog(ViewReservationsGUI.this, message);

                // reload UI to reflect change
                loadReservations();
                panel.revalidate();
                panel.repaint();
            });

            // add components to card and card to panel
            card.add(info);
            card.add(cancel);
            panel.add(card);
            panel.add(Box.createRigidArea(new Dimension(0, 5))); // small gap between cards
        }
        panel.revalidate();
        panel.repaint();
    }

    // Optional
    public static void main(String[] args) {SwingUtilities.invokeLater(() -> {
            ViewReservationsGUI gui = new ViewReservationsGUI("testuser");
            gui.setVisible(true);
        });
    }
}