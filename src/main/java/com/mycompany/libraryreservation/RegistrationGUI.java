
package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class RegistrationGUI extends JFrame {

    private JTextField txtName;
    private JPasswordField txtPassword;

    // Theme
    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);      // أزرق غامق
    private static final Color PRIMARY_LIGHT = new Color(0, 90, 180); // أزرق أفتح
    private static final Color TEXT = new Color(30, 30, 30);
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public RegistrationGUI() {
        setTitle("Registration");
        setSize(420, 330);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);

        // عنوان النظام
        JLabel lblSystem = new JLabel("Library Booking System", SwingConstants.CENTER);
        lblSystem.setFont(TITLE);
        lblSystem.setForeground(PRIMARY);
        lblSystem.setBounds(60, 20, 300, 25);
        add(lblSystem);

        // عنوان فرعي
        JLabel lblTitle = new JLabel("Create a New Account", SwingConstants.CENTER);
        lblTitle.setFont(LABEL);
        lblTitle.setForeground(TEXT);
        lblTitle.setBounds(90, 55, 240, 20);
        add(lblTitle);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(LABEL);
        lblUsername.setBounds(60, 110, 100, 25);
        add(lblUsername);

        txtName = new JTextField();
        txtName.setBounds(160, 110, 200, 25);
        txtName.setFont(LABEL);
        add(txtName);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(LABEL);
        lblPassword.setBounds(60, 145, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 145, 200, 25);
        txtPassword.setFont(LABEL);
        add(txtPassword);

        // زر Register
        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(BUTTON);
        btnRegister.setBackground(PRIMARY);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(true);
        btnRegister.setBorder(BorderFactory.createLineBorder(PRIMARY));
        btnRegister.setBounds(150, 185, 120, 32);
        add(btnRegister);

        // OR
        JLabel lblOr = new JLabel("or", SwingConstants.CENTER);
        lblOr.setFont(LABEL);
        lblOr.setBounds(150, 220, 120, 20);
        add(lblOr);

        // زر Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(BUTTON);
        btnLogin.setBackground(PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorder(BorderFactory.createLineBorder(PRIMARY_LIGHT));
        btnLogin.setBounds(150, 245, 120, 32);
        add(btnLogin);

        // Actions
        btnRegister.addActionListener(e -> tryRegister());
        btnLogin.addActionListener(e -> {
            new LoginGUI();
            dispose();
        });

        setVisible(true);
    }

    private void tryRegister() {
        String name = txtName.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.");
            return;
        }

        try (Socket socket = new Socket("localhost", 9090);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("REGISTER|" + name + "|" + pass);

            String reply = in.readLine();
            System.out.println("Server reply: " + reply);

            if (reply != null && reply.contains("REGISTER|OK")) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new ChooseLibraryGUI(name);
                dispose();
            } else if (reply != null && reply.contains("Username taken")) {
                JOptionPane.showMessageDialog(this, "This username is already taken.");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed: " + reply);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the server.");
        }
    }

    public static void main(String[] args) {
        new RegistrationGUI();
    }
}