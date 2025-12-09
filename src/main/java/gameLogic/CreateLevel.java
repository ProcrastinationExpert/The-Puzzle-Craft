package gameLogic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class CreateLevel {

    public static void createData(String name, String Username) {

        try {
            // CEK TERLEBIH DAHULU APAKAH SUDAH ADA
            URL checkUrl = new URL("https://pokemon-5d1b3-default-rtdb.asia-southeast1.firebasedatabase.app/" 
                                    + name + ".json");

            HttpURLConnection checkConn = (HttpURLConnection) checkUrl.openConnection();
            checkConn.setRequestMethod("GET");
            checkConn.setConnectTimeout(3000);
            checkConn.setReadTimeout(3000);

            int checkCode = checkConn.getResponseCode();

            // Ambil data GET
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(checkConn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            checkConn.disconnect();

            // Jika data tidak null → sudah ada
            if (!sb.toString().equals("null")) {
                JOptionPane.showMessageDialog(null,
                        "Level \"" + name + "\" sudah ada!\nSilakan gunakan nama lain.",
                        "Level Sudah Ada",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // === JIKA BELUM ADA, LANJUT BUAT LEVEL BARU ===

            int[] layer1 = new int[312];
            int[] layer2 = new int[312];
            Arrays.fill(layer1, 0);
            Arrays.fill(layer2, 0);

            String json = "{"
                    + "\"username\": \"" + Username + "\","
                    + "\"layer1\": " + Arrays.toString(layer1) + ","
                    + "\"layer2\": " + Arrays.toString(layer2)
                    + "}";

            HttpURLConnection conn = (HttpURLConnection) checkUrl.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = conn.getResponseCode();
            conn.disconnect();

            if (code >= 200 && code < 300) {
                JOptionPane.showMessageDialog(null,
                        "Level berhasil dibuat!",
                        "Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Gagal membuat level.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke server.",
                    "Koneksi Gagal",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
