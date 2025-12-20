package com.example.salonmanagementsystem.model;

public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    private boolean active;

    public Employee() {}

    public Employee(String firstName, String lastName, String position, boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.active = active;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getPosition() {return position;}
    public void setPosition(String position) {this.position = position;}

    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                ", active=" + active +
                '}';
    }
}
