package gameLogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

public class ReadData {

    public static int[] getLayerData(String name, int layer) {
        try {
            String firebaseURL = "https://pokemon-5d1b3-default-rtdb.asia-southeast1.firebasedatabase.app/" + name + "/layer" + layer + ".json";
            URL url = new URL(firebaseURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();

            String json = response.toString().trim();

            json = json.substring(1, json.length() - 1);

            String[] values = json.split(",");

            int[] result = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = Integer.parseInt(values[i].trim());
            }
            System.out.println("Nice");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghubungkan", "Koneksi gagal", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Pengambilan data gagal");
            return new int[0]; // return array kosong kalau gagal
            
        }
    }
}
