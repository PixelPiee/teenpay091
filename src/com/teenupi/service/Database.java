package com.teenupi.service;

import com.teenupi.model.Transaction;
import com.teenupi.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Database {
    private static Database instance;
    private List<User> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private long userIdCounter = 1;
    private long transactionIdCounter = 1;

    private Database() {
        seedData();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void seedData() {
        users.add(new User(userIdCounter++, "Yatharth", "yatharth@teen", 210, new BigDecimal("5000.00"),
                "Building cool stuff 🚀"));
        users.add(new User(userIdCounter++, "Aarav Patel", "aarav@teen", 140, new BigDecimal("1500.00"),
                "Gaming & Pizza 🍕"));
        users.add(
                new User(userIdCounter++, "Zara Khan", "zara@teen", 320, new BigDecimal("3200.00"), "Design & Art 🎨"));
        users.add(new User(userIdCounter++, "Rohan Gupta", "rohan@teen", 45, new BigDecimal("800.00"),
                "Music is life 🎧"));
    }

    public Optional<User> findUserByUpi(String upiId) {
        return users.stream().filter(u -> u.getUpiId().equals(upiId)).findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<User> getFriends(String currentUpi) {
        return users.stream().filter(u -> !u.getUpiId().equals(currentUpi)).collect(Collectors.toList());
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getRecentTransactions(int limit) {
        // Sort desc by time (newest first) - since we add to end, reverse list
        List<Transaction> reversed = new ArrayList<>(transactions);
        java.util.Collections.reverse(reversed);
        return reversed.stream().limit(limit).collect(Collectors.toList());
    }

    public synchronized void addTransaction(BigDecimal amount, String note, User sender, User receiver) {
        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Create transaction
        transactions.add(new Transaction(transactionIdCounter++, amount, note, sender, receiver));
    }
}
