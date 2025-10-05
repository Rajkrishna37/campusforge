package com.loginapp;

import javax.swing.*;
import java.awt.*;

public class UserLauncher extends JFrame {

    private String username;

    public UserLauncher(String username) {
        this.username = username;

        setTitle("Campus Forge - User");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setBackground(Color.DARK_GRAY);
        JButton homeBtn = new JButton("Home");
        JButton searchBtn = new JButton("Search");
        JButton profileBtn = new JButton("Profile");
        JButton logoutBtn = new JButton("Logout");

        navPanel.add(homeBtn);
        navPanel.add(searchBtn);
        navPanel.add(profileBtn);
        navPanel.add(logoutBtn);

        add(navPanel, BorderLayout.NORTH);

        // Panel holder
        JPanel container = new JPanel(new CardLayout());
        add(container, BorderLayout.CENTER);

        // Create panels
        HomePanel homePanel = new HomePanel(username);
        SearchPanel searchPanel = new SearchPanel(username);
        ProfilePanel profilePanel = new ProfilePanel(username);

        container.add(homePanel, "Home");
        container.add(searchPanel, "Search");
        container.add(profilePanel, "Profile");

        // Navigation actions
        homeBtn.addActionListener(e -> switchPanel(container, "Home"));
        searchBtn.addActionListener(e -> switchPanel(container, "Search"));
        profileBtn.addActionListener(e -> switchPanel(container, "Profile"));
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginGUI();
        });

        setVisible(true);
    }

    private void switchPanel(JPanel container, String name){
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, name);
    }
}
