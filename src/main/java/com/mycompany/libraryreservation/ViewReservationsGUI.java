package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ViewReservationsGUI extends JFrame {

    private String username;
    private DataStorage storage;
    private JPanel panel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);
    private static final Font LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 13);

    public ViewReservationsGUI(String username) {
        this.username = username;

        setTitle("My Reservations");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        storage = new DataStorage();
        try {
            socket = new Socket("localhost", 9090);  // you asked to keep localhost
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

 
        loadReservations();
    }


    private void loadReservations() {
        panel.removeAll();

        List<Reservations> reservations = new ArrayList<>();

        try {
            out.println("GETRESERVATIONS|" + username);


            String reply = in.readLine();
            System.out.println("Server reply: " + reply);

            if (reply == null || reply.equals("NONE") || reply.isEmpty()) {
                JLabel lbl = new JLabel("You have no reservations.");
                lbl.setFont(LABEL);
                panel.add(lbl);
                panel.revalidate();
                panel.repaint();
                return;
            }

            String[] items = reply.split(":::");

            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length < 4) continue;

                String library = parts[0];
                String topic = parts[1];
                String book = parts[2];
                String date = parts[3];

                Reservations r = new Reservations(username, library, topic, book, date);
                reservations.add(r);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

 
        for (Reservations r : reservations) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel info = new JLabel(
                    "Library: " + r.getLibrary() +
                    " | Topic: " + r.getTopic() +
                    " | Book: " + r.getBook() +
                    " | Date: " + r.getDate()
            );
            info.setFont(LABEL);
            card.add(info, BorderLayout.CENTER);

            JButton cancel = new JButton("Cancel");
            styleButton(cancel);
            cancel.setFont(BUTTON);
            cancel.addActionListener(e -> handleCancel(r));

            card.add(cancel, BorderLayout.EAST);
            panel.add(card);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.revalidate();
        panel.repaint();
    }

    private void handleCancel(Reservations r) {
        try {
            out.println("CANCEL|" + username + "|" 
            + r.getLibrary() + "|" 
            + r.getTopic() + "|" 
            + r.getBook() + "|" 
            + r.getDate());

            String reply = in.readLine();
            System.out.println("Cancel reply: " + reply);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadReservations(); // refresh after cancel
    }

    private void styleButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
}
