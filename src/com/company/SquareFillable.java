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
        textField.setBounds(30 + x * 30, 30 + y * 30, 30, 30);
        textField.setHorizontalAlignment(JFormattedTextField.CENTER);
        Main.panel.add(textField);
        letter = 32;
        letterSolution = s;
    }

    public SquareFillable(int x, int y, char s, int n)
    {
        JFormattedTextField textField = new JFormattedTextField(Main.createFormatter("U"));
        textField.setBounds(30 + x * 30, 30 + y * 30, 30, 30);
        textField.setHorizontalAlignment(JFormattedTextField.CENTER);
        JLabel label = new JLabel(String.valueOf(n));
        label.setSize(10, 10);
        label.setBounds(20 + x * 30, 20 + y * 30, 20 * String.valueOf(num).length(), 20);
        Main.panel.add(textField);
        Main.panel.add(label);
        letter = 32;
        letterSolution = s;
        num = n;
    }

    public String toString()
    {
        return String.valueOf(letterSolution);
    }
}
