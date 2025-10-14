package com.loginapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageUsersPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel model;
    private JButton markBtn, deleteBtn, refreshBtn, showMarkedBtn;

    public ManageUsersPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // ===== Table Setup =====
        String[] columns = {"User ID", "Username", "Marked"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(model);
        userTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // ===== Top Panel =====
        JPanel topPanel = new JPanel();
        JLabel title = new JLabel("Manage Users");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.add(title);

        // ===== Buttons =====
        JPanel btnPanel = new JPanel();
        markBtn = new JButton("Mark/Unmark");
        deleteBtn = new JButton("Delete Users");
        refreshBtn = new JButton("Refresh");
        showMarkedBtn = new JButton("Show Marked");

        for (JButton b : new JButton[]{markBtn, deleteBtn, refreshBtn, showMarkedBtn}) btnPanel.add(b);

        // ===== Add Layout =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== Load all users =====
        loadAllUsers();

        // ===== Button Actions =====
        refreshBtn.addActionListener(e -> loadAllUsers());
        markBtn.addActionListener(e -> toggleMark());
        deleteBtn.addActionListener(e -> deleteSelectedUsers());
        showMarkedBtn.addActionListener(e -> showMarkedUsers());
    }

    // ===== Load all users from DB =====
    private void loadAllUsers() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, username, marked FROM users")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String marked = rs.getInt("marked") == 1 ? "Yes" : "No";
                model.addRow(new Object[]{id, username, marked});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    // ===== Mark/Unmark users =====
    private void toggleMark() {
        int[] selected = userTable.getSelectedRows();
        if (selected.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one user to mark/unmark.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE users SET marked = ? WHERE id = ?")) {

            for (int row : selected) {
                int userId = (int) model.getValueAt(row, 0);
                String status = (String) model.getValueAt(row, 2);
                int newMark = status.equals("No") ? 1 : 0;

                ps.setInt(1, newMark);
                ps.setInt(2, userId);
                ps.executeUpdate();

                model.setValueAt(newMark == 1 ? "Yes" : "No", row, 2);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating marked users: " + ex.getMessage());
        }
    }

    // ===== Delete selected users =====
    private void deleteSelectedUsers() {
        int[] selected = userTable.getSelectedRows();
        if (selected.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one user to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete selected users?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id = ?")) {

            for (int row : selected) {
                int userId = (int) model.getValueAt(row, 0);
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Users deleted successfully.");
            loadAllUsers();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting users: " + ex.getMessage());
        }
    }

    // ===== Show only marked users =====
    private void showMarkedUsers() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, username, marked FROM users WHERE marked = 1")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                model.addRow(new Object[]{id, username, "Yes"});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error showing marked users: " + ex.getMessage());
        }
    }
}
