package com.mycompany.libraryreservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class RegistrationGUI extends JFrame {

    private JTextField txtName;
    private JPasswordField txtPassword;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public RegistrationGUI() {

        setTitle("Library Registration");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);


        JLabel lblTitle = new JLabel("Register New User");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(80, 15, 240, 30);
        add(lblTitle);

        JLabel lblName = new JLabel("Username:");
        lblName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblName.setBounds(50, 70, 100, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(150, 70, 180, 25);
        add(txtName);


        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblPassword.setBounds(50, 110, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 110, 180, 25);
        add(txtPassword);


        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBounds(140, 160, 120, 35);
        btnRegister.setBackground(new Color(200, 230, 255));
        btnRegister.setFocusPainted(false);
        add(btnRegister);
        

        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(this, "Connected to server");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationGUI.this, "Please enter both username and password.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String command = "REGISTER|" + name + "|" + password;
                out.println(command);

                try {
                    String reply = in.readLine();
                    JOptionPane.showMessageDialog(RegistrationGUI.this, reply);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(RegistrationGUI.this, "Error reading server response.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new RegistrationGUI();
    }
}
