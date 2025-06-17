package com.rentacar.factory;

import com.rentacar.entity.User;
import com.rentacar.entity.Manager;
import com.rentacar.entity.Employee;
import com.rentacar.entity.Role;


// Factory Pattern Kullanımı

public class UserFactory {
    public static User createUser(String username, String passwordHash, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        return user;
    }

    public static Manager createManager(User user) {
        Manager manager = new Manager();
        manager.setUser(user);
        return manager;
    }

    public static Employee createEmployee(User user) {
        Employee employee = new Employee();
        employee.setUser(user);
        return employee;
    }
} 