package com.company;

import javax.swing.JFrame;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException
    {
        JFrame frame = new JFrame("Crossword");
        Board board = new Board();
        File file = new File("words.txt");
        Scanner sc = new Scanner(file);
        ArrayList<Word> across = new ArrayList<>();
        ArrayList<Word> down = new ArrayList<>();
        boolean acrossDone = false;
        while(sc.hasNextLine())
        {
            if(!acrossDone)
                across.add(new Word(sc.nextLine()));
            else
                down.add(new Word(sc.nextLine()));
            if(across.get(across.size() - 1).word.equalsIgnoreCase(""))
            {
                acrossDone = true;
                across.remove(across.size() - 1);
            }
        }
        for(Word w : across)
        {
            w.countConnections(down);
        }
        for(Word w : down)
        {
            w.countConnections(across);
        }
        Collections.sort(across);
        Collections.sort(down);
        System.out.println(across);
        System.out.println(down);
        ArrayList<ArrayList<Character>> letters = new ArrayList<>();

        while(across.size() > 0 || down.size() > 0)
        {
            if(across.get(0).connections <= down.get(0).connections)
            {

            }
        }
    }
}
