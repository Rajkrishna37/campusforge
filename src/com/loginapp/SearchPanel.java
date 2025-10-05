package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SearchPanel extends JPanel {

    private JTextField skillField;
    private JPanel resultPanel;
    private String username;

    public SearchPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // Top search panel
        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("Search Users by Skill:");
        skillField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        topPanel.add(label);
        topPanel.add(skillField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // Result panel
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(resultPanel);
        add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchUsers());
    }

    private void searchUsers() {
        resultPanel.removeAll();
        String skill = skillField.getText().trim();
        if (skill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a skill to search.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Step 1: Get distinct users with the skill
            PreparedStatement psUsers = conn.prepareStatement(
                    "SELECT DISTINCT u.id, u.username " +
                    "FROM users u " +
                    "JOIN user_skills us ON u.id = us.user_id " +
                    "WHERE us.skill_name LIKE ?"
            );
            psUsers.setString(1, "%" + skill + "%");
            ResultSet rsUsers = psUsers.executeQuery();

            Set<Integer> userIds = new HashSet<>();

            while (rsUsers.next()) {
                int userId = rsUsers.getInt("id");
                String uname = rsUsers.getString("username");

                if (!userIds.contains(userId)) {
                    userIds.add(userId);

                    // Panel for this user
                    JPanel userPanel = new JPanel(new BorderLayout());
                    userPanel.setBorder(BorderFactory.createTitledBorder(uname));

                    // Fetch all skills
                    PreparedStatement psSkills = conn.prepareStatement(
                            "SELECT skill_name FROM user_skills WHERE user_id = ?"
                    );
                    psSkills.setInt(1, userId);
                    ResultSet rsSkills = psSkills.executeQuery();
                    StringBuilder skillsText = new StringBuilder("Skills: ");
                    while (rsSkills.next()) {
                        skillsText.append(rsSkills.getString("skill_name")).append(", ");
                    }
                    if (skillsText.length() > 8) skillsText.setLength(skillsText.length() - 2);
                    JLabel skillsLabel = new JLabel(skillsText.toString());

                    // Fetch all certificate names
                    PreparedStatement psCerts = conn.prepareStatement(
                            "SELECT cert_name FROM user_certificates WHERE user_id = ?"
                    );
                    psCerts.setInt(1, userId);
                    ResultSet rsCerts = psCerts.executeQuery();
                    StringBuilder certsText = new StringBuilder("Certificates: ");
                    while (rsCerts.next()) {
                        certsText.append(rsCerts.getString("cert_name")).append(", ");
                    }
                    if (certsText.length() > 14) certsText.setLength(certsText.length() - 2);
                    JLabel certsLabel = new JLabel(certsText.toString());

                    JPanel infoPanel = new JPanel();
                    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                    infoPanel.add(skillsLabel);
                    infoPanel.add(certsLabel);

                    userPanel.add(infoPanel, BorderLayout.CENTER);
                    resultPanel.add(userPanel);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching users: " + e.getMessage());
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
