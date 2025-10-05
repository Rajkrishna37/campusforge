package com.loginapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminManagePanel extends JPanel {

    private JPanel userListPanel;

    public AdminManagePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Manage Users");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(userListPanel);
        add(scroll, BorderLayout.CENTER);

        loadUsers();
    }

    private void loadUsers() {
        userListPanel.removeAll();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, username FROM users");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                JLabel nameLabel = new JLabel(username);
                JButton deleteBtn = new JButton("Remove");

                deleteBtn.addActionListener(e -> deleteUser(id, username));

                row.add(nameLabel);
                row.add(deleteBtn);
                userListPanel.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void deleteUser(int id, String username) {
        int confirm = JOptionPane.showConfirmDialog(this, "Remove user " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, username + " removed!");
            loadUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing user: " + e.getMessage());
        }
    }
}
