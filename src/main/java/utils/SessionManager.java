/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author User
 */

import controller.AuthResponse;

/**
 * Menyimpan data sesi pengguna setelah login berhasil.
 */
public final class SessionManager {

    // Data disimpan di sini
    private static String idToken;
    private static String uid;
    private static String username;

    private SessionManager() {}

    public static void startDummySessionManager(String idToken, String uid, String username) {
        SessionManager.idToken = idToken;
        SessionManager.uid = uid;
        SessionManager.username = username;
    }


    public static void startSession(AuthResponse response, String username) {
        SessionManager.idToken = response.getIdToken();
        SessionManager.uid = response.getLocalId();
        SessionManager.username = username;
    }

    // Dipanggil saat logout.
    public static void clearSession() {
        SessionManager.idToken = null;
        SessionManager.uid = null;
        SessionManager.username = null;
    }

    public static String getIdToken() {
        return idToken;
    }

    public static String getUid() {
        return uid;
    }
    
    public static String getUsername() {
        return username;
    }

    public static boolean isSessionActive() {
        return idToken != null;
    }
}
