package com.loginapp;

import javax.swing.*;
import java.awt.*;

public class AdminLauncher extends JFrame {

    private String username;
    private JPanel container;

    public AdminLauncher(String username) {
        this.username = username;

        setTitle("Campus Forge - Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Navigation Bar =====
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        navPanel.setBackground(Color.DARK_GRAY);

        JButton homeBtn = new JButton("Home");
        JButton searchBtn = new JButton("Search Users");
        JButton manageBtn = new JButton("Manage Users");
        JButton logoutBtn = new JButton("Logout");

        for (JButton b : new JButton[]{homeBtn, searchBtn, manageBtn, logoutBtn}) {
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
        }

        navPanel.add(homeBtn);
        navPanel.add(searchBtn);
        navPanel.add(manageBtn);
        navPanel.add(logoutBtn);
        add(navPanel, BorderLayout.NORTH);

        // ===== Panel Container =====
        container = new JPanel(new CardLayout());
        add(container, BorderLayout.CENTER);

        AdminHomePanel homePanel = new AdminHomePanel();
        AdminSearchPanel searchPanel = new AdminSearchPanel();
        ManageUsersPanel managePanel = new ManageUsersPanel();

        container.add(homePanel, "Home");
        container.add(searchPanel, "Search");
        container.add(managePanel, "Manage");

        // ===== Button Actions =====
        homeBtn.addActionListener(e -> switchPanel("Home"));
        searchBtn.addActionListener(e -> switchPanel("Search"));
        manageBtn.addActionListener(e -> switchPanel("Manage"));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginGUI(); // return to login page
        });

        setVisible(true);
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, name);
    }
}
