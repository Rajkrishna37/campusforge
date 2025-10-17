package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserDetailsDialog extends JDialog {

    public UserDetailsDialog(String username) {
        setTitle("Details of " + username);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel skillsTitle = new JLabel("Skills:");
        skillsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(skillsTitle);
        panel.add(Box.createVerticalStrut(5));

        try (Connection conn = DBConnection.getConnection()) {
            // ===== Unique Skills =====
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT DISTINCT skill_name FROM user_skills WHERE user_id=(SELECT id FROM users WHERE username=?)");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            Set<String> skillsSet = new LinkedHashSet<>();
            while (rs.next()) {
                skillsSet.add(rs.getString("skill_name"));
            }

            if (skillsSet.isEmpty()) {
                JLabel noSkills = new JLabel("No skills added.");
                noSkills.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                panel.add(noSkills);
            } else {
                for (String skill : skillsSet) {
                    JLabel skillLabel = new JLabel(" - " + skill);
                    skillLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    panel.add(skillLabel);
                }
            }

            // ===== Certificates =====
            panel.add(Box.createVerticalStrut(15));
            JLabel certTitle = new JLabel("Certificates:");
            certTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            panel.add(certTitle);
            panel.add(Box.createVerticalStrut(5));

            ps = conn.prepareStatement(
                    "SELECT cert_name, cert_file FROM user_certificates WHERE user_id=(SELECT id FROM users WHERE username=?)");
            ps.setString(1, username);
            rs = ps.executeQuery();

            boolean hasCerts = false;
            while (rs.next()) {
                hasCerts = true;
                String certName = rs.getString("cert_name");
                byte[] certData = rs.getBytes("cert_file");

                JButton openBtn = new JButton(certName);
                styleButton(openBtn);
                openBtn.addActionListener(e -> openCertificate(certName, certData));

                panel.add(openBtn);
                panel.add(Box.createVerticalStrut(5));
            }

            if (!hasCerts) {
                JLabel noCerts = new JLabel("No certificates uploaded.");
                noCerts.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                panel.add(noCerts);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

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

    // Modern styled buttons
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
    }
}
