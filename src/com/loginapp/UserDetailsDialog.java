package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;

public class UserDetailsDialog extends JDialog {

    public UserDetailsDialog(String username) {
        setTitle("Details of " + username);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ===== Skills =====
        panel.add(new JLabel("Skills:"));
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT skill_name FROM user_skills WHERE user_id=(SELECT id FROM users WHERE username=?)");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                panel.add(new JLabel(" - " + rs.getString("skill_name")));
            }

            // ===== Certificates =====
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Certificates:"));
            ps = conn.prepareStatement(
                    "SELECT cert_name, cert_file FROM user_certificates WHERE user_id=(SELECT id FROM users WHERE username=?)");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                String certName = rs.getString("cert_name");
                byte[] certData = rs.getBytes("cert_file");
                JButton openBtn = new JButton(certName);
                openBtn.addActionListener(e -> openCertificate(certName, certData));
                panel.add(openBtn);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
        }

        add(new JScrollPane(panel), BorderLayout.CENTER);
        setVisible(true);
    }

    private void openCertificate(String certName, byte[] certData) {
        try {
            File temp = new File(System.getProperty("java.io.tmpdir") + "/" + certName);
            try (FileOutputStream fos = new FileOutputStream(temp)) {
                fos.write(certData);
            }
            Desktop.getDesktop().open(temp);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot open certificate: " + e.getMessage());
        }
    }
}
