package com.company;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Main
{
    public static ArrayList<Word> origAcross;
    public static ArrayList<Word> origDown;
    public static ArrayList<Clue> clueAcross;
    public static ArrayList<Clue> clueDown;
    public static Board board;
    public static JFrame frame;
    public static JPanel panel;
    public static JLabel result;
    public static JLabel[] clues;
    public static JButton checkAnswers;

    public static void main(String[] args) throws FileNotFoundException
    {
        File file = new File("words.txt");
        Scanner sc = new Scanner(file);
        ArrayList<Word> across = new ArrayList<>();
        ArrayList<Word> down = new ArrayList<>();
        boolean acrossDone = false;
        sc.useDelimiter("\t|\n");
        while(sc.hasNextLine())
        {
            if(!acrossDone)
            {
                String word = sc.next();
                if(word.equalsIgnoreCase(""))
                {
                    acrossDone = true;
                    continue;
                }
                String clue = sc.next();
                across.add(new Word(word, clue));
            }
            else
            {
                String word = sc.next();
                String clue = sc.next();
                down.add(new Word(word, clue));
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
        char[][] letters = new char[50][50];
        for(int i = 0; i < letters.length; i++)
        {
            for(int j = 0; j < letters[i].length; j++)
            {
                letters[i][j] = ' ';
            }
        }

        for(int i = 0; i < across.get(0).word.length(); i++)
        {
            letters[25][25 + i] = across.get(0).word.charAt(i);
        }
        across.remove(0);

        if(!makeBoard(across, down, letters))
        {
            System.out.println("Board could not be made.");
            System.exit(0);
        }

        int minWidth = 50;
        int minHeight = 50;
        int maxWidth = 0;
        int maxHeight = 0;
        for(int i = 0; i < letters.length; i++)
        {
            for(int j = 0; j < letters[i].length; j++)
            {
                if(letters[i][j] != ' ')
                {
                    if(j < minWidth)
                        minWidth = j;
                    if(j > maxWidth)
                        maxWidth = j;
                    if(i < minHeight)
                        minHeight = i;
                    if(i > maxHeight)
                        maxHeight = i;
                }
            }
        }
        char[][] condensedLetters = new char[maxHeight - minHeight + 1][maxWidth - minWidth + 1];
        for(int i = 0; i < condensedLetters.length; i++)
        {
            for(int j = 0; j < condensedLetters[i].length; j++)
            {
                condensedLetters[i][j] = letters[i + minHeight][j + minWidth];
            }
        }

        int maxClueSize = 0;
        for(Word w : origAcross)
        {
            if(w.clue.length() > maxClueSize)
                maxClueSize = w.clue.length();
        }
        for(Word w : origDown)
        {
            if(w.clue.length() > maxClueSize)
                maxClueSize = w.clue.length();
        }

        frame = new JFrame("Crossword");
        panel = new JPanel();
        panel.setLayout(null);
        checkAnswers = new JButton("Check");
        checkAnswers.addActionListener(new action());
        checkAnswers.setBounds(30, 30 + condensedLetters.length * 30 + 30, 75, 30);
        result = new JLabel("");
        result.setBounds(120, 30 + condensedLetters.length * 30 + 30, 60, 30);
        panel.add(result);
        panel.add(checkAnswers);

        board = new Board(condensedLetters);
        System.out.println(board);

        clueAcross = new ArrayList<>();
        clueDown = new ArrayList<>();
        for(int i = 0; i < board.board.length; i++)
        {
            for(int j = 0; j < board.board[i].length; j++)
            {
                if(board.board[i][j] instanceof SquareFillable && board.board[i][j].num != 0)
                {
                    String s1 = compileWord(condensedLetters, i, j, true);
                    String s2 = compileWord(condensedLetters, i, j, false);
                    if(s1.length() > 1)
                    {
                        for(Word w : origAcross)
                        {
                            if(w.word.equalsIgnoreCase(s1))
                                clueAcross.add(new Clue(board.board[i][j].num, w.clue));
                        }
                    }
                    if(s2.length() > 1)
                    {
                        for(Word w : origDown)
                        {
                            if(w.word.equalsIgnoreCase(s2))
                                clueDown.add(new Clue(board.board[i][j].num, w.clue));
                        }
                    }
                }
            }
        }

        String label = "Across:\n";
        for(Clue c : clueAcross)
        {
            label += c.num + ": " + c.clue + "\n";
        }
        label += "Down:";
        for(Clue c : clueDown)
        {
            label += "\n" + c.num + ": " + c.clue;
        }

        clues = new JLabel[2 + origAcross.size() + origDown.size()];
        clues[0] = new JLabel("Across:");
        clues[0].setBounds(30 + condensedLetters[0].length * 30 + 30, 60, (maxClueSize + 3) * 10, 15);
        panel.add(clues[0]);
        for(int i = 1; i < clues.length; i++)
        {
            if(i <= clueAcross.size())
                clues[i] = new JLabel(clueAcross.get(i - 1).num + ": " + clueAcross.get(i - 1).clue);
            else if(i == clueAcross.size() + 1)
                clues[i] = new JLabel("Down: ");
            else
                clues[i] = new JLabel(clueDown.get(i - clueAcross.size() - 2).num + ": " + clueDown.get(i - clueAcross.size() - 2).clue);
            clues[i].setBounds(30 + condensedLetters[0].length * 30 + 30, 60 + i * 15, (maxClueSize + 3) * 10, 15);
            panel.add(clues[i]);
        }


        frame.add(panel);

        frame.setSize(30 + condensedLetters[0].length * 30 + 30 + (maxClueSize + 3) * 10, 30 + condensedLetters.length * 30 + 90);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static boolean makeBoard(ArrayList<Word> a, ArrayList<Word> d, char[][] c) //place word, then next word, so on, if word fails to place, back up one step
    {
        char[][] board = deepCopy(c);
        top:
        for(int h = 0; h < a.size(); h++)
        {
            Word temp = a.get(h);
            int wordIndex = a.indexOf(temp);
            a.remove(a.get(h));
            for(int i = 0; i < c.length; i++)
            {
                for(int j = 0; j < c[0].length; j++)
                {
                    int count = 0;
                    for(int k = 0; k < temp.word.length(); k++)
                    {
                        if(temp.word.charAt(k) == c[i][j])
                            count++;
                    }
                    board:
                    for(int k = 0; k < count; k++)
                    {
                        int index = nthIndexOf(k + 1, temp.word, c[i][j]);
                        for(int l = index - 1; l >= 0; l--)
                        {
                            if(j - index + l >= 0 && c[i][j - index + l] == ' ')
                            {
                                c[i][j - index + l] = temp.word.charAt(l);
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                                continue board;
                            }
                        }
                        for(int l = index + 1; l < temp.word.length(); l++)
                        {
                            if(j - index + l < c[i].length && c[i][j - index + l] == ' ')
                            {
                                c[i][j - index + l] = temp.word.charAt(l);
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                                continue board;
                            }
                        }
                        if(checkLegal(c))
                            if(makeBoard(a, d, c))
                                break top;
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                            }
                        else
                        {
                            System.arraycopy(board, 0, c, 0, c.length);
                            board = deepCopy(c);
                        }
                    }
                }
            }
            a.add(wordIndex, temp);
        }

        top:
        for(int h = 0; h < d.size(); h++)
        {
            Word temp = d.get(h);
            int wordIndex = d.indexOf(temp);
            d.remove(d.get(h));
            for(int i = 0; i < c.length; i++)
            {
                for(int j = 0; j < c[0].length; j++)
                {
                    int count = 0;
                    for(int k = 0; k < temp.word.length(); k++)
                    {
                        if(temp.word.charAt(k) == c[i][j])
                            count++;
                    }
                    board:
                    for(int k = 0; k < count; k++)
                    {
                        int index = nthIndexOf(k + 1, temp.word, c[i][j]);
                        for(int l = index - 1; l >= 0; l--)
                        {
                            if(i - index + l >= 0 && c[i - index + l][j] == ' ')
                            {
                                c[i - index + l][j] = temp.word.charAt(l);
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                                continue board;
                            }
                        }
                        for(int l = index + 1; l < temp.word.length(); l++)
                        {
                            if(i - index + l < c.length && c[i - index + l][j] == ' ')
                            {
                                c[i - index + l][j] = temp.word.charAt(l);
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                                continue board;
                            }
                        }
                        if(checkLegal(c))
                            if(makeBoard(a, d, c))
                                break top;
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                            }
                        else
                        {
                            System.arraycopy(board, 0, c, 0, c.length);
                            board = deepCopy(c);
                        }
                    }
                }
            }
            d.add(wordIndex, temp);
        }

        if(a.size() > 0 || d.size() > 0) //revert changes if no more words fit but words left
        {
            System.arraycopy(board, 0, c, 0, c.length);
            return false;
        }

        return true;
    }
    
    public static int nthIndexOf(int n, String w, char c) //returns index of the nth occurrence
    {
        int count = 0;
        for(int i = 0; i < w.length(); i++)
        {
            if(w.charAt(i) == c)
                count++;
            if(count == n)
                return i;
        }
        return -1;
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

    public static String compileWord(char[][] c, int i, int j, boolean across) //combines characters starting at i,j to create word for legal check
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

    public static ArrayList<Word> deepCopy(ArrayList<Word> a) //creates a deep copy of an arraylist<word>
    {
        ArrayList<Word> ret = new ArrayList<>();
        for(int i = 0; i < a.size(); i++)
        {
            ret.add(new Word(a.get(i).word, a.get(i).clue));
        }
        return ret;
    }

    public static char[][] deepCopy(char[][] c) //creates a deep copy of a char[][]
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

    public static MaskFormatter createFormatter(String s)
    {
        MaskFormatter formatter = null;
        try
        {
            formatter = new MaskFormatter(s);
        }
        catch(java.text.ParseException e)
        {
            System.out.println("no gud");
            System.exit(-1);
        }
        return formatter;
    }

    public static class action implements ActionListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            for(Square[] row : board.board)
                for(Square s : row)
                {
                    s.update();
                    if(s.letter != s.letterSolution)
                    {
                        result.setText("Incorrect");
                        return;
                    }
                }
            result.setText("Correct");
        }
    }
}
