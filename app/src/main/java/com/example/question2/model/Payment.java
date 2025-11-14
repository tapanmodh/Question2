package com.example.question2.model;

public class Payment {
    public PaymentType type;
    public double amount;
    public String provider;
    public String reference;

    public Payment(PaymentType type, double amount, String provider, String reference) {
        this.type = type;
        this.amount = amount;
        this.provider = provider;
        this.reference = reference;
    }
}
