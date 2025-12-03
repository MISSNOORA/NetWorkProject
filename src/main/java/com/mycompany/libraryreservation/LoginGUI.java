
package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LoginGUI extends JFrame {

    private JTextField txtName;
    private JPasswordField txtPassword;

    // Theme
    private static final Color BG = Color.WHITE;
    private static final Color PRIMARY = new Color(0, 70, 140);
    private static final Color TEXT = new Color(30, 30, 30);
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public LoginGUI() {
        setTitle("Login");
        setSize(420, 300);
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
        JLabel lblTitle = new JLabel("User Login", SwingConstants.CENTER);
        lblTitle.setFont(LABEL);
        lblTitle.setForeground(TEXT);
        lblTitle.setBounds(90, 50, 240, 20);
        add(lblTitle);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(LABEL);
        lblUsername.setBounds(60, 100, 100, 25);
        add(lblUsername);

        txtName = new JTextField();
        txtName.setFont(LABEL);
        txtName.setBounds(160, 100, 200, 25);
        add(txtName);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(LABEL);
        lblPassword.setBounds(60, 135, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(LABEL);
        txtPassword.setBounds(160, 135, 200, 25);
        add(txtPassword);

        // زر Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(BUTTON);
        btnLogin.setBackground(PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorder(BorderFactory.createLineBorder(PRIMARY));
        btnLogin.setBounds(155, 185, 110, 32);
        add(btnLogin);

        btnLogin.addActionListener(e -> tryLogin());

        setVisible(true);
    }

    private void tryLogin() {
        String name = txtName.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill both fields.");
            return;
        }

        try (Socket socket = new Socket("localhost", 9090);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("LOGIN|" + name + "|" + pass);
            String reply = in.readLine();
            System.out.println("Server reply: " + reply);

            if (reply != null && reply.startsWith("LOGIN|OK")) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new UserDashboardGUI(name).setVisible(true);
                dispose();
            } else if (reply != null && reply.startsWith("LOGIN|FAIL")) {
                String[] p = reply.split("\\|", 3);
                String reason = p.length >= 3 ? p[2] : "Login failed.";
                JOptionPane.showMessageDialog(this, "Login failed: " + reason);
            } else {
                JOptionPane.showMessageDialog(this, "Unexpected server response: " + reply);
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot connect to server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}