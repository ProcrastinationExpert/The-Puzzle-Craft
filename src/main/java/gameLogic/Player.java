package gameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

import static gameLogic.RunLevel.currentLauncher;
import static gameLogic.RunLevel.runningLevelFrame;


public class Player {

    private JLabel playerLabel;
    private int playerX;
    private int playerY;

    private int Location() {
        return (playerY / 64) * 24 + (playerX / 64);
    }

    private int Location(int offset) {
        return (playerY / 64) * 24 + (playerX / 64) + offset;
    }

    private boolean isEndTheGame = false;

    private void endTheGame(String playerStatus) {
        if (isEndTheGame) return;
        isEndTheGame = true;
        SwingUtilities.invokeLater(() -> {
            if (playerStatus.equals("WIN")) {
                System.out.println("Finish.");
                currentLauncher.onLevelCompleted();
                JOptionPane.showMessageDialog(null, "Selamat kamu telah menyelesaikan level ini!", "Menang", JOptionPane.INFORMATION_MESSAGE);
            } else if (playerStatus.equals("RUNAWAY")) {
                JOptionPane.showMessageDialog(null, "Oh tidak player menghilang...", "yahhh", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Pemain kabur.");
            }
            runningLevelFrame.dispose();
            currentLauncher.updateDashboard();
        });
    }

    private void checkFinish() {
        if (Location() == Global.end) {
            endTheGame("WIN");
        }
    }
    
    private void checkOutOfBounds() {
    // misal area minimal dan maksimal
    int minX = 0;
    int maxX = 23 * 64; // 24 kolom
    int minY = 0;
    int maxY = 15 * 64; // 16 baris

    if (playerX < minX || playerX > maxX || playerY < minY || playerY > maxY) {
        endTheGame("RUNAWAY");
    }
}
    
    private void moveX(int step, int fall) {
        Global.Keys = false;
        new Thread(() -> {
            try {
                playerX += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(102);
                Thread.sleep(100);

                playerX += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(101);
                Thread.sleep(100);

                playerX += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(103);
                Thread.sleep(100);

                playerX += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(101);
                Thread.sleep(100);
                checkFinish();
                checkOutOfBounds();
                Global.Keys = true;
                if (fall == 0) {
                fallDown();}
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void moveY(int step) {
        Global.Keys = false;
        new Thread(() -> {
            try {
                playerY += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(104);
                Thread.sleep(100);

                playerY += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(105);
                Thread.sleep(100);

                playerY += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(104);
                Thread.sleep(100);

                playerY += step;
                playerLabel.setLocation(playerX, playerY);
                setImage(105);
                Thread.sleep(100);
                checkFinish();
                checkOutOfBounds();
                if (step == -16) {
                Global.Keys = true;}
                if (Global.Ladder.contains(Location(0))) {
                Global.Keys = true;}
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void fallDown() {
        
        new Thread(() -> {
            Global.Keys = false;   // matikan input agar tidak spam tombol
            setImage(106);
            checkFinish();
            checkOutOfBounds();

            try {

                while (!Global.Collided.contains(Location(+24))) {

                    // cek trigger break
                    if (Global.Trig1.contains(Location(+24)) && Global.Sw1) break;
                    if (Global.Trig2.contains(Location(+24)) && Global.Sw2) break;
                    if (Global.Trig3.contains(Location(+24)) && Global.Sw3) break;

                    // toggle switch
                    if ((playerY % 64) == 0) {
                        if (Global.Switch1.contains(Location(+24))) Global.Sw1 = !Global.Sw1;
                        if (Global.Switch2.contains(Location(+24))) Global.Sw2 = !Global.Sw2;
                        if (Global.Switch3.contains(Location(+24))) Global.Sw3 = !Global.Sw3;
                    }

                    // gerakan per 16, terlihat turun smooth
                    playerY += 16;
                    setImage(106);
                    checkOutOfBounds();
                    playerLabel.setLocation(playerX, playerY);

                    Thread.sleep(100); // jeda kecil seperti animasi
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Global.Keys = true; // nyalakan input lagi

            setImage(101);
            checkFinish();
            checkOutOfBounds();
        }).start();
    }

    // -----------------------------------------
    //  CONSTRUCTOR
    // -----------------------------------------
    public Player(JFrame frame, int xLoc, int yLoc, int type) {
        if (Location() == Global.end) {
                    endTheGame("WIN");
                }
        String imageName = Assets.image(type);

        ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + imageName + ".png");
        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        playerLabel = new JLabel(new ImageIcon(img));

        playerX = xLoc * 64;
        playerY = yLoc * 64;

        playerLabel.setBounds(playerX, playerY, 64, 64);

        frame.getLayeredPane().add(playerLabel, JLayeredPane.DRAG_LAYER);

        addMovement(frame);
    }

    public void setImage(int type) {
    String imageName = Assets.image(type);
    ImageIcon icon = new ImageIcon("src/main/resources/assets/images/levelAssets/" + imageName + ".png");
    Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);

    playerLabel.setIcon(new ImageIcon(img));  // Ganti gambar saat runtime
}

    private void addMovement(JFrame frame) {

        frame.setFocusable(true);
        frame.requestFocusInWindow();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_W && Global.Keys) {
                    if (Global.Collided.contains(Location(-24))) {

                    } else if (Global.Ladder.contains(Location(-24))) {
                        moveY(-16);
                    } else {
                    }

                }
                if (key == KeyEvent.VK_S && Global.Keys) {
                    if (Global.Collided.contains(Location(+24))) {

                    } else if (Global.Trig1.contains(Location(+24)) && Global.Sw1) {

                    } else if (Global.Trig2.contains(Location(+24)) && Global.Sw2) {

                    } else if (Global.Trig3.contains(Location(+24)) && Global.Sw3) {

                    } else if (Global.Ladder.contains(Location(+24))) {
                        moveY(+16);
                    } else {
                        Global.Keys = false;
                        if (!Global.Ladder.contains(Location(+48))) {
                            if (Global.Switch1.contains(Location(+48))) {
                            if (!Global.Sw1) {Global.Sw1 = true;} else {Global.Sw1 = false;}}
                            if (Global.Switch2.contains(Location(+48))) {
                            if (!Global.Sw2) {Global.Sw2 = true;} else {Global.Sw2 = false;}}
                            if (Global.Switch3.contains(Location(+48))) {
                            if (!Global.Sw3) {Global.Sw3 = true;} else {Global.Sw3 = false;}}
                        }
                        if (!Global.Ladder.contains(Location(+24))) {
                            if (Global.Switch1.contains(Location(+24))) {
                            if (!Global.Sw1) {Global.Sw1 = true;} else {Global.Sw1 = false;}}
                            if (Global.Switch2.contains(Location(+24))) {
                            if (!Global.Sw2) {Global.Sw2 = true;} else {Global.Sw2 = false;}}
                            if (Global.Switch3.contains(Location(+24))) {
                            if (!Global.Sw3) {Global.Sw3 = true;} else {Global.Sw3 = false;}}
                            moveY(+16);
                            fallDown();
                        }
                    }
                }

                if (key == KeyEvent.VK_A && Global.Keys) {
                    if (Global.Collided.contains(Location(-1))) {

                    } else if (Global.Trig1.contains(Location(-1)) && Global.Sw1) {

                    } else if (Global.Trig2.contains(Location(-1)) && Global.Sw2) {

                    } else if (Global.Trig3.contains(Location(-1)) && Global.Sw3) {

                    } else if (Global.Ladder.contains(Location(-1))) {
                        moveX(-16, 1);

                    } else {
                        //For switching
                        if (Global.Switch1.contains(Location(-1))) {
                        if (!Global.Sw1) {Global.Sw1 = true;} else {Global.Sw1 = false;}}
                        if (Global.Switch2.contains(Location(-1))) {
                        if (!Global.Sw2) {Global.Sw2 = true;} else {Global.Sw2 = false;}}
                        if (Global.Switch3.contains(Location(-1))) {
                        if (!Global.Sw3) {Global.Sw3 = true;} else {Global.Sw3 = false;}}      
                        
                        moveX(-16, 0);
                        //fallDown();
                    }
                }

                if (key == KeyEvent.VK_D && Global.Keys) {
                    if (Global.Collided.contains(Location(+1))) {

                    } else if (Global.Trig1.contains(Location(+1)) && Global.Sw1) {

                    } else if (Global.Trig2.contains(Location(+1)) && Global.Sw2) {

                    } else if (Global.Trig3.contains(Location(+1)) && Global.Sw3) {

                    } else if (Global.Ladder.contains(Location(+1))) {
                        moveX(+16, 1);

                    } else {
                        //For switching
                        if (Global.Switch1.contains(Location(+1))) {
                        if (!Global.Sw1) {Global.Sw1 = true;} else {Global.Sw1 = false;}}
                        if (Global.Switch2.contains(Location(+1))) {
                        if (!Global.Sw2) {Global.Sw2 = true;} else {Global.Sw2 = false;}}
                        if (Global.Switch3.contains(Location(+1))) {
                        if (!Global.Sw3) {Global.Sw3 = true;} else {Global.Sw3 = false;}}
                        
                        moveX(+16, 0);
                        //fallDown();
                    }
                }
                
                if (key == KeyEvent.VK_E && Global.Keys) {
                    endTheGame("RUNAWAY");
                }

                playerLabel.setLocation(playerX, playerY);
                //System.out.println("X=" + playerX / 64 + " Y=" + playerY / 64 + " R=" + Location());
            }
        });
    }
}
