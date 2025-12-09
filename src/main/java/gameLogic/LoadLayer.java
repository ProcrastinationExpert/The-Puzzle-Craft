package gameLogic;

import java.io.*;
import java.nio.file.*;

public class LoadLayer {
    public static int[] getArray(String name, int layer) throws IOException {
        var lines = Files.readAllLines(Paths.get("src/level/" + name + "/layer" + layer + ".txt"));
        int[] tiles = new int[312];

        for (int i = 0; i < 312; i++)
            tiles[i] = Integer.parseInt(lines.get(i).trim());

        return tiles;
    }

    public static int[] getCollide(String name) throws IOException {
        var lines = Files.readAllLines(Paths.get("src/level/" + name + "/layer3.txt"));
        int[] collide = new int[312];

        for (int i = 0; i < 312; i++)
            collide[i] = Integer.parseInt(lines.get(i).trim());

        return collide;
    }
}