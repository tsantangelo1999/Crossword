package com.company;

public class Board
{
    public Square[][] board;

    public Board(char[][] chars)
    {
        board = new Square[chars.length][chars[0].length];
        int num = 1;
        for(int i = 0; i < chars.length; i++)
        {
            for(int j = 0; j < chars[i].length; j++)
            {
                if(chars[i][j] == ' ')
                {
                    board[i][j] = new Square(); //parts of the board with no letter have just a Square object fill the array at that index
                }
                else //parts of the board with a letter have a squareFillable at their index
                {
                    if(((i == 0 || chars[i - 1][j] == ' ') && (i < board.length - 1 && chars[i + 1][j] != ' ')) || ((j == 0 || chars[i][j - 1] == ' ') && (j < board[i].length - 1 && chars[i][j + 1] != ' '))) //determine if this index contains a leading letter
                    {
                        board[i][j] = new SquareFillable(j, i, chars[i][j], num); //create a SquareFillable object with coordinates for JFrame, the proper letter, and the number
                        num++;
                    }
                    else
                    {
                        board[i][j] = new SquareFillable(j, i, chars[i][j]); //same as above but with no number (therefore this index is not a leading letter)
                    }
                }
            }
        }
    }

    public String toString() //print out the completed solution board in grid format to console
    {
        String ret = "";
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                ret += board[i][j] + " ";
            }
            ret += "\n";
        }
        return ret;
    }
}
