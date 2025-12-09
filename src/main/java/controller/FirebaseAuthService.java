package controller;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import model.LoginData;
import model.RegisterData;

public class FirebaseAuthService implements AuthService {
    private final String API_KEY = "AIzaSyBbAPgBnKoMVXOaEcwshgrrbR8dD3xPhxw";
    private final String URL_REGISTER = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
    private final String URL_LOGIN = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
    private final String URL_SEND_VERIFICATION = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + API_KEY;
    private final String URL_LOOKUP = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + API_KEY;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public AuthResponse register(RegisterData regData) throws Exception {
        AuthRequest payload = new AuthRequest(regData.getEmail(), regData.getPassword());
        String jsonPayload = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_REGISTER))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("EMAIL_EXISTS_OR_WEAK_PASSWORD");
        }

        AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);
        sendVerificationEmail(authResponse.getIdToken());
        return authResponse;
    }

    @Override
    public AuthResponse login(LoginData loginData) throws Exception {
        AuthRequest payload = new AuthRequest(loginData.getEmail(), loginData.getPassword());
        String jsonPayload = gson.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_LOGIN))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("INVALID_CREDENTIALS");
        }

        AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

        try {
            String lookupPayload = "{\"idToken\":\"" + authResponse.getIdToken() + "\"}";
            HttpRequest lookupRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL_LOOKUP))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(lookupPayload))
                    .build();

            HttpResponse<String> lookupResponse = client.send(lookupRequest, HttpResponse.BodyHandlers.ofString());

            if (lookupResponse.statusCode() == 200) {
                Map<String, Object> parsedResponse = gson.fromJson(lookupResponse.body(), Map.class);
                List<Map<String, Object>> users = (List<Map<String, Object>>) parsedResponse.get("users");
                if (users != null && !users.isEmpty()) {
                    Map<String, Object> user = users.get(0);
                    boolean freshStatus = (Boolean) user.get("emailVerified");
                    authResponse.setEmailVerified(freshStatus);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal me-refresh status pengguna: " + e.getMessage());
        }

        return authResponse;
    }

    @Override
    public void sendVerificationEmail(String idToken) throws Exception {
        String jsonPayload = "{\"requestType\":\"VERIFY_EMAIL\", \"idToken\":\"" + idToken + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_SEND_VERIFICATION))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("FAILED_TO_SEND_VERIFICATION");
        }
        System.out.println("Verification link has been sent to user email.");
    }

    @Override
    public void sendPasswordResetEmail(String email) throws Exception {
        String jsonPayload = "{\"requestType\":\"PASSWORD_RESET\", \"email\":\"" + email + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_SEND_VERIFICATION))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("EMAIL_NOT_FOUND");
        }
        System.out.println("Success sent request reset password to user email");
    }
}
