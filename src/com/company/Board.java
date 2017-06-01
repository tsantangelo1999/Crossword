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
                    board[i][j] = new Square();
                }
                else
                {
                    if(((i == 0 || chars[i - 1][j] == ' ') && (i < board.length - 1 && chars[i + 1][j] != ' ')) || ((j == 0 || chars[i][j - 1] == ' ') && (j < board[i].length - 1 && chars[i][j + 1] != ' ')))
                    {
                        board[i][j] = new SquareFillable(j, i, chars[i][j], num);
                        num++;
                    }
                    else
                    {
                        board[i][j] = new SquareFillable(j, i, chars[i][j]);
                    }
                }
            }
        }
    }

    public String toString()
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
