package sisdisper.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI {
   private JFrame mainFrame;
   private JPanel controlPanel;
   private JTextPane txtpnStarting;
   private JTable table;

   public GUI(){
      prepareGUI();
      GUI swingControlDemo = new GUI();  
      swingControlDemo.showEventDemo();     
   }
   
   private void prepareGUI(){
      mainFrame = new JFrame("Java SWING Examples");
      mainFrame.setTitle("MMORG");
      mainFrame.setAlwaysOnTop(true);
      mainFrame.setSize(532,631);
      
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
      mainFrame.getContentPane().setLayout(null);
      
      txtpnStarting = new JTextPane();
      txtpnStarting.setBounds(0, 0, 516, 175);
      txtpnStarting.setForeground(Color.BLACK);
      txtpnStarting.setBackground(Color.WHITE);
      mainFrame.getContentPane().add(txtpnStarting);
      
      table = new JTable();
      table.setBounds(20, 175, 79, 132);
      table.setModel(new DefaultTableModel(
      	new Object[][] {
      		{null, null, null, null},
      		{null, null, null, null},
      		{null, null, null, null},
      		{null, null, null, null},
      	},
      	new String[] {
      		"New column", "New column", "New column", "New column"
      	}
      ));
      mainFrame.getContentPane().add(table);
      controlPanel = new JPanel();
      controlPanel.setBounds(0, 396, 516, 217);
      mainFrame.getContentPane().add(controlPanel);
      mainFrame.setVisible(true);  
   }
   private void showEventDemo(){
      JButton stoptButton = new JButton("Stop");
      stoptButton.setBounds(10, 22, 93, 53);
      JButton restartButton = new JButton("Restart");
      restartButton.setBounds(10, 86, 93, 53);
      restartButton.setActionCommand("Restart");
      stoptButton.addActionListener(new ButtonClickListener()); 
      restartButton.addActionListener(new ButtonClickListener());
      controlPanel.setLayout(null);
      controlPanel.add(stoptButton);
      
      JButton okButton = new JButton("UP");
      okButton.setBounds(273, 28, 93, 38);
      
            okButton.setActionCommand("OK");
            
                  okButton.addActionListener(new ButtonClickListener()); 
                  
                        controlPanel.add(okButton);
      controlPanel.add(restartButton);       
      
      JButton btnDown = new JButton("RIGHT");
      btnDown.setActionCommand("OK");
      btnDown.setBounds(375, 77, 93, 38);
      controlPanel.add(btnDown);
      
      JButton button = new JButton("DOWN");
      button.setActionCommand("OK");
      button.setBounds(273, 77, 93, 38);
      controlPanel.add(button);
      
      JButton btnLeft = new JButton("LEFT");
      btnLeft.setActionCommand("OK");
      btnLeft.setBounds(170, 77, 93, 38);
      controlPanel.add(btnLeft);
      
      JButton btnBomb = new JButton("BOMB");
      btnBomb.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      	}
      });
      btnBomb.setActionCommand("OK");
      btnBomb.setBounds(180, 125, 267, 38);
      controlPanel.add(btnBomb);
      txtpnStarting.setText("Starting...");
      mainFrame.setVisible(true);  
   }
   
   private class ButtonClickListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();  
         
         if( command.equals( "OK" ))  {
           
         } else if( command.equals( "Submit" ) )  {
           
         } else {
            
         }  	
      }		
   }
}
