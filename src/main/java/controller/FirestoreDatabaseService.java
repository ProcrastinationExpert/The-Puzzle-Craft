package controller;

import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import model.FirestoreDocument;
import model.FirestoreField;
import model.UserAccountDescriptionData;

public class FirestoreDatabaseService implements DatabaseService {

    private final String FIRESTORE_BASE_URL = "https://firestore.googleapis.com/v1/projects/the-monkey-game-data/databases/(default)/documents";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public boolean claimUsername(String idToken, String uid, String username, String email) {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String url = FIRESTORE_BASE_URL + "/usernames?documentId=" + encodedUsername;

        Map<String, String> uidValue = new HashMap<>();
        uidValue.put("stringValue", uid);
        
        Map<String, String> emailValue = new HashMap<>();
        emailValue.put("stringValue", email);

        Map<String, Object> fields = new HashMap<>();
        fields.put("uid", uidValue);
        fields.put("email", emailValue);

        Map<String, Object> jsonPayloadObject = new HashMap<>();
        jsonPayloadObject.put("fields", fields);

        String jsonPayload = gson.toJson(jsonPayloadObject);

        return createDocument(idToken, url, jsonPayload);
    }
    
    @Override
    public boolean isUsernameTaken(String username) {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String url = FIRESTORE_BASE_URL + "/usernames/" + encodedUsername;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 404) {
                return false;
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Failed to check username (HTTP " + response.statusCode() + "): " + response.body(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return true; 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true; 
        }
    }
    
    @Override
    public boolean saveNewUser(String idToken, String uid, String username, String email) {
        String url = FIRESTORE_BASE_URL + "/users?documentId=" + uid;
        
        Map<String, String> usernameValue = new HashMap<>();
        usernameValue.put("stringValue", username);

        Map<String, String> emailValue = new HashMap<>();
        emailValue.put("stringValue", email);
        
        Map<String, String> lastPlayedValue = new HashMap<>();
        lastPlayedValue.put("stringValue", "Never");

        Map<String, String> lastLevelValue = new HashMap<>();
        lastLevelValue.put("stringValue", "unknown");
        
        Map<String, String> scoreValue = new HashMap<>();
        scoreValue.put("integerValue", "0");

        Map<String, Object> fields = new HashMap<>();
        fields.put("username", usernameValue);
        fields.put("email", emailValue);
        fields.put("lastPlayed", lastPlayedValue);
        fields.put("lastLevel", lastLevelValue);
        fields.put("score", scoreValue);

        Map<String, Object> jsonPayloadObject = new HashMap<>();
        jsonPayloadObject.put("fields", fields);

        String jsonPayload = gson.toJson(jsonPayloadObject);
        
        return createDocument(idToken, url, jsonPayload);
    }

    private boolean createDocument(String idToken, String url, String jsonPayload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                System.err.println("Firestore write failed (HTTP " + response.statusCode() + "): " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updateDocument(String idToken, String url, String jsonPayload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                System.err.println("Firestore PATCH failed (HTTP " + response.statusCode() + "): " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String getEmailFromUsername(String username) {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            String url = FIRESTORE_BASE_URL + "/usernames/" + encodedUsername;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return null;
            }

            Map<String, Object> document = gson.fromJson(response.body(), Map.class);
            Map<String, Object> fields = (Map<String, Object>) document.get("fields");
            Map<String, String> emailField = (Map<String, String>) fields.get("email");
            return emailField.get("stringValue");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean updateUserLastPlayed(String idToken, String uid) {
        String url = FIRESTORE_BASE_URL + "/users/" + uid + "?updateMask.fieldPaths=lastPlayed";

        Instant instant = Instant.now();
        ZoneId userZone = ZoneId.systemDefault();
        ZonedDateTime localTime = instant.atZone(userZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        String timeNowString = formatter.format(localTime);

        Map<String, String> lastPlayedValue = new HashMap<>();
        lastPlayedValue.put("stringValue", timeNowString);

        Map<String, Object> fields = new HashMap<>();
        fields.put("lastPlayed", lastPlayedValue);

        Map<String, Object> jsonPayloadObject = new HashMap<>();
        jsonPayloadObject.put("fields", fields);

        String jsonPayload = gson.toJson(jsonPayloadObject);

        return updateDocument(idToken, url, jsonPayload);
    }
    
    @Override
    public boolean updateUserLastLevel(String idToken, String uid, String levelName) {
        String url = FIRESTORE_BASE_URL + "/users/" + uid + "?updateMask.fieldPaths=lastLevel";

        Map<String, String> lastLevelValue = new HashMap<>();
        lastLevelValue.put("stringValue", levelName);

        Map<String, Object> fields = new HashMap<>();
        fields.put("lastLevel", lastLevelValue);

        Map<String, Object> jsonPayloadObject = new HashMap<>();
        jsonPayloadObject.put("fields", fields);

        String jsonPayload = gson.toJson(jsonPayloadObject);

        return updateDocument(idToken, url, jsonPayload);
    }
    
    @Override
    public UserAccountDescriptionData getUserData(String idToken, String uid) {
        try {
            String url = FIRESTORE_BASE_URL + "/users/" + uid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to get user datas (HTTP " + response.statusCode() + "): " + response.body());
                return null;
            }

            FirestoreDocument doc = gson.fromJson(response.body(), FirestoreDocument.class);

            String username = doc.getFields().get("username").getStringValue();
            String email = doc.getFields().get("email").getStringValue();
            String lastPlayed = doc.getFields().get("lastPlayed").getStringValue();
            String lastLevel = doc.getFields().get("lastLevel").getStringValue();
            
            // Retrieve score, handling potential null for existing users
            FirestoreField scoreField = doc.getFields().get("score");
            Integer score = (scoreField != null && scoreField.getIntegerValue() != null) 
                            ? Integer.parseInt(scoreField.getIntegerValue()) 
                            : null;

            return new UserAccountDescriptionData(username, email, lastPlayed, lastLevel, score);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean updateUserScore(String idToken, String uid, int newScore) {
        String url = FIRESTORE_BASE_URL + "/users/" + uid + "?updateMask.fieldPaths=score";

        Map<String, String> scoreValue = new HashMap<>();
        scoreValue.put("integerValue", String.valueOf(newScore));

        Map<String, Object> fields = new HashMap<>();
        fields.put("score", scoreValue);

        Map<String, Object> jsonPayloadObject = new HashMap<>();
        jsonPayloadObject.put("fields", fields);

        String jsonPayload = gson.toJson(jsonPayloadObject);

        return updateDocument(idToken, url, jsonPayload);
    }
}

