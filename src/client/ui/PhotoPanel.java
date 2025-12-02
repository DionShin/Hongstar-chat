package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class PhotoPanel extends JPanel{
    public PhotoPanel(){
        
    	setLayout(null);
        
        JLabel ex_label = new JLabel("PhotoPanel");
    	ex_label.setBounds(0,0,100,50);
        ex_label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    	add(ex_label);


        setBackground(new Color(220, 255, 255));

        // UI

    }
}