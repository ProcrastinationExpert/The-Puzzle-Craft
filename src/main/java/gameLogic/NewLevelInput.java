package gameLogic;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class NewLevelInput {

    // === FONT PIXEL EMBEDDED (tanpa file eksternal) ===
    private static Font pixelFont = loadPixelFont();

    private static Font loadPixelFont() {
        try {
            String base64 = ""
                    + "AAEAAAALAIAAAwAwT1MvMggjlgAAAVAAAAYGNtYXABdS/fAAABgAAAADBnbHlm"
                    + "MdiS6AAAAbgAAABQaGVhZADzAP8AAAHYAAAANmhoZWEDsQEeAAACMAAAACRobXR4"
                    + "AAMAAAAAAIoAAAAIGG1heHAAEwAqAAACqAAAACBuYW1lECE9TgAAArwAAAUucG9z"
                    + "dAAAAAGwAAAAg3ByZXAAHwAAAAAEkAAAACBwcmVwAAEAAAAA";

            byte[] data = Base64.getDecoder().decode(base64);
            Font f = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
            return f.deriveFont(14f);
        } catch (Exception e) {
            return new Font("Monospaced", Font.BOLD, 14);
        }
    }

    public static void show(String Username) {

        // Theme warna pixel
        Color bg = new Color(255, 255, 255);
        Color text = new Color(0, 0, 0);
        Color accent = new Color(0, 0, 0);

        JFrame frame = new JFrame("Buat Level Baru");
        frame.setSize(350, 190);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(bg);

        // Label
        JLabel label = new JLabel("Masukkan nama level baru (tanpa spasi):");
        label.setBounds(20, 20, 320, 20);
        label.setForeground(text);
        label.setFont(pixelFont);
        frame.add(label);

        // TextField pixel
        JTextField inputField = new JTextField();
        inputField.setBounds(20, 50, 300, 30);
        inputField.setBackground(new Color(30, 30, 30));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createLineBorder(accent, 2));
        inputField.setFont(pixelFont);
        frame.add(inputField);

        // === BUTTON BERGAMBAR 16px → 32px + efek klik ===
        ImageIcon yesIcon = new ImageIcon(
                NewLevelInput.class.getResource("/assets/images/levelAssets/yes.png")
        );

        Image yesScaled = yesIcon.getImage().getScaledInstance(
                yesIcon.getIconWidth() * 2,
                yesIcon.getIconHeight() * 2,
                Image.SCALE_DEFAULT
        );
        ImageIcon yesIcon2x = new ImageIcon(yesScaled);

        // Icon klik
        ImageIcon yesClickIcon = new ImageIcon(
                NewLevelInput.class.getResource("/assets/images/levelAssets/yesclick.png")
        );

        Image yesClickScaled = yesClickIcon.getImage().getScaledInstance(
                yesClickIcon.getIconWidth() * 2,
                yesClickIcon.getIconHeight() * 2,
                Image.SCALE_DEFAULT
        );
        ImageIcon yesClickIcon2x = new ImageIcon(yesClickScaled);

        // JButton icon pixel
        JButton okBtn = new JButton(yesIcon2x);
        okBtn.setBounds(160, 100, yesIcon2x.getIconWidth(), yesIcon2x.getIconHeight());
        okBtn.setBorder(null);
        okBtn.setContentAreaFilled(false);
        okBtn.setFocusPainted(false);

        // Efek klik: ganti gambar
        okBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                okBtn.setIcon(yesClickIcon2x);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                okBtn.setIcon(yesIcon2x);
            }
        });

        // Event OK
        okBtn.addActionListener(e -> {
            String levelName = inputField.getText().trim();

            if (levelName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Nama level tidak boleh kosong.");
                return;
            }

            if (levelName.contains(" ")) {
                JOptionPane.showMessageDialog(frame, "Nama level tidak boleh mengandung SPASI.");
                return;
            }

            CreateLevel.createData(levelName, Username);
            System.out.println("Nama level baru: " + levelName);

            frame.dispose();
        });

        frame.add(okBtn);

        frame.setVisible(true);
    }
}
