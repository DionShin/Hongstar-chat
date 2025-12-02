package client.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class RoomPanel extends JPanel{
    public RoomPanel(){
        
    	setLayout(null);

        JLabel ex_label = new JLabel("RoomPanel");
    	ex_label.setBounds(0,0,100,50);
        ex_label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    	add(ex_label);

        setBackground(new Color(200, 235, 255));

        // UI

    }
}