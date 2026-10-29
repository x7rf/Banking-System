package com.example.bankingsystem;

import javax.swing.*;
public final class BankApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankModel model = new BankModel(); BankView view = new BankView(model);
            new BankController(model, view);
            JFrame window = new JFrame("Banking System");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); window.setContentPane(view);
            window.setSize(960, 540); window.setLocationRelativeTo(null); window.setVisible(true);
        });
    }
}
