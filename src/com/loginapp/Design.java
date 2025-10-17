package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserHomePanel extends JPanel {
    private String username;
    private JLabel nameLabel, welcomeLabel;
    private JPanel contentPanel;

    public UserHomePanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel("Welcome back, " + username + " 👋", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel title = new JLabel("🏫 Campus Forge - User Dashboard", SwingConstants.RIGHT);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        title.setForeground(Color.LIGHT_GRAY);

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(title, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== CONTENT AREA =====
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 3, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(new Color(245, 247, 250));

        // Add "cards"
        contentPanel.add(createInfoCard("📚 Courses", "View available campus courses"));
        contentPanel.add(createInfoCard("📰 Announcements", "Stay updated with latest news"));
        contentPanel.add(createInfoCard("👤 Profile", "View or edit your profile"));

        add(contentPanel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footer = new JLabel("© 2025 Campus Forge. All Rights Reserved.", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footer.setForeground(new Color(120, 120, 120));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);

        // (Optional) Fetch and display extra data
        loadUserData();
    }

    private JPanel createInfoCard(String title, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));

        JLabel descLabel = new JLabel("<html><p style='width:200px;'>" + desc + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 100, 100));

        JButton actionBtn = new JButton("Open");
        actionBtn.setBackground(new Color(52, 152, 219));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        actionBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionBtn.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionBtn.setBackground(new Color(52, 152, 219));
            }
        });

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);
        textPanel.add(actionBtn, BorderLayout.SOUTH);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String info = rs.getString("username");
                welcomeLabel.setText("Welcome, " + info + " 👋");
            }
        } catch (SQLException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }
}
