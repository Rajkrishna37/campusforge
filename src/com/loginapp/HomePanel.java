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
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // soft gray background

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Title Header
        JLabel title = new JLabel("🏠 Home Dashboard");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setForeground(new Color(40, 55, 90));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(25));

        // Load Data
        loadData(contentPanel);
    }

    private void loadData(JPanel contentPanel) {
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

            // Section Header
            JLabel skillTitle = new JLabel("🧠 Your Skills");
            skillTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            skillTitle.setForeground(new Color(60, 60, 100));
            skillTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(skillTitle);
            contentPanel.add(Box.createVerticalStrut(10));

            JPanel skillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            skillPanel.setBackground(new Color(250, 250, 250));
            skillPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            if (skills.isEmpty()) {
                JLabel noSkill = new JLabel("No skills added yet.");
                noSkill.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                skillPanel.add(noSkill);
            } else {
                for (String skill : skills) {
                    JLabel skillLabel = new JLabel(skill);
                    skillLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    skillLabel.setForeground(Color.WHITE);
                    skillLabel.setOpaque(true);
                    skillLabel.setBackground(new Color(70, 130, 180)); // soft blue
                    skillLabel.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                    skillLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    skillLabel.setToolTipText("Skill: " + skill);
                    skillLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(60, 120, 160)),
                            BorderFactory.createEmptyBorder(5, 12, 5, 12)
                    ));
                    skillLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    skillLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                    skillLabel.setBackground(new Color(80, 140, 220));
                    skillLabel.setForeground(Color.WHITE);
                    skillLabel.setOpaque(true);
                    skillLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(70, 130, 180), 1, true),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    ));
                    skillLabel.setToolTipText("Your skill: " + skill);
                    skillPanel.add(skillLabel);
                }
            }

            skillPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(skillPanel);
            contentPanel.add(Box.createVerticalStrut(25));

            // === Load Certificates ===
            JLabel certTitle = new JLabel("📜 Your Certificates");
            certTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            certTitle.setForeground(new Color(60, 60, 100));
            certTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(certTitle);
            contentPanel.add(Box.createVerticalStrut(10));

            PreparedStatement psCerts = conn.prepareStatement(
                    "SELECT cert_name, cert_file FROM user_certificates WHERE user_id=(SELECT id FROM users WHERE username=?)");
            psCerts.setString(1, username);
            ResultSet rsCerts = psCerts.executeQuery();

            JPanel certContainer = new JPanel();
            certContainer.setLayout(new BoxLayout(certContainer, BoxLayout.Y_AXIS));
            certContainer.setBackground(new Color(250, 250, 250));
            certContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            certContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (!rsCerts.isBeforeFirst()) {
                JLabel noCert = new JLabel("No certificates uploaded yet.");
                noCert.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                certContainer.add(noCert);
            } else {
                while (rsCerts.next()) {
                    String certName = rsCerts.getString("cert_name");
                    byte[] certData = rsCerts.getBytes("cert_file");

                    JPanel certPanel = new JPanel(new BorderLayout());
                    certPanel.setBackground(Color.WHITE);
                    certPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10),
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))
                    ));

                    JLabel certLabel = new JLabel("📄 " + certName);
                    certLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

                    JButton openBtn = new JButton("Open");
                    openBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    openBtn.setBackground(new Color(60, 120, 200));
                    openBtn.setForeground(Color.WHITE);
                    openBtn.setFocusPainted(false);
                    openBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
                    openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    openBtn.addActionListener(e -> showCertificate(certName, certData));

                    certPanel.add(certLabel, BorderLayout.WEST);
                    certPanel.add(openBtn, BorderLayout.EAST);

                    certContainer.add(certPanel);
                }
            }

            contentPanel.add(certContainer);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
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
