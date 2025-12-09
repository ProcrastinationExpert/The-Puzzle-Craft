package gameLogic;

import main.Launcher;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.imageio.ImageIO;

import static main.Launcher.*;

public class LevelSelectorFrame {

    private static String selectedLevel = null;
    private static Map<String, String> allLevels; // key=levelName, value=username

    public static void show(String currentUser, Create create, Launcher launcher) {

        allLevels = fetchLevels();   // Ambil semua level dari Firebase

        JFrame frame = new JFrame("Login as " + currentUser);
        frame.setSize(450, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup(); // agar cuma bisa pilih 1 checkbox

        // === daftar checkbox hijau/merah
        for (String levelName : allLevels.keySet()) {
            String owner = allLevels.get(levelName);

            JCheckBox cb = new JCheckBox(levelName + "    (made by " + owner + ")");
            cb.setFont(new Font("Arial", Font.PLAIN, 14));
            cb.setOpaque(false);
            cb.setForeground(Color.BLACK);

            cb.setIcon(new ColorIcon(Color.RED));
            cb.setSelectedIcon(new ColorIcon(Color.GREEN));

            cb.addActionListener(e -> {
                if (cb.isSelected()) selectedLevel = levelName;
                else selectedLevel = null;
            });

            group.add(cb);
            listPanel.add(cb);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBounds(20, 20, 400, 260);
        frame.add(scroll);

        // scroll hitam-putih
        setupScrollBar(scroll.getVerticalScrollBar());
        setupScrollBar(scroll.getHorizontalScrollBar());

        // Tombol Play (image 32x32)
        JButton playBtn = createImageButton(
                "/assets/images/levelAssets/play.png",
                "/assets/images/levelAssets/playclick.png"
        );
        playBtn.setBounds(100, 300, 32*2, 32*2); // 16x16 *2
        playBtn.addActionListener(e -> {
            if (selectedLevel == null) {
                JOptionPane.showMessageDialog(frame, "Belum memilih level!");
                return;
            }
            launcher.updateLastLevelPlayedData(selectedLevel);
            launcher.updateDashboard();
            RunLevel.build(create, ReadData.getLayerData(selectedLevel, 1), ReadData.getLayerData(selectedLevel, 2), currentUser, launcher);
        });
        frame.add(playBtn);

        // Tombol Edit (image 32x32)
        JButton editBtn = createImageButton(
                "/assets/images/levelAssets/edit.png",
                "/assets/images/levelAssets/editclick.png"
        );
        editBtn.setBounds(260, 300, 32*2, 32*2);
        editBtn.addActionListener(e -> {
            if (selectedLevel == null) {
                JOptionPane.showMessageDialog(frame, "Belum memilih level!");
                return;
            }
            RunEditor.build(create, selectedLevel, ReadData.getLayerData(selectedLevel, 1), ReadData.getLayerData(selectedLevel, 2), currentUser, GetUser.getUsername(selectedLevel));
        });
        frame.add(editBtn);

        frame.setVisible(true);
    }

    // ================================================================
    // Helper: buat tombol image
    // ================================================================
    private static JButton createImageButton(String normalPath, String clickPath) {
        JButton btn = new JButton();
        try {
            BufferedImage imgNormal = ImageIO.read(LevelSelectorFrame.class.getResource(normalPath));
            BufferedImage imgClick = ImageIO.read(LevelSelectorFrame.class.getResource(clickPath));

            // scale 2x
            Image scaledNormal = imgNormal.getScaledInstance(imgNormal.getWidth()*2, imgNormal.getHeight()*2, Image.SCALE_SMOOTH);
            Image scaledClick = imgClick.getScaledInstance(imgClick.getWidth()*2, imgClick.getHeight()*2, Image.SCALE_SMOOTH);

            btn.setIcon(new ImageIcon(scaledNormal));
            btn.setPressedIcon(new ImageIcon(scaledClick));
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
        } catch (Exception e) {
            System.out.println("Error load button image: " + e.getMessage());
        }
        return btn;
    }

    // ================================================================
    // scrollbar hitam-putih
    // ================================================================
    private static void setupScrollBar(JScrollBar sb) {
        sb.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.BLACK;
                this.trackColor = Color.WHITE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setBackground(Color.WHITE);
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setBackground(Color.WHITE);
                return button;
            }
        });
    }

    // ================================================================
    // Fetch level dari Firebase
    // ================================================================
    private static Map<String, String> fetchLevels() {
        Map<String, String> result = new LinkedHashMap<>();
        String urlString = "https://pokemon-5d1b3-default-rtdb.asia-southeast1.firebasedatabase.app/.json";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            String json = sb.toString().trim();
            if (json.equals("null")) return result;

            Pattern keyPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\{");
            Matcher keyMatch = keyPattern.matcher(json);

            while (keyMatch.find()) {
                String levelName = keyMatch.group(1);
                String owner = "Unknown";

                int startPos = json.indexOf("\"username\"", keyMatch.start());
                if (startPos != -1) {
                    int colon = json.indexOf(":", startPos);
                    int quote1 = json.indexOf("\"", colon + 1);
                    int quote2 = json.indexOf("\"", quote1 + 1);
                    if (quote1 != -1 && quote2 != -1) owner = json.substring(quote1 + 1, quote2);
                }

                result.put(levelName, owner);
            }

        } catch (Exception e) {
            System.out.println("Error fetch: " + e.getMessage());
        }

        return result;
    }

    // ================================================================
    // Custom Icon untuk checkbox
    // ================================================================
    static class ColorIcon implements Icon {
        private final Color color;
        private final int size = 16;

        public ColorIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }
    }
}
