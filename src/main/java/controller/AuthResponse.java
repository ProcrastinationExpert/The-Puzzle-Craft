/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author User
 */

// Class ini untuk menangkap data balasan
public class AuthResponse {
    private String idToken;
    private String email;
    private String refreshToken;
    private String expiresIn;
    private String localId; // userID (UID)
    private boolean emailVerified;

    public String getIdToken() {
        return idToken;
    }

    public String getEmail() {
        return email;
    }

    public String getLocalId() {
        return localId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}