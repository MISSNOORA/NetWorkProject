
package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChooseBookGUI extends JFrame {

    private JComboBox<String> topicBox;
    private JComboBox<String> bookBox;
    private JComboBox<String> comboDates;
    private JButton reserveButton;

    private String selectedLibrary;
    private String username;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Theme
    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);
    private static final Font LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public ChooseBookGUI(String username, String selectedLibrary) {
        this.username = username;
        this.selectedLibrary = selectedLibrary;

        setTitle("Choose Book - " + selectedLibrary);
        setSize(450, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);
        setLayout(new GridLayout(5, 2, 10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // الاتصال بالسيرفر
        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error connecting to server: " + e.getMessage());
        }

        // عناصر الواجهة
        JLabel lblTopic = new JLabel("Choose a topic:");
        lblTopic.setFont(LABEL);
        add(lblTopic);

        topicBox = new JComboBox<>();
        topicBox.setFont(LABEL);
        add(topicBox);

        JLabel lblBook = new JLabel("Choose a book:");
        lblBook.setFont(LABEL);
        add(lblBook);

        bookBox = new JComboBox<>();
        bookBox.setFont(LABEL);
        add(bookBox);

        JLabel lblDate = new JLabel("Choose available date:");
        lblDate.setFont(LABEL);
        add(lblDate);

        comboDates = new JComboBox<>();
        comboDates.setFont(LABEL);
        add(comboDates);

        // خانة فاضية لمحاذاة الزر
        add(new JLabel());

        reserveButton = new JButton("Reserve Book");
        reserveButton.setFont(BUTTON);
        reserveButton.setBackground(PRIMARY);
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setFocusPainted(false);
        reserveButton.setOpaque(true);
        reserveButton.setBorder(BorderFactory.createLineBorder(PRIMARY));
        add(reserveButton);

        // تحميل البيانات
        loadTopics();
        topicBox.addActionListener(e -> loadBooks());
        bookBox.addActionListener(e -> loadAvailableDates());
        reserveButton.addActionListener(e -> reserveBook());

        setVisible(true);
    }

    private void loadTopics() {
        if (out == null || in == null) return;

        try {
            out.println("GETTOPICS|" + selectedLibrary);
            String reply = in.readLine();
            if (reply != null) {
                topicBox.removeAllItems();
                for (String t : reply.split("\\|")) {
                    topicBox.addItem(t.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading topics: " + e.getMessage());
        }
    }

    private void loadBooks() {
        if (out == null || in == null) return;

        String selectedTopic = (String) topicBox.getSelectedItem();
        if (selectedTopic == null) return;

        try {
            out.println("GETBOOKS|" + selectedLibrary + "|" + selectedTopic);
            String reply = in.readLine();
            if (reply != null) {
                bookBox.removeAllItems();
                for (String b : reply.split("\\|")) {
                    bookBox.addItem(b.trim());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void loadAvailableDates() {
        if (out == null || in == null) return;

        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        if (selectedTopic == null || selectedBook == null) return;

        try {
            out.println("GETDATES|" + selectedLibrary + "|" + selectedTopic + "|" + selectedBook);
            String reply_ = in.readLine();
            comboDates.removeAllItems();

            if (reply_ != null && !reply_.isEmpty()) {
                String[] dates = reply_.split("\\|");
                for (String d : dates) {
                    comboDates.addItem(d.trim());
                }
            } else {
                comboDates.addItem("No dates available");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading dates: " + e.getMessage());
        }
    }

    private void reserveBook() {
        if (out == null || in == null) return;

        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        String selectedDate = (String) comboDates.getSelectedItem();

        if (selectedTopic == null || selectedBook == null || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a topic, book, and date.");
            return;
        }

        try {
            out.println("RESERVEBOOK|" + username + "|" + selectedLibrary + "|"
                    + selectedTopic + "|" + selectedBook + "|" + selectedDate);

            String reply = in.readLine();

            if (reply != null && (reply.contains("SUCCESS") || reply.contains("RESERVEBOOK|OK"))) {
                JOptionPane.showMessageDialog(this, "Book reserved successfully!");
                loadAvailableDates();
                UserDashboardGUI gui = new UserDashboardGUI(username);
                gui.setVisible(true);

                this.dispose();
                
            } else if (reply != null && reply.contains("ALREADY")) {
                JOptionPane.showMessageDialog(this,
                        "This date is already reserved. Please choose another.");
                loadAvailableDates();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Reservation failed. Server said: " + reply);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error connecting to server: " + e.getMessage());
        }
    }
}