package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class RegistrationGUI extends JFrame {

    private JTextField txtName;
    private JPasswordField txtPassword;

    public RegistrationGUI() {
        setTitle("Library Registration");
        setSize(400, 300); // زدنا الارتفاع لأن عندنا 3 عناصر (Register - or - Login)
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("Register New User", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setBounds(70, 15, 260, 30);
        add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 70, 100, 25);
        add(lblUsername);

        txtName = new JTextField();
        txtName.setBounds(150, 70, 190, 25);
        add(txtName);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 105, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 105, 190, 25);
        add(txtPassword);

        // زر Register
        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBounds(145, 140, 110, 35);
        add(btnRegister);

        // كلمة OR
        JLabel lblOr = new JLabel("or", SwingConstants.CENTER);
        lblOr.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblOr.setBounds(145, 180, 110, 20);
        add(lblOr);

        // زر Login بنفس حجم Register
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setBounds(145, 205, 110, 35);
        add(btnLogin);

        // Register Action
        btnRegister.addActionListener(e -> tryRegister());

        // Login Action
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
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("REGISTER|" + name + "|" + pass);

            String reply = in.readLine();
            System.out.println("Server reply: " + reply);

            if (reply != null && reply.contains("REGISTER|OK")) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new ChooseLibraryGUI(name);
                dispose();
            } else if (reply != null && reply.contains("Username taken")) {
                JOptionPane.showMessageDialog(this, "This username is already taken. Please choose another one.");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.\nServer said: " + reply);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the server.");
        }
    }

    public static void main(String[] args) {
        new RegistrationGUI();
    }
}