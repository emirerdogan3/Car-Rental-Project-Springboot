package com.rentacar.util;

// Singleton Pattern Kullanımı

public class LoggerSingleton {
    private static volatile LoggerSingleton instance;

    private LoggerSingleton() {
        // private constructor
    }

    public static LoggerSingleton getInstance() {
        if (instance == null) {
            synchronized (LoggerSingleton.class) {
                if (instance == null) {
                    instance = new LoggerSingleton();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
} 