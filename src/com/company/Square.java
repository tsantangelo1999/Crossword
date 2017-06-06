package com.company;

import javax.swing.*;

public class Square
{
    public JFormattedTextField textField;

    public int num;
    public char letter;
    public char letterSolution;

    public void update()
    {
        if(textField != null)
            letter = textField.getText().toLowerCase().charAt(0);
    }

    public String toString()
    {
        return " ";
    }
}
