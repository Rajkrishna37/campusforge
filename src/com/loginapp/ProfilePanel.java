package com.loginapp;

import com.loginapp.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

public class ProfilePanel extends JPanel {

    private String username;
    private JComboBox<String> skillBox;
    private JButton uploadSkillButton, uploadCertButton;
    private File selectedCertFile;

    public ProfilePanel(String username) {
        this.username = username;
        setLayout(new GridBagLayout());
        setBackground(Color.LIGHT_GRAY);

        JLabel title = new JLabel("Profile - Upload Skills & Certificates");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        skillBox = new JComboBox<>(new String[]{"Java", "Python", "C++", "JavaScript", "SQL"});
        uploadSkillButton = new JButton("Add Skill");
        uploadSkillButton.addActionListener(e -> addSkill());

        uploadCertButton = new JButton("Upload Certificate");
        uploadCertButton.addActionListener(e -> chooseAndUploadCert());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,15,15,15);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Select Skill:"), gbc);
        gbc.gridx = 1;
        add(skillBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(uploadSkillButton, gbc);

        gbc.gridy = 3;
        add(uploadCertButton, gbc);
    }

    private void addSkill() {
        String skill = (String) skillBox.getSelectedItem();
        try(Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_skills(user_id, skill_name) VALUES((SELECT id FROM users WHERE username=?), ?)");
            ps.setString(1, username);
            ps.setString(2, skill);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Skill added successfully!");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding skill: " + e.getMessage());
        }
    }

    private void chooseAndUploadCert() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            selectedCertFile = chooser.getSelectedFile();
            uploadCertificate();
        }
    }

    private void uploadCertificate() {
        if(selectedCertFile == null) return;
        try(Connection conn = DBConnection.getConnection();
            FileInputStream fis = new FileInputStream(selectedCertFile)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_certificates(user_id, cert_name, cert_file) VALUES((SELECT id FROM users WHERE username=?), ?, ?)");
            ps.setString(1, username);
            ps.setString(2, selectedCertFile.getName());
            ps.setBinaryStream(3, fis, (int) selectedCertFile.length());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Certificate uploaded successfully!");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error uploading certificate: " + e.getMessage());
        }
    }
}
