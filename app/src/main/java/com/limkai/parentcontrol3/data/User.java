package com.limkai.parentcontrol3.data;

public class User {

    private String username;
    private String email;
    private String role;
    private String profilePicture;
    private int hasChild;

    public User(){

    }

    public User(String username, String email,String role, String profilePicture, Integer hasChild) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.profilePicture = profilePicture;
        this.hasChild = hasChild;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(Integer hasChild) {
        this.hasChild = hasChild;
    }

}
