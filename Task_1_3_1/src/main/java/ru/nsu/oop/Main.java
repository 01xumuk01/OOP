package ru.nsu.oop;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("voina-i-mir.txt");
        var a = FinderString.Find("бог", path);
        System.out.println(a);
        //System.out.println(a.size());
    }
}