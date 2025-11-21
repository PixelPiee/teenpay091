package com.teenupi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private BigDecimal amount;
    private String note;
    private LocalDateTime createdAt;
    private User sender;
    private User receiver;

    public Transaction(Long id, BigDecimal amount, String note, User sender, User receiver) {
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
}
