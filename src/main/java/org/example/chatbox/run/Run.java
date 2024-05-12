package org.example.chatbox.run;

import org.example.chatbox.login.Login;

import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new Login().setVisible(true);
        });
    }
}
