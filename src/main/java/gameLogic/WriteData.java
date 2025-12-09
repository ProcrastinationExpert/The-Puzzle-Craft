package gameLogic;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class WriteData {
    public static void sendData(String name, String Username) {
        try {
            int[] layer1 = Global.layers1;
            int[] layer2 = Global.layers2;

            String layer1Json = Arrays.toString(layer1);
            String layer2Json = Arrays.toString(layer2);

            String json = "{"
                        + "\"username\": \"" + Username + "\","
                        + "\"layer1\": " + layer1Json + ","
                        + "\"layer2\": " + layer2Json
                        + "}";

            URL url = new URL("https://pokemon-5d1b3-default-rtdb.asia-southeast1.firebasedatabase.app/"+ name +".json");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("PUT");
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);

            // Biar tidak freeze kalau internet mati
            c.setConnectTimeout(3000);
            c.setReadTimeout(3000);

            try (OutputStream os = c.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = c.getResponseCode();
            c.disconnect();

            if (code >= 200 && code < 300) {
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Data terkirim!");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghubungkan", "Koneksi gagal", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Data gagal dikirim!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menghubungkan", "Koneksi gagal", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Data gagal dikirim!");
        }
    }
}

