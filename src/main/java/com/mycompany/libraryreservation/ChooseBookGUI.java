package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChooseBookGUI extends JFrame {
    private JComboBox<String> topicBox;
    private JComboBox<String> bookBox;
    private JComboBox<String> comboDates;
    private JButton reserveButton;
    private String selectedLibrary;
    private String username;

    // Constructor
    public ChooseBookGUI(String username, String selectedLibrary) {
        this.username = username;
        this.selectedLibrary = selectedLibrary;

        setTitle("Choose Book - " + selectedLibrary);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);

        // Components
        add(new JLabel("Choose a topic:"));
        topicBox = new JComboBox<>();
        add(topicBox);

        add(new JLabel("Choose a book:"));
        bookBox = new JComboBox<>();
        add(bookBox);

        add(new JLabel("Choose available date:"));
        comboDates = new JComboBox<>();
        add(comboDates);

        reserveButton = new JButton("Reserve Book");
        add(new JLabel()); // Empty cell
        add(reserveButton);

        // Load topics on start
        loadTopics();

        // When a topic is selected → load books
        topicBox.addActionListener(e -> loadBooks());

        // When a book is selected → load available dates
        bookBox.addActionListener(e -> loadAvailableDates());

        // When reserve button is clicked
        reserveButton.addActionListener(e -> reserveBook());

        setVisible(true);
    }

    // ---------- Load Topics ----------
    private void loadTopics() {
        try (Socket socket = new Socket("localhost", 9090);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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

    // ---------- Load Books ----------
    private void loadBooks() {
        String selectedTopic = (String) topicBox.getSelectedItem();
        if (selectedTopic == null) return;

        try (Socket socket = new Socket("localhost", 9090);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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

    // ---------- Load Available Dates ----------
    private void loadAvailableDates() {
        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        if (selectedTopic == null || selectedBook == null) return;

        try (Socket socket = new Socket("localhost", 9090);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GETDATES|" + selectedLibrary + "|" + selectedTopic + "|" + selectedBook);
            String reply_ = in.readLine();

            comboDates.removeAllItems(); // clear old ones

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

    // ---------- Reserve Book ----------
    private void reserveBook() {
        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        String selectedDate = (String) comboDates.getSelectedItem();

        if (selectedTopic == null || selectedBook == null || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a topic, book, and date.");
            return;
        }

        try (Socket socket = new Socket("localhost", 9090);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("RESERVEBOOK|" + username + "|" + selectedLibrary + "|" + selectedTopic + "|" + selectedBook + "|" + selectedDate);
            String reply = in.readLine();

            if (reply != null && reply.contains("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Book reserved successfully!");
                loadAvailableDates(); // refresh available dates for others
            } else if (reply != null && reply.contains("ALREADY")) {
                JOptionPane.showMessageDialog(this, "This date is already reserved. Please choose another.");
                loadAvailableDates(); // refresh available dates again
            } else {
                JOptionPane.showMessageDialog(this, "Reservation failed. Server said: " + reply);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage());
        }
    }

    
}
