/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */

public class RegisterData extends UserAccountData {
    private String email;
    public RegisterData(String email, String username, String password) {
        super(username, password);
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    
}