package com.example.bankingsystem;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankModelTest {
    @Test
    void hasFourNamedAccounts() {
        BankModel model = new BankModel();
        assertEquals(4, model.accounts().size());
        assertEquals("Mohammed", model.holder(new AccountId("100001")));
        assertEquals("Abdualaziz", model.holder(new AccountId("100002")));
        assertEquals("Rayan", model.holder(new AccountId("100003")));
        assertEquals("Bassam", model.holder(new AccountId("100004")));
    }

    @Test
    void equalIdentifiersRetrieveTheSameAccount() {
        AccountId first = new AccountId("100001");
        AccountId second = new AccountId("100001");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(new BankModel().balance(first), new BankModel().balance(second));
    }

    @Test
    void transferUpdatesBothAccountsAndNotifiesOnce() {
        BankModel model = new BankModel();
        AccountId from = new AccountId("100003"), to = new AccountId("100004");
        BigDecimal beforeFrom = model.balance(from), beforeTo = model.balance(to);
        BigDecimal amount = new BigDecimal("25.00");
        int[] events = {0};
        model.addListener(() -> events[0]++);
        model.transfer(from, to, amount);
        assertEquals(beforeFrom.subtract(amount), model.balance(from));
        assertEquals(beforeTo.add(amount), model.balance(to));
        assertEquals(1, model.history(from).size());
        assertEquals(1, model.history(to).size());
        assertEquals(1, events[0]);
    }

    @Test
    void rejectedTransferDoesNotChangeState() {
        BankModel model = new BankModel();
        AccountId from = new AccountId("100001"), to = new AccountId("100002");
        BigDecimal beforeFrom = model.balance(from), beforeTo = model.balance(to);
        assertThrows(IllegalArgumentException.class,
                () -> model.transfer(from, to, beforeFrom.add(BigDecimal.ONE)));
        assertEquals(beforeFrom, model.balance(from));
        assertEquals(beforeTo, model.balance(to));
        assertTrue(model.history(from).isEmpty());
        assertTrue(model.history(to).isEmpty());
    }
}
