/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class UserAccountDescriptionData {
    private String username;
    private String email;
    private String lastPlayed;
    private String lastLevel;
    private Integer score;

    public UserAccountDescriptionData(String username, String email, String lastPlayed, String lastLevel, Integer score) { // Updated constructor
        this.username = username;
        this.email = email;
        this.lastPlayed = lastPlayed;
        this.lastLevel = lastLevel;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getLastPlayed() {
        return lastPlayed;
    }

    public String getLastLevel() {
        return lastLevel;
    }
    
    public Integer getScore() { // Added getter for score
        return score;
    }
}
