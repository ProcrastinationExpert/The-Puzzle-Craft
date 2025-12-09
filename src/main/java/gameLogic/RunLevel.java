package gameLogic;

import main.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class RunLevel {

    public static Launcher currentLauncher;

    public static JFrame runningLevelFrame;

    public static void build(Create create, int[] Lay1, int[] Lay2, String username, Launcher launcher) {

        currentLauncher = launcher;
        
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
        
        // === BUAT FRAME BARU ===
        runningLevelFrame = new JFrame("Play Level : " + username);
        runningLevelFrame.setSize(1552, 871);
        runningLevelFrame.setLayout(null);
        runningLevelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        runningLevelFrame.setLocationRelativeTo(null);
        runningLevelFrame.setVisible(true);

        runningLevelFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                launcher.updateDashboard();
                runningLevelFrame.dispose();
            }
        });


        int[] tile1 = Lay1;
        int[] tile2 = Lay2;
        
        
        for (int i = 0; i < tile2.length; i++) {
            //System.out.println(tile2[i]);
            if (tile2[i] == 0) {
            
            }
            else if (tile2[i] == 101) {
            
            }
            else if (tile2[i] == 100) {
            Global.end = i;
            }
            else if (tile2[i] == 25) {
            Global.Switch1.add(i);
            }
            else if (tile2[i] == 26) {
            Global.Switch2.add(i);
            }
            else if (tile2[i] == 27) {
            Global.Switch3.add(i);
            }
            else if (tile2[i] == 28) {
            Global.Trig1.add(i);
            }
            else if (tile2[i] == 29) {
            Global.Trig2.add(i);
            }
            else if (tile2[i] == 30) {
            Global.Trig3.add(i);
            }
            else if (tile2[i] == 31) {
            Global.Ladder.add(i);
            }
            else {
            Global.Collided.add(i);
            }
        }
        
        for (int n : Global.Collided) {
            System.out.println(n);
        }
        
        for (int i = 0; i < tile2.length; i++) {
            int x = i % 24;
            int y = i / 24;
            if (tile2[i] == 0){
                
            } 
            else if (tile2[i] == 101) {
                Player p = new Player(runningLevelFrame, x, y, 101);
            } else if (tile2[i] == 28) {
                create.Tiles(runningLevelFrame, x, y, 110, 28, 1);
            } else if (tile2[i] == 29) {
                create.Tiles(runningLevelFrame, x, y, 111, 29, 2);
            } else if (tile2[i] == 30) {
                create.Tiles(runningLevelFrame, x, y, 112, 30, 3);
            } else {
                create.Tiles(runningLevelFrame, x, y, tile2[i]);
            }
        }
        
        for (int i = 0; i < tile1.length; i++) {
            int x = i % 24;
            int y = i / 24;
            if (tile1[i] == 0){
            } 
            else {
                create.Tiles(runningLevelFrame, x, y, tile1[i]);
            }
        }
        runningLevelFrame.setVisible(true);
        runningLevelFrame.revalidate();
        runningLevelFrame.repaint();
        
    }
}
