package com.example.salonmanagementsystem.model;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Role role;
    private Long employeeId;

    public User() {}

    public User(String username, String passwordHash, Role role, Long employeeId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.employeeId = employeeId;
    }

    public Long getId() {return id;}
    public void setId(Long id){this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPasswordHash() {return passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}

    public Long getEmployeeId() {return employeeId;}
    public void setEmployeeId(Long employeeId) {this.employeeId = employeeId;}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", employeeId=" + employeeId +
                '}';
    }
}




