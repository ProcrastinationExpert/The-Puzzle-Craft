package gameLogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUser {

    public static String getUsername(String levelName) {
        try {
            // URL ke level yang sama seperti CreateLevel
            URL url = new URL(
                "https://pokemon-5d1b3-default-rtdb.asia-southeast1.firebasedatabase.app/" 
                + levelName + ".json"
            );

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(3000);
            c.setReadTimeout(3000);

            BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream())
            );

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            String json = sb.toString();

            // Ambil "username":"..."
            String key = "\"username\":\"";
            int start = json.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf("\"", start);

            return json.substring(start, end);

        } catch (Exception e) {
            return null;
        }
    }
}
