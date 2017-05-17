package com.company;



public class SquareFillable extends Square
{
    int num;
    char letter;
    char letterSolution;

    public SquareFillable(char s)
    {
        letter = 0;
        letterSolution = s;
    }

    public SquareFillable(char s, int n)
    {
        letter = 0;
        letterSolution = s;
        num = n;
    }
}
