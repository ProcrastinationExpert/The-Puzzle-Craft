/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author User
 */
// Class ini hanya untuk membungkus data kiriman
public class AuthRequest {
    private String email;
    private String password;
    private boolean returnSecureToken = true;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.returnSecureToken = true;
    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}