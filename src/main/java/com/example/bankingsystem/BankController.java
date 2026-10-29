package com.example.bankingsystem;

import java.math.BigDecimal;

public final class BankController {
    private final BankModel model;
    private final BankView view;
    public BankController(BankModel model, BankView view) {
        this.model = model; this.view = view;
        view.account.addActionListener(e -> { view.modelChanged(); view.message("Account selected.", false); });
        view.deposit.addActionListener(e -> execute("Deposit"));
        view.withdraw.addActionListener(e -> execute("Withdrawal"));
        view.transfer.addActionListener(e -> execute("Transfer"));
    }
    private void execute(String action) {
        try {
            String input = view.amount.getText().trim();
            if (!input.matches("[0-9]+(\\.[0-9]{1,2})?"))
                throw new IllegalArgumentException("Enter a positive number with at most two decimal places.");
            BigDecimal amount = new BigDecimal(input);
            switch (action) {
                case "Deposit" -> model.deposit(view.selectedAccount(), amount);
                case "Withdrawal" -> model.withdraw(view.selectedAccount(), amount);
                case "Transfer" -> model.transfer(view.selectedAccount(), (AccountId) view.destination.getSelectedItem(), amount);
                default -> throw new IllegalArgumentException("Unknown action.");
            }
            view.amount.setText(""); view.message(action + " completed successfully.", false);
        } catch (IllegalArgumentException e) { view.message(e.getMessage(), true); }
    }
}
