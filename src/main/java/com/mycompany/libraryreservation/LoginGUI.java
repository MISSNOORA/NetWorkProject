/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LoginGUI extends JFrame {
    private JTextField txtName;
    private JPasswordField txtPassword;

    public LoginGUI() {
        setTitle("Login");
        setSize(360, 220);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("User Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setBounds(70, 15, 220, 30);
        add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(40, 60, 80, 25);
        add(lblUsername);

        txtName = new JTextField();
        txtName.setBounds(130, 60, 170, 25);
        add(txtName);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(40, 95, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 95, 170, 25);
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 130, 100, 30);
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
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
