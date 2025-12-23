package com.example.salonmanagementsystem.model;

import java.time.LocalDate;

public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private boolean active;

    public Employee() {
        this.hireDate = LocalDate.now();
        this.active = true;
    }

    public Employee(String firstName, String lastName, String position, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return active ? "ACTIVE" : "INACTIVE";
    }

    @Override
    public String toString() {
        return getFullName();
    }
}