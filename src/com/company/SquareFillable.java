package com.company;

import javax.swing.*;

public class SquareFillable extends Square
{
    public SquareFillable(int x, int y, char s)
    {
        textField = new JFormattedTextField(Main.createFormatter("U")); //make the JFormattedTextField for this particular square
        textField.setBounds(30 + x * 30, 30 + y * 30, 30, 30); //set its coordinates
        textField.setHorizontalAlignment(JFormattedTextField.CENTER); //center that text
        Main.panel.add(textField); //slap the text field into the JPanel
        letter = 32; //32 == ' '
        letterSolution = s;
        num = 0;
    }

    public SquareFillable(int x, int y, char s, int n)
    {
        textField = new JFormattedTextField(Main.createFormatter("U")); //see
        textField.setBounds(30 + x * 30, 30 + y * 30, 30, 30);          //
        textField.setHorizontalAlignment(JFormattedTextField.CENTER);   //above
        JLabel label = new JLabel(String.valueOf(n)); //create a JLabel of this square's number
        label.setSize(10, 10); //size the label
        label.setBounds(20 + x * 30, 20 + y * 30, 20 * String.valueOf(num).length(), 20); //set the label's coordinates directly up and to the left of its corresponding JFormattedTextField
        Main.panel.add(textField); //see above
        Main.panel.add(label); //add number label to JPanel
        letter = 32;
        letterSolution = s;
        num = n;
    }

    public String toString()
    {
        return String.valueOf(letterSolution);
    }
}
