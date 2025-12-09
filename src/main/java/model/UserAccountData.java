/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */


abstract class UserAccountData {
    protected String username, password;
    UserAccountData(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        if (username.isEmpty() || username == null) throw new IllegalArgumentException("Username kosong");
        return username;
    }
    public String getPassword() {
        if (password.isEmpty() || password == null) throw new IllegalArgumentException("Password kosong");
        return password;
    }
}