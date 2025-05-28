package com.rentacar.observer;

import java.util.ArrayList;
import java.util.List;

public class FeedbackNotificationService implements NotificationSubject {
    private final List<NotificationObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }

    // Feedback geldiğinde çağrılacak örnek metot
    public void newFeedback(String feedbackMessage) {
        notifyObservers("Yeni feedback: " + feedbackMessage);
    }
} 