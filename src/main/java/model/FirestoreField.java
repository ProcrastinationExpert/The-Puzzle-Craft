/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
// Class ini HANYA untuk GSON
// untuk mem-parsing {"stringValue": "..."}
public class FirestoreField {
    private String stringValue;
    private String integerValue; // Added this field

    public String getStringValue() {
        return stringValue;
    }
    
    public String getIntegerValue() { // Added this getter
        return integerValue;
    }
}