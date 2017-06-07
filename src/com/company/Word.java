package com.company;

import java.util.ArrayList;

public class Word implements Comparable<Word>
{
    public final String word;
    public final String clue;

    public int connections;

    public Word(String w, String c)
    {
        word = w;
        clue = c;
        connections = 0;
    }

    public int compareTo(Word word1) //used for sorting the word arraylists in ascending connection order
    {
        if(connections < word1.connections)
            return -1;
        else if(connections > word1.connections)
            return 1;
        else
            return 0;
    }

    public void countConnections(ArrayList<Word> words) //finds the amount of words in the passed arraylist that share a common letter with this word
    {
        top: for(Word w : words)
        {
            char[] chars = w.word.toCharArray();
            for(char c : chars)
            {
                if(word.contains(String.valueOf(c)))
                {
                    connections++;
                    continue top;
                }
            }
        }
    }

    public String toString()
    {
        return word + " - Connections: " + connections;
    }
}
