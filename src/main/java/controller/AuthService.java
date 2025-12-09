package controller;

import model.LoginData;
import model.RegisterData;

public interface AuthService {
    AuthResponse register(RegisterData regData) throws Exception;
    AuthResponse login(LoginData loginData) throws Exception;
    void sendVerificationEmail(String idToken) throws Exception;
    void sendPasswordResetEmail(String email) throws Exception;
}
