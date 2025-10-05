package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class HomePanel extends JPanel {

    private final String username;

    public HomePanel(String username) {
        this.username = username;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        loadData();
    }

    private void loadData() {
        removeAll();

        try (Connection conn = DBConnection.getConnection()) {

            // === Load Skills ===
            PreparedStatement psSkills = conn.prepareStatement(
                    "SELECT DISTINCT skill_name FROM user_skills WHERE user_id=(SELECT id FROM users WHERE username=?)");
            psSkills.setString(1, username);
            ResultSet rsSkills = psSkills.executeQuery();

            Set<String> skills = new LinkedHashSet<>();
            while (rsSkills.next()) {
                skills.add(rsSkills.getString("skill_name"));
            }

            StringBuilder skillText = new StringBuilder("Your skills: ");
            if (skills.isEmpty()) {
                skillText.append("None");
            } else {
                skillText.append(String.join(", ", skills));
            }

            JLabel skillsLabel = new JLabel(skillText.toString());
            skillsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            skillsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(skillsLabel);

            add(Box.createVerticalStrut(15));

            // === Load Certificates ===
            JLabel certTitle = new JLabel("Your certificates:");
            certTitle.setFont(new Font("Arial", Font.PLAIN, 16));
            certTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(certTitle);
            add(Box.createVerticalStrut(5));

            PreparedStatement psCerts = conn.prepareStatement(
                    "SELECT cert_name, cert_file FROM user_certificates WHERE user_id=(SELECT id FROM users WHERE username=?)");
            psCerts.setString(1, username);
            ResultSet rsCerts = psCerts.executeQuery();

            if (!rsCerts.isBeforeFirst()) {
                JLabel noCert = new JLabel("No certificates uploaded.");
                noCert.setFont(new Font("Arial", Font.ITALIC, 14));
                noCert.setAlignmentX(Component.LEFT_ALIGNMENT);
                add(noCert);
            } else {
                JPanel certContainer = new JPanel();
                certContainer.setLayout(new BoxLayout(certContainer, BoxLayout.Y_AXIS));
                certContainer.setBackground(Color.WHITE);
                certContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

                while (rsCerts.next()) {
                    String certName = rsCerts.getString("cert_name");
                    byte[] certData = rsCerts.getBytes("cert_file");

                    JPanel certPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 1)); // tighter spacing
                    certPanel.setBackground(Color.WHITE);

                    JLabel certLabel = new JLabel("\"" + certName + "\"");
                    JButton openBtn = new JButton("Open");
                    openBtn.setFont(new Font("Arial", Font.PLAIN, 12));
                    openBtn.addActionListener(e -> showCertificate(certName, certData));

                    certPanel.add(certLabel);
                    certPanel.add(openBtn);

                    certContainer.add(certPanel);
                }

                add(certContainer);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }

        revalidate();
        repaint();
    }

    private void showCertificate(String certName, byte[] certData) {
        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + certName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(certData);
            }
            Desktop.getDesktop().open(tempFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening certificate: " + e.getMessage());
        }
    }
}
