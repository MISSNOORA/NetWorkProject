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
        setSize(400, 230);
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

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBounds(140, 160, 120, 35);
        btnRegister.setBackground(new Color(200, 230, 255));
        btnRegister.setFocusPainted(false);
        add(btnRegister);

        // 💡 this line makes the button open ChooseLibraryGUI after registration
        btnRegister.addActionListener(e -> tryRegister());

        setVisible(true);
    }

    // ---------------- REGISTER LOGIC ----------------
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

            // send register command
            out.println("REGISTER|" + name + "|" + pass);

            // wait for reply
            String reply = in.readLine();
            System.out.println("Server reply: " + reply);

            // success
            if (reply != null && reply.contains("successfully")) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new ChooseLibraryGUI(name); 
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.\nServer said: " + reply);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to the server. Make sure it's running.");
        }
    }

    public static void main(String[] args) {
        new RegistrationGUI();
    }
}
