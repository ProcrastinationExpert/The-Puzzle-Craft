package controller;

import model.UserAccountDescriptionData;

public interface DatabaseService {
    boolean claimUsername(String idToken, String uid, String username, String email);
    boolean isUsernameTaken(String username);
    boolean saveNewUser(String idToken, String uid, String username, String email);
    String getEmailFromUsername(String username);
    boolean updateUserLastPlayed(String idToken, String uid);
    boolean updateUserLastLevel(String idToken, String uid, String levelName);
    UserAccountDescriptionData getUserData(String idToken, String uid);
    boolean updateUserScore(String idToken, String uid, int newScore);
}
