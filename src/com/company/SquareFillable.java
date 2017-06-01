package com.company;

import javax.swing.*;

public class SquareFillable extends Square
{
    int num;
    char letter;
    char letterSolution;

    public SquareFillable(int x, int y, char s)
    {
        JFormattedTextField textField = new JFormattedTextField(Main.createFormatter("U"));
        textField.setBounds(40 + x * 40, 40 + y * 40, 30, 30);
        textField.setHorizontalAlignment(JFormattedTextField.CENTER);
        Main.panel.add(textField);
        letter = 32;
        letterSolution = s;
    }

    public SquareFillable(int x, int y, char s, int n)
    {
        JFormattedTextField textField = new JFormattedTextField(Main.createFormatter("U"));
        textField.setBounds(40 + x * 40, 40 + y * 40, 30, 30);
        textField.setHorizontalAlignment(JFormattedTextField.CENTER);
        Main.panel.add(textField);
        letter = 32;
        letterSolution = s;
        num = n;
    }

    public String toString()
    {
        return String.valueOf(letterSolution);
    }
}
