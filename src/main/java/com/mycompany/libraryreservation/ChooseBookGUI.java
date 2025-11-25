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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    
    public ChooseBookGUI(String username, String selectedLibrary) {
        this.username = username;
        this.selectedLibrary = selectedLibrary;

        setTitle("Choose Book - " + selectedLibrary);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);
        
        try{
            socket =new Socket("localhost",9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server");
        } catch(IOException e){
            JOptionPane.showMessageDialog(this,"Error conneting to server:" + e.getMessage());
        }

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
        add(new JLabel());
        add(reserveButton);

        loadTopics();                                           // first we get the topics
        topicBox.addActionListener(e -> loadBooks());           // then get the books of this topic
        bookBox.addActionListener(e -> loadAvailableDates());   // then get the available dates of this book
        reserveButton.addActionListener(e -> reserveBook());

        setVisible(true);
    }

    private void loadTopics() {
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
        String selectedTopic = (String) topicBox.getSelectedItem();
        if (selectedTopic == null) return;

        try{
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
        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        if (selectedTopic == null || selectedBook == null) return;

        try {
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

    private void reserveBook() {
        String selectedTopic = (String) topicBox.getSelectedItem();
        String selectedBook = (String) bookBox.getSelectedItem();
        String selectedDate = (String) comboDates.getSelectedItem();

        if (selectedTopic == null || selectedBook == null || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a topic, book, and date.");
            return;
        }

        try{
            out.println("RESERVEBOOK|" + username + "|" + selectedLibrary + "|" + selectedTopic + "|" + selectedBook + "|" + selectedDate);
            String reply = in.readLine();

            if (reply != null && (reply.contains("SUCCESS") || reply.contains("RESERVEBOOK|OK"))) {// if server replayed that reservation was succseesful then tell the user that
                JOptionPane.showMessageDialog(this, "Book reserved successfully!");
                loadAvailableDates(); 
                dispose();
                new UserDashboardGUI(username); // going bacj to the library gui
                
            } else if (reply != null && reply.contains("ALREADY")) { // incase method
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
