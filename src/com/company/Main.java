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
        while(sc.hasNextLine()) //Populate across and down arraylists with word objects
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
            w.countConnections(down); //countConnections aids in efficiency by placing the words with the least connections first
        }
        for(Word w : down)
        {
            w.countConnections(across);
        }
        Collections.sort(across);
        Collections.sort(down);
        origAcross = deepCopy(across); //static arraylists useful in method calls and also for unmodified versions
        origDown = deepCopy(down);
        System.out.println(across); //debug feature useful for seeing all of the words and number of connections
        System.out.println(down);
        char[][] letters = new char[50][50]; //max board size is 50x50, if board cannot fit in there, board fails to be created
        for(int i = 0; i < letters.length; i++)
        {
            for(int j = 0; j < letters[i].length; j++)
            {
                letters[i][j] = ' '; //initialize all indices of letters[][] to a space
            }
        }

        for(int i = 0; i < across.get(0).word.length(); i++)
        {
            letters[25][25 + i] = across.get(0).word.charAt(i); //place first word of across wordlist at center of board, required as makeBoard method builds from existing words
        }
        across.remove(0); //remove first word from list so that it is not placed again

        if(!makeBoard(across, down, letters)) //makes the board, and if it fails, exits the program
        {
            System.out.println("Board could not be made.");
            System.exit(0);
        }

        int minWidth = 50;
        int minHeight = 50;
        int maxWidth = 0;
        int maxHeight = 0;
        for(int i = 0; i < letters.length; i++) //finds the size of the board so that it can be condensed
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
        for(int i = 0; i < condensedLetters.length; i++) //condenses the board so no empty space exists on the edges
        {
            for(int j = 0; j < condensedLetters[i].length; j++)
            {
                condensedLetters[i][j] = letters[i + minHeight][j + minWidth];
            }
        }

        int maxClueSize = 0; //max clue size necessary for JFrame clue display
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
        checkAnswers.addActionListener(new action()); //action() updates the letters of each Square object when called and updates the result JLabel
        checkAnswers.setBounds(30, 30 + condensedLetters.length * 30 + 30, 75, 30);
        result = new JLabel("");
        result.setBounds(120, 30 + condensedLetters.length * 30 + 30, 60, 30);
        panel.add(result);
        panel.add(checkAnswers);

        board = new Board(condensedLetters); //create the board object
        System.out.println(board);

        clueAcross = new ArrayList<>();
        clueDown = new ArrayList<>();
        for(int i = 0; i < board.board.length; i++) //creates the clues arraylists
        {
            for(int j = 0; j < board.board[i].length; j++)
            {
                if(board.board[i][j] instanceof SquareFillable && board.board[i][j].num != 0) //check for leading boxes (boxes with numbers)
                {
                    String s1 = compileWord(condensedLetters, i, j, true); //make word across
                    String s2 = compileWord(condensedLetters, i, j, false); //make word down
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

        clues = new JLabel[2 + origAcross.size() + origDown.size()];
        clues[0] = new JLabel("Across:");
        clues[0].setBounds(30 + condensedLetters[0].length * 30 + 30, 60, (maxClueSize + 3) * 10, 15);
        panel.add(clues[0]);
        for(int i = 1; i < clues.length; i++) //sequentially place the clues into JLabels on the board
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
        frame.setVisible(true); //make that sweet sweet window pop open so you can actually do the crossword
    }

    public static boolean makeBoard(ArrayList<Word> a, ArrayList<Word> d, char[][] c) //place word, then next word, so on, if word fails to place, back up one step
    {
        char[][] board = deepCopy(c); //make a deep copy of the current board
        top:
        for(int h = 0; h < a.size(); h++) //iterate through across words
        {
            Word temp = a.get(h); //temporarily pull the current word out of the arraylist
            int wordIndex = a.indexOf(temp);
            a.remove(a.get(h));
            for(int i = 0; i < c.length; i++)
            {
                for(int j = 0; j < c[0].length; j++)
                {
                    int count = 0;
                    for(int k = 0; k < temp.word.length(); k++) //find the amount of instances that the character at i,j appears in word
                    {
                        if(temp.word.charAt(k) == c[i][j])
                            count++;
                    }
                    board:
                    for(int k = 0; k < count; k++)
                    {
                        int index = nthIndexOf(k + 1, temp.word, c[i][j]); //find the nth of the letter at i,j in word
                        for(int l = index - 1; l >= 0; l--)
                        {
                            if(j - index + l >= 0 && (c[i][j - index + l] == ' ' || c[i][j - index + l] == temp.word.charAt(l)))
                            {
                                c[i][j - index + l] = temp.word.charAt(l); //move from index - 1 to the left and place letters
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length); //if it hits a word or the edge, reset the board to before modifications were made
                                board = deepCopy(c); //make a new deepcopy again
                                continue board; //continue iteration trying more possibilities
                            }
                        }
                        for(int l = index + 1; l < temp.word.length(); l++)
                        {
                            if(j - index + l < c[i].length && (c[i][j - index + l] == ' ' || c[i][j - index + l] == temp.word.charAt(l)))
                            {
                                c[i][j - index + l] = temp.word.charAt(l); //same as before but starting at index + 1 and going right
                            }
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length);
                                board = deepCopy(c);
                                continue board;
                            }
                        }
                        if(checkLegal(c)) //make sure the letters placed didnt lie adjacent to other letters making nonsense words
                            if(makeBoard(a, d, c)) //recursive call
                                break top; //if calling chain is returning true, break because we did it
                            else
                            {
                                System.arraycopy(board, 0, c, 0, c.length); //if it done goofed, reset board to layout at start of this particular method call and continue iterating
                                board = deepCopy(c);
                            }
                        else
                        {
                            System.arraycopy(board, 0, c, 0, c.length); //if placement was not legal, reset board to original layout and continue trying
                            board = deepCopy(c);
                        }
                    }
                }
            }
            a.add(wordIndex, temp); //readd the current word to the arraylist, as the loop finished its iteration without a recursive call
        }

        top:
        for(int h = 0; h < d.size(); h++) //exact same as before but with down words instead of across words
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
                            if(i - index + l >= 0 && (c[i - index + l][j] == ' ' || c[i - index + l][j] == temp.word.charAt(l)))
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
                            if(i - index + l < c.length && (c[i - index + l][j] == ' ' || c[i - index + l][j] == temp.word.charAt(l)))
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
                if(c[i][j] != ' ' && (j == 0 || c[i][j - 1] == ' ') && !(j == c[i].length - 1 || c[i][j + 1] == ' ')) //check for leading letter across
                {
                    String w = compileWord(c, i, j, true); //makes the word starting at i,j and moving right
                    boolean legal = false;
                    for(Word word : origAcross)
                    {
                        if(w.equalsIgnoreCase(word.word)) //check every word in original across list and if it matches one, board is still legal
                            legal = true;
                    }
                    if(!legal)
                        return false;
                }
                if(c[i][j] != ' ' && (i == 0 || c[i - 1][j] == ' ') && !(i == c.length - 1 || c[i + 1][j] == ' ')) //everything is the same as before but with down instead of across
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
        while((i < c.length && j < c[i].length) && c[i][j] != ' ') //keep moving until edge or empty space is hit
        {
            if(across)
            {
                word += c[i][j];
                j++;
            }
            if(!across) //!across == down
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

    public static MaskFormatter createFormatter(String s) //used in the JFormattedTextField
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

    public static class action implements ActionListener //used in the JButton
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
