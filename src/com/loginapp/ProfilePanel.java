package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

public class ProfilePanel extends JPanel {

    private final String username;
    private JComboBox<String> skillBox;
    private JButton uploadSkillButton, uploadCertButton;
    private File selectedCertFile;

    public ProfilePanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // soft background

        // ===== Title Section =====
        JLabel title = new JLabel("Profile Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(40, 55, 71));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ===== Main Form Panel =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        formPanel.setPreferredSize(new Dimension(600, 400));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel skillLabel = new JLabel("Select Skill:");
        skillLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        skillLabel.setForeground(new Color(60, 63, 65));

        skillBox = new JComboBox<>(new String[]{
                "Java", "Python", "C++", "C#", "JavaScript", "HTML", "CSS", "Ruby", "C", "SQL"
        });
        styleComboBox(skillBox);

        uploadSkillButton = createStyledButton("➕ Add Skill");
        uploadCertButton = createStyledButton("📁 Upload Certificate");

        uploadSkillButton.addActionListener(e -> addSkill());
        uploadCertButton.addActionListener(e -> chooseAndUploadCert());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(skillLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(skillBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(uploadSkillButton, gbc);

        gbc.gridy = 2;
        formPanel.add(uploadCertButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    // ========== HELPER UI METHODS ==========
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
        });

        return button;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 35));
    }

    // ========== DATABASE OPERATIONS ==========
    private void addSkill() {
        String skill = (String) skillBox.getSelectedItem();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_skills(user_id, skill_name) VALUES((SELECT id FROM users WHERE username=?), ?)");
            ps.setString(1, username);
            ps.setString(2, skill);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Skill added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error adding skill: " + e.getMessage());
        }
    }

    private void chooseAndUploadCert() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedCertFile = chooser.getSelectedFile();
            uploadCertificate();
        }
    }

    private void uploadCertificate() {
        if (selectedCertFile == null) return;
        try (Connection conn = DBConnection.getConnection();
             FileInputStream fis = new FileInputStream(selectedCertFile)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_certificates(user_id, cert_name, cert_file) VALUES((SELECT id FROM users WHERE username=?), ?, ?)");
            ps.setString(1, username);
            ps.setString(2, selectedCertFile.getName());
            ps.setBinaryStream(3, fis, (int) selectedCertFile.length());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Certificate uploaded successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error uploading certificate: " + e.getMessage());
        }
    }
}
