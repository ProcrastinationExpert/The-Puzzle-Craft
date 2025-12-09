package gameLogic;

import java.util.HashSet;
import java.util.Set;

public class Global {
    public static int layer = 0;
    public static int select = 0;
    public static int[] layers1;
    public static int[] layers2;
    public static boolean Keys = true;
    public static boolean Falls = true;
    public static boolean Sw1 = true;
    public static boolean Sw2 = true;
    public static boolean Sw3 = true;
    public static Set<Integer> Collided = new HashSet<>();
    public static Set<Integer> Ladder = new HashSet<>();
    public static Set<Integer> Switch1 = new HashSet<>();
    public static Set<Integer> Switch2 = new HashSet<>();
    public static Set<Integer> Switch3 = new HashSet<>();
    public static Set<Integer> Trig1 = new HashSet<>();
    public static Set<Integer> Trig2 = new HashSet<>();
    public static Set<Integer> Trig3 = new HashSet<>();
    public static int end = 0;
}