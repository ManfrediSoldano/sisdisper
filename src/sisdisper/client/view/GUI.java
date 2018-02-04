package sisdisper.client.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import sisdisper.client.BufferController;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.MoveCLI;

public class GUI {
	private JFrame mainFrame;
	private JPanel controlPanel;
	private JTextPane txtpnStarting;
	private JTable table;
	private Integer dim = null;
	private int x =0;
	private int y=0;
	private UserObservable observable=null;
	public boolean live = false;
	public UserObservable getObservable() {
		return observable;
	}

	public void setObservable(UserObservable observable) {
		this.observable = observable;
	}

	public GUI() {
		
		
	}
	
	
	public void setDim(Integer dim) {
		this.dim = dim;
	}

	public void startGUI(){
		if(BufferController.mygame!=null){
			prepareGUI(BufferController.mygame.getDimension());
			showEventDemo();
			live=true;
		}
	}

	private void prepareGUI(int dim) {
		dim++;
		mainFrame = new JFrame("MMORG");
		mainFrame.setTitle("MMORG");
		mainFrame.setAlwaysOnTop(true);
		mainFrame.setSize(532, 631);

		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
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
		table.setBounds(160, 185, 200, 200);
		table.setModel(new DefaultTableModel(dim, dim));
		table.setRowHeight(200 / dim);

		mainFrame.getContentPane().add(table);

		controlPanel = new JPanel();
		controlPanel.setBounds(0, 396, 516, 217);
		mainFrame.getContentPane().add(controlPanel);
		mainFrame.setVisible(true);
		this.dim = dim;
	}

	private void showEventDemo() {
		controlPanel.setLayout(null);

		JButton stoptButton = new JButton("Stop");
		stoptButton.setBounds(10, 22, 93, 53);
		stoptButton.setActionCommand("STOP");
		stoptButton.addActionListener(new ButtonClickListener());
		controlPanel.add(stoptButton);
		
		JButton restartButton = new JButton("Restart");
		restartButton.setBounds(10, 86, 93, 53);
		restartButton.setActionCommand("RESTART");
		restartButton.addActionListener(new ButtonClickListener());
		controlPanel.add(restartButton);


		JButton upButton = new JButton("UP");
		upButton.setBounds(273, 28, 93, 38);
		upButton.setActionCommand("UP");
		upButton.addActionListener(new ButtonClickListener());
		controlPanel.add(upButton);

		JButton btnRight = new JButton("RIGHT");
		btnRight.setActionCommand("RIGHT");
		btnRight.setBounds(375, 77, 93, 38);
		btnRight.addActionListener(new ButtonClickListener());
		controlPanel.add(btnRight);

		JButton buttonDown = new JButton("DOWN");
		buttonDown.setActionCommand("DOWN");
		buttonDown.setBounds(273, 77, 93, 38);
		buttonDown.addActionListener(new ButtonClickListener());
		controlPanel.add(buttonDown);

		JButton btnLeft = new JButton("LEFT");
		btnLeft.setActionCommand("LEFT");
		btnLeft.setBounds(170, 77, 93, 38);
		btnLeft.addActionListener(new ButtonClickListener());
		controlPanel.add(btnLeft);

		JButton btnBomb = new JButton("BOMB");
		btnBomb.setActionCommand("BOMB");
		btnBomb.setBounds(180, 125, 267, 38);
		btnBomb.addActionListener(new ButtonClickListener());
		controlPanel.add(btnBomb);
		
		txtpnStarting.setText("Starting...");
		mainFrame.setVisible(true);
	}

	public void addText(String text){
		String[] lines = txtpnStarting.getText().split("\r\n");
		   	
		if(lines.length>10) {
			txtpnStarting.setText("");
		}
		txtpnStarting.setText(txtpnStarting.getText().concat(System.lineSeparator().concat(text)));
	}
	
	public void move(int x, int y) {
		y++;
		if (x >= 0 && x <= dim && dim-y >= 0 && dim-y <= dim) {
			
			table.setValueAt("X", dim-y, x);
			
			table.setValueAt("", this.y, this.x);
			
			this.x=x;
			this.y=dim-y;
			
		}
	}
	public void bomb(int x, int y) {
		if (x >= 0 && x <= dim && y >= 0 && y <= dim) {
			table.setValueAt("O", y, x);
		}
	}
	
	public void explodedBomb(int x, int y) {
		if (x >= 0 && x <= dim && y >= 0 && y <= dim) {
			table.setValueAt("", y, x);
		}
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MoveCLI movecli = new MoveCLI();
			String command = e.getActionCommand();
			switch(command){
			case "STOP":
				break;
			case "RESTART":
				break;
			case "UP":
				movecli.setWhere(MoveCLI.Where.UP);
				observable.setActionChanged(movecli);
				break;
			case "RIGHT":
				movecli.setWhere(MoveCLI.Where.RIGHT);
				observable.setActionChanged(movecli);
				break;
			case "DOWN":
				movecli.setWhere(MoveCLI.Where.DOWN);
				observable.setActionChanged(movecli);
				break;
			case "LEFT":
				movecli.setWhere(MoveCLI.Where.LEFT);
				observable.setActionChanged(movecli);
				break;
			case "BOMB":
				Bomb bomb = new Bomb();
				observable.setActionChanged(bomb);
				break;
			
			default:
				break;
			}
			
		}
	}
}
