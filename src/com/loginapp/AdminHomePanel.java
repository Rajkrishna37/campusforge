package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AdminHomePanel extends JPanel {

    private JPanel contentPanel;

    public AdminHomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245)); // soft web-like background

        // ====== HEADER ======
        JLabel title = new JLabel("Admin Dashboard - All Registered Users", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        title.setForeground(new Color(33, 47, 61));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ====== CONTENT PANEL ======
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 242, 245));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // smoother scroll
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(scroll, BorderLayout.CENTER);

        loadUsers();
    }

    private void loadUsers() {
        contentPanel.removeAll();
        java.util.List<String> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, username FROM users ORDER BY id ASC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                users.add(username);

                // ====== USER CARD ======
                JPanel row = new JPanel(new BorderLayout());
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(8, 10, 8, 10),
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
                ));

                JLabel label = new JLabel("👤 " + username);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                label.setForeground(new Color(33, 47, 61));

                JButton viewBtn = createStyledButton("View Details");
                viewBtn.addActionListener(e -> new UserDetailsDialog(username));

                row.add(label, BorderLayout.WEST);
                row.add(viewBtn, BorderLayout.EAST);

                // Add slight hover effect on card
                row.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        row.setBackground(new Color(245, 247, 250));
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        row.setBackground(Color.WHITE);
                    }
                });

                contentPanel.add(Box.createVerticalStrut(10)); // spacing between cards
                contentPanel.add(row);
            }

            if (users.isEmpty()) {
                JLabel none = new JLabel("No users found!", SwingConstants.CENTER);
                none.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                none.setForeground(new Color(120, 120, 120));
                none.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                contentPanel.add(none);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ====== STYLED BUTTON METHOD ======
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

        return button;
    }
}
