package pal;


import java.util.Arrays;

public class PalTest {

    public static void main(String[] args) {
        System.out.println(Pal.joinLast(Arrays.asList("Java", "Ruby", "Scala", "Javascript"), ", ", " and "));
        System.out.println(Pal.joinLast(Arrays.asList("Java", "Ruby"), ", ", " and "));
        System.out.println(Pal.joinLast(Arrays.asList("Java"), ", ", " and "));
        System.out.println(Pal.joinLast(Arrays.<String>asList(), ", ", " and "));
    }
}
