package com.rentacar.strategy;

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardHolder;
    private String expiry;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cardHolder, String expiry, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiry = expiry;
        this.cvv = cvv;
    }

    @Override
    public void pay(double amount) {
        System.out.println(amount + " TL kredi kartı ile ödendi. [Kart: " + cardNumber + "]");
    }
} 