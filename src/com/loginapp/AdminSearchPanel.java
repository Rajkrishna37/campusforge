package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminSearchPanel extends JPanel {

    private JComboBox<String> searchTypeBox;
    private JTextField searchField;
    private JPanel resultPanel;

    public AdminSearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== Top Search Bar =====
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.add(new JLabel("Search by:"));
        searchTypeBox = new JComboBox<>(new String[]{"Name", "Skill"});
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        topPanel.add(searchTypeBox);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        // ===== Results =====
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(resultPanel);
        add(scroll, BorderLayout.CENTER);

        // ===== Button Action =====
        searchBtn.addActionListener(e -> searchUsers());
    }

    private void searchUsers() {
        resultPanel.removeAll();

        String type = (String) searchTypeBox.getSelectedItem();
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter search text!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if (type.equals("Name")) {
                ps = conn.prepareStatement("SELECT username FROM users WHERE username LIKE ?");
                ps.setString(1, "%" + query + "%");
            } else {
                ps = conn.prepareStatement("""
                        SELECT u.username 
                        FROM users u 
                        JOIN user_skills s ON u.id = s.user_id 
                        WHERE s.skill_name LIKE ?
                        """);
                ps.setString(1, "%" + query + "%");
            }

            ResultSet rs = ps.executeQuery();
            int count = 0;

            while (rs.next()) {
                String username = rs.getString("username");
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                row.setBackground(Color.WHITE);
                JLabel nameLabel = new JLabel(username);
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

                JButton viewBtn = new JButton("View");
                viewBtn.addActionListener(e -> new UserDetailsDialog(username));

                row.add(nameLabel);
                row.add(viewBtn);
                resultPanel.add(row);
                count++;
            }

            if (count == 0) {
                JLabel none = new JLabel("No users found for: " + query);
                none.setFont(new Font("Arial", Font.ITALIC, 16));
                resultPanel.add(none);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
