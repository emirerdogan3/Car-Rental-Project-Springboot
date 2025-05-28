package com.rentacar.observer;

public class EmailNotificationObserver implements NotificationObserver {
    private final String email;

    public EmailNotificationObserver(String email) {
        this.email = email;
    }

    @Override
    public void update(String message) {
        System.out.println("E-posta gönderildi: " + email + " | Mesaj: " + message);
    }
} 