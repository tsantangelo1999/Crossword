package com.company;

import javax.swing.JFrame;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Main
{
    public static ArrayList<Word> origAcross;
    public static ArrayList<Word> origDown;

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
        origAcross = deepCopy(across);
        origDown = deepCopy(down);
        System.out.println(across);
        System.out.println(down);
        char[][] letters = new char[6][5];
        for(int i = 0; i < letters.length; i++)
        {
            for(int j = 0; j < letters[i].length; j++)
            {
                letters[i][j] = ' ';
            }
        }

        for(int i = 0; i < across.get(0).word.length(); i++)
        {
            letters[5][2 + i] = across.get(0).word.charAt(i);
        }
        across.remove(0);

        makeBoard(across, down, letters);

        for(int i = 0; i < letters.length; i++)
        {
            for(int j = 0; j < letters[0].length; j++)
            {
                System.out.print(letters[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void makeBoard(ArrayList<Word> a, ArrayList<Word> d, char[][] c) //place word, then next word, so on, if word fails to place, back up one step
    {
        char[][] tempBoard = deepCopy(c);
        for(int i = 0; i < a.size(); i++)
        {
            Word temp = a.get(i);
            int index = a.indexOf(temp);
            a.remove(a.get(i));
            if(addWord(temp, c, true))
            {
                makeBoard(a, d, c);
                break;
            }
            else
            {
                a.add(index, temp);
            }
        }
        for(int i = 0; i < d.size(); i++)
        {
            Word temp = d.get(i);
            int index = d.indexOf(temp);
            d.remove(d.get(i));
            if(addWord(temp, c, false))
            {
                makeBoard(a, d, c);
                break;
            }
            else
            {
                d.add(index, temp);
            }
        }
        if(a.size() > 0 || d.size() > 0) //revert changes if no more words fit but words left
            System.arraycopy(tempBoard, 0, c, 0, c.length);
    }

    public static boolean addWord(Word w, char[][] c, boolean across) //adds a word to the character array board
    {
        char[][] temp = deepCopy(c);
        for(int i = 0; i < c.length; i++)
        {
            for(int j = 0; j < c[0].length; j++)
            {
                int index = w.word.indexOf(String.valueOf(c[i][j]));
                if(index >= 0)
                {
                    for(int k = index - 1; k >= 0; k--)
                    {
                        if(across)
                        {
                            if(c[i][j - index + k] == ' ')
                            {
                                c[i][j - index + k] = w.word.charAt(k);
                            }
                            else
                            {
                                System.arraycopy(temp, 0, c, 0, c.length);
                                return false;
                            }
                        }
                        if(!across)
                        {
                            if(c[i - index + k][j] == ' ')
                            {
                                c[i - index + k][j] = w.word.charAt(k);
                            }
                            else
                            {
                                System.arraycopy(temp, 0, c, 0, c.length);
                                return false;
                            }
                        }
                    }
                    for(int k = index + 1; k < w.word.length(); k++)
                    {
                        if(across)
                        {
                            if(c[i][j - index + k] == ' ')
                            {
                                c[i][j - index + k] = w.word.charAt(k);
                            }
                            else
                            {
                                System.arraycopy(temp, 0, c, 0, c.length);
                                return false;
                            }
                        }
                        if(!across)
                        {
                            if(c[i - index + k][j] == ' ')
                            {
                                c[i - index + k][j] = w.word.charAt(k);
                            }
                            else
                            {
                                System.arraycopy(temp, 0, c, 0, c.length);
                                return false;
                            }
                        }
                    }
                    return checkLegal(c);
                }
            }
        }
        return false;
    }

    public static boolean checkLegal(char[][] c) //scan whole board, anytime a leading letter (nothing left and/or above) is found, check if made word is in list
    {
        for(int i = 0; i < c.length; i++)
        {
            for(int j = 0; j < c[i].length; j++)
            {
                if(c[i][j] != ' ' && (j == 0 || c[i][j - 1] == ' ') && !(j == c[i].length - 1 || c[i][j + 1] == ' '))
                {
                    String w = compileWord(c, i, j, true);
                    boolean legal = false;
                    for(Word word : origAcross)
                    {
                        if(w.equalsIgnoreCase(word.word))
                            legal = true;
                    }
                    if(!legal)
                        return false;
                }
                if(c[i][j] != ' ' && (i == 0 || c[i - 1][j] == ' ') && !(i == c.length - 1 || c[i + 1][j] == ' '))
                {
                    String w = compileWord(c, i, j, false);
                    boolean legal = false;
                    for(Word word : origDown)
                    {
                        if(w.equalsIgnoreCase(word.word))
                            legal = true;
                    }
                    if(!legal)
                        return false;
                }
            }
        }
        return true;
    }

    public static String compileWord(char[][] c, int i, int j, boolean across)
    {
        String word = "";
        while((i < c.length && j < c[i].length) && c[i][j] != ' ')
        {
            if(across)
            {
                word += c[i][j];
                j++;
            }
            if(!across)
            {
                word += c[i][j];
                i++;
            }
        }
        return word;
    }

    public static ArrayList<Word> deepCopy(ArrayList<Word> a)
    {
        ArrayList<Word> ret = new ArrayList<>();
        for(int i = 0; i < a.size(); i++)
        {
            ret.add(new Word(a.get(i).word));
        }
        return ret;
    }

    public static char[][] deepCopy(char[][] c)
    {
        char[][] ret = new char[c.length][c[0].length];
        for(int i = 0; i < c.length; i++)
        {
            for(int j = 0; j < c[0].length; j++)
            {
                ret[i][j] = c[i][j];
            }
        }
        return ret;
    }
}
