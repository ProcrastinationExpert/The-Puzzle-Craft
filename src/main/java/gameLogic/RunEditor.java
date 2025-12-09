package gameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RunEditor {

    public static void build(Create create, String name, int[] Lay1, int[] Lay2, String Username, String acces) {

        Global.layer = 0;
        Global.select = 0;
        Global.layers1 = null;
        Global.layers2 = null;
        Global.Keys = true;
        Global.Falls = true;
        Global.Sw1 = true;
        Global.Sw2 = true;
        Global.Sw3 = true;
        Global.Collided.clear();
        Global.Ladder.clear();
        Global.Switch1.clear();
        Global.Switch2.clear();
        Global.Switch3.clear();
        Global.Trig1.clear();
        Global.Trig2.clear();
        Global.Trig3.clear();
        Global.end = 0;
        
        // Cek akses
        String allowedUsername = acces;
        if (!allowedUsername.equals(Username)) {
            JOptionPane.showMessageDialog(
                null,
                "Username tidak memiliki akses ke editor!",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // === BUAT FRAME BARU ===
        JFrame editorFrame = new JFrame("Level Editor : " + name);
        editorFrame.setSize(1552, 871);
        editorFrame.setLayout(null);
        editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editorFrame.setLocationRelativeTo(null);
        editorFrame.setVisible(true);

        // Set data
        Global.layers1 = Lay1;
        Global.layers2 = Lay2;
        Global.layer = 1;

        // Icon tombol layer
        ImageIcon normalLayerIcon = resizeIcon("src/main/resources/assets/images/levelAssets/layer1.png", 156, 44);
        ImageIcon pressedLayerIcon = resizeIcon("src/main/resources/assets/images/levelAssets/layer2.png", 156, 44);

        JButton btnLayer = new JButton(normalLayerIcon);
        btnLayer.setBounds(1300, 100, 156, 44);
        btnLayer.setBorderPainted(false);
        btnLayer.setContentAreaFilled(false);
        btnLayer.setFocusPainted(false);

        // Efek klik
        btnLayer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { btnLayer.setIcon(pressedLayerIcon); }
            @Override
            public void mouseReleased(MouseEvent e) { btnLayer.setIcon(normalLayerIcon); }
        });

        // Action tombol layer
        btnLayer.addActionListener(e -> {
            editorFrame.getContentPane().removeAll();
            Global.select = 0;

            Global.layer = (Global.layer == 1) ? 2 : 1;

            if (Global.layer == 2) {
                for (int i = 0; i < Global.layers2.length; i++) {
                    int x = i % 24, y = i / 24;
                    create.Collider(editorFrame, x, y, Global.layers2[i], 2);
                }
            }

            for (int i = 0; i < Global.layers1.length; i++) {
                int x = i % 24, y = i / 24;
                create.Collider(editorFrame, x, y, Global.layers1[i], 1);
            }

            if (Global.layer == 2)
                create.buatHighlight(editorFrame, "select2", -20, 608);
            else
                create.buatHighlight(editorFrame, "select2", 1420, 704);

            // Palette baris 1
            for (int i = 0; i < 16; i++) {
                int x = 1 + (i * 2), y = 14;
                create.buatImage(editorFrame, "select1", x, y);
                create.Select(editorFrame, x, y, Global.layer == 2 ? i : (i + 32));
            }

            // Palette baris 2
            for (int i = 0; i < 16; i++) {
                int x = 1 + (i * 2), y = 16;
                create.buatImage(editorFrame, "select1", x, y);
                create.Select(editorFrame, x, y, Global.layer == 2 ? (i + 16) : (i + 48));
            }

            if (Global.layer == 2) {
                create.Select(editorFrame, 28, 8, 100);
                create.buatImage(editorFrame, "select3", 28, 8);

                create.Select(editorFrame, 28, 5, 101);
                create.buatImage(editorFrame, "select3", 28, 5);
            }

            create.addSaveButton(editorFrame, name, Username);

            editorFrame.add(btnLayer);
            editorFrame.revalidate();
            editorFrame.repaint();
        });

        editorFrame.add(btnLayer);

        // Render pertama
        btnLayer.doClick();
    }

    private static ImageIcon resizeIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
