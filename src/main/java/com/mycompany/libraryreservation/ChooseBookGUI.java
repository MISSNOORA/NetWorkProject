package com.mycompany.libraryreservation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ChooseBookGUI extends JFrame{
    private JComboBox<String> topicBox, bookBox, dateBox;
    private JButton reserveBtn;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username, library;

    public ChooseBookGUI(String username, String library) {
        this.username = username;
        this.library = library;

        setTitle("Choose Topic & Book");
        setSize(450, 350);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTopic = new JLabel("Choose a topic:");
        lblTopic.setBounds(50, 50, 200, 25);
        add(lblTopic);

        topicBox = new JComboBox<>();
        topicBox.setBounds(200, 50, 180, 25);
        add(topicBox);

        JLabel lblBook = new JLabel("Choose a book:");
        lblBook.setBounds(50, 100, 200, 25);
        add(lblBook);

        bookBox = new JComboBox<>();
        bookBox.setBounds(200, 100, 180, 25);
        add(bookBox);

        JLabel lblDate = new JLabel("Choose date:");
        lblDate.setBounds(50, 150, 200, 25);
        add(lblDate);

        dateBox = new JComboBox<>();
        dateBox.setBounds(200, 150, 180, 25);
        add(dateBox);

        reserveBtn = new JButton("Reserve Book");
        reserveBtn.setBounds(150, 220, 150, 40);
        add(reserveBtn);

        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("GETTOPICS|" + library);
            String reply = in.readLine();
            for (String t : reply.split("\\|")) topicBox.addItem(t);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection error.");
        }

        topicBox.addActionListener(e -> {
            String topic = (String) topicBox.getSelectedItem();
            if (topic != null) {
                out.println("GETBOOKS|" + library + "|" + topic);
                try {
                    bookBox.removeAllItems();
                    String reply = in.readLine();
                    for (String b : reply.split("\\|")) bookBox.addItem(b);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading books.");
                }
            }
        });

        bookBox.addActionListener(e -> updateAvailableDates());

        reserveBtn.addActionListener(e -> {
            String topic = (String) topicBox.getSelectedItem();
            String book = (String) bookBox.getSelectedItem();
            String date = (String) dateBox.getSelectedItem();

            if (topic == null || book == null || date == null) {
                JOptionPane.showMessageDialog(this, "Please select a topic, book, and date before reserving.");
                return;
            }

            String cmd = "RESERVEBOOK|" + username + "|" + library + "|" + topic + "|" + book + "|" + date;
            out.println(cmd);

            try {
                String reply = in.readLine();
                JOptionPane.showMessageDialog(this, reply);

                if (reply.startsWith("OK:")) {
                    dateBox.removeItem(date);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error communicating with server.");
            }
        });

        setVisible(true);
    }

    private void updateAvailableDates() {
        dateBox.removeAllItems();
        String topic = (String) topicBox.getSelectedItem();
        String book = (String) bookBox.getSelectedItem();

        if (topic == null || book == null) return;

        for (String date : DataStorage.getAvailableDates()) {

            if (!DataStorage.isAlreadyReserved(library, topic, book, date)) {
                dateBox.addItem(date);
            }
        }
    }
}