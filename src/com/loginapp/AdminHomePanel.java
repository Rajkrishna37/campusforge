package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AdminHomePanel extends JPanel {

    private JPanel contentPanel;

    public AdminHomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("All Registered Users");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                row.setBackground(Color.WHITE);
                JLabel label = new JLabel("👤 " + username);
                label.setFont(new Font("Arial", Font.PLAIN, 16));

                JButton viewBtn = new JButton("View Details");
                viewBtn.addActionListener(e -> new UserDetailsDialog(username));

                row.add(label);
                row.add(viewBtn);
                contentPanel.add(row);
            }

            System.out.println("🧾 Users fetched: " + users);

            if (users.isEmpty()) {
                JLabel none = new JLabel("No users found!");
                none.setFont(new Font("Arial", Font.ITALIC, 16));
                contentPanel.add(none);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
