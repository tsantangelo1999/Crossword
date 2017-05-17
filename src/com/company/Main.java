package com.company;

import javax.swing.JFrame;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException
    {
        JFrame frame = new JFrame("Crossword");
        Board board = new Board();
        File file = new File("words.txt");
        Scanner sc = new Scanner(file);
        ArrayList<String> across = new ArrayList<>();
        ArrayList<String> down = new ArrayList<>();
        boolean acrossDone = false;
        while(sc.hasNextLine())
        {
            if(!acrossDone)
                across.add(sc.nextLine());
            else
                down.add(sc.nextLine());
            if(across.get(across.size() - 1).equalsIgnoreCase(""))
            {
                acrossDone = true;
                across.remove(across.size() - 1);
            }
        }
        System.out.println(across);
        System.out.println(down);
    }
}
