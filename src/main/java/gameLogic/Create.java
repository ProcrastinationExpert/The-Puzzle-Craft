package gameLogic;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Create {

    private void setImage(JButton btn, String imageName) {
        ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + imageName + ".png");
        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH); // langsung ukuran
        btn.setIcon(new ImageIcon(img));
    }

    public void Collider(JFrame frame, int x, int y, int type, int layer) {
        JButton btn = new JButton();
        btn.setBounds(x * 48, y * 48, 48, 48); // langsung ukuran
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);

        setImage(btn, Assets.image(type));
        // klik ubah ke gambar global
        btn.addActionListener(e -> {
        if (layer == 2)
        {   
            if (Global.select != 100 && Global.select != 101) {
            setImage(btn, Assets.image(Global.select));
            Global.layers2[y * 24 + x] = Global.select;
            int change = y * 24 + x;
            System.out.print(";lo " + Global.layers2[(y * 24) + x] + " " + change);
            } else if (Global.select == 100 && Arrays.stream(Global.layers2).anyMatch(z -> z == 100)) {
                System.out.print("100 terpakai");
            } else if (Global.select == 101 && Arrays.stream(Global.layers2).anyMatch(z -> z == 101)) {
                System.out.print("101 juga");
            } else if (Global.select == 63) {
                setImage(btn, Assets.image(0));
                Global.layers2[y * 24 + x] = 0;
                int change = y * 24 + x;
            } else {
                setImage(btn, Assets.image(Global.select));
                Global.layers2[y * 24 + x] = Global.select;
                int change = y * 24 + x;
            }
            
        }
        else if (layer == 1)
        {   
            if (Global.select == 63) {
                setImage(btn, Assets.image(0));
            Global.layers1[y * 24 + x] = 0;
            int change = y * 24 + x;
            } else {
            setImage(btn, Assets.image(Global.select));
            Global.layers1[y * 24 + x] = Global.select;
            int change = y * 24 + x;
            System.out.print(";lo " + Global.layers1[(y * 24) + x] + " " + change);}
        }
    }        
        );

        frame.add(btn);
    }
    
    public void addSaveButton(JFrame frame, String name, String Username) {
    ImageIcon normalSaveIcon = resizeIcon("src/main/resources/assets/images/levelAssets/save1.png", 128, 44);
    ImageIcon pressedSaveIcon = resizeIcon("src/main/resources/assets/images/levelAssets/save2.png", 128, 44);

    JButton saveBtn = new JButton(normalSaveIcon);
    saveBtn.setBounds(1300, 20, 128, 44);
    saveBtn.setBorderPainted(false);
    saveBtn.setContentAreaFilled(false);
    saveBtn.setFocusPainted(false);

    // Ganti gambar saat diklik
    saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            saveBtn.setIcon(pressedSaveIcon);
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            saveBtn.setIcon(normalSaveIcon);
        }
    });

    // --- Event lama tetap sama ---
    saveBtn.addActionListener(e -> {
        try {
            WriteData.sendData(name, Username);
        } catch (Exception ex) {
        }
    });

    frame.add(saveBtn);
}

// --- Tambahkan fungsi bantu ini di class yang sama ---
private static ImageIcon resizeIcon(String path, int w, int h) {
    ImageIcon icon = new ImageIcon(path);
    Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new ImageIcon(scaled);
}

    JLabel highlight; // gambar penanda

// Fungsi untuk membuat gambar highlight
    public void buatHighlight(JFrame frame, String namaGambar, int x, int y) {
    ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + namaGambar + ".png");

    Image img = icon.getImage().getScaledInstance(144, 144, Image.SCALE_SMOOTH);
    ImageIcon resized = new ImageIcon(img);

    highlight = new JLabel(resized);
    highlight.setBounds(x, y, 144, 144);
    frame.add(highlight);
}

// Fungsi utama tombol
    public void Select(JFrame frame, int x, int y, int type) {
        JButton btn = new JButton();
        btn.setBounds((x * 48) - 20, (y * 48) - 16, 48, 48);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);

        setImage(btn, Assets.image(type));

        btn.addActionListener(e -> {
            Global.select = type;

            if (highlight != null) {
                highlight.setLocation((x * 48 - 48) - 20, (y * 48 - 48) - 16);
                frame.repaint();
            }
        });

        frame.add(btn);
    }
    
        public void buatImage(JFrame frame, String namaGambar, int x, int y) { 
        ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + namaGambar + ".png");

        Image img = icon.getImage().getScaledInstance(144, 144, Image.SCALE_SMOOTH);
        ImageIcon resized = new ImageIcon(img);

        JLabel label = new JLabel(resized);
        label.setBounds(((x - 1) * 48) - 20, ((y - 1) * 48) - 16, 144, 144);

        frame.add(label);
}
        public void Tiles(JFrame frame, int xLoc, int yLoc, int type) {
        String imageName = Assets.image(type);

        ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + imageName + ".png");
        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        JLabel labelGambar = new JLabel(new ImageIcon(img));
        labelGambar.setBounds(xLoc * 64, yLoc * 64, 64, 64);

        frame.add(labelGambar);
    }
        public void Tiles(JFrame frame, int xLoc, int yLoc, int type1, int type2, int switchId) {
        JLabel labelGambar = new JLabel();
        labelGambar.setBounds(xLoc * 64, yLoc * 64, 64, 64);
        frame.add(labelGambar);

        Runnable updateImage = () -> {

            boolean state = false;

            if (switchId == 1) {
                state = Global.Sw1;
            } 
            else if (switchId == 2) {
                state = Global.Sw2;
            }
            else if (switchId == 3) {
                state = Global.Sw3;
            }

            String imageName = Assets.image(state ? type2 : type1);
            ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + imageName + ".png");
            Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            labelGambar.setIcon(new ImageIcon(img));
        };

        updateImage.run();
        Timer timer = new Timer(100, e -> updateImage.run());
        timer.start();
    }
}

