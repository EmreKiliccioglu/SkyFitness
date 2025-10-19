package com.example.skyfitness;

public class UserModel {
    private String userId;
    private String username;
    private String lastname;
    private String email;
    private String role;

    public UserModel() {
        // Firestore için boş constructor gerekir
    }

    public UserModel(String userId, String username, String lastname, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
    }

    // Getter ve Setter'lar

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
