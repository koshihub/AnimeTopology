
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;


class Main {
	static CanvasPanel canvasPanel;

	public static void main(String args[]) {
		JFrame frame = new JFrame("(・ω・)");

		// event
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		
		// main panel
		canvasPanel = new CanvasPanel();
		
		// setting panel
		Action act = new Action();
		Change change = new Change();
		Panel settingPanel = new Panel();
		Button button = new Button("Open");
		Checkbox check1 = new Checkbox("T-Junction");
		Checkbox check2 = new Checkbox("AreaID");
		check1.setState(true);
		check2.setState(true);
		settingPanel.setLayout(new FlowLayout());
		settingPanel.add(button);
		settingPanel.add(check1);
		settingPanel.add(check2);
		button.addActionListener(act);
		check1.addItemListener(change);
		check2.addItemListener(change);
	    
		frame.setLayout(new BorderLayout());
		frame.add("Center", canvasPanel);
		frame.add("North", settingPanel);
		frame.setBounds(100, 100, 800, 1000);
		frame.setVisible(true);
	}

	public static class Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String label = e.getActionCommand();
			if( label.equals("Open") ) {
				JFileChooser filechooser = new JFileChooser(new File(".").getAbsolutePath() + "/images");
			    if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			    	canvasPanel.openImage(filechooser.getSelectedFile());
			    }
			}
		}
	}
	
	public static class Change implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Checkbox ch = (Checkbox)e.getItemSelectable();
			if( ch.getLabel().equals("T-Junction") ) {
				canvasPanel.setJunctionFlag(ch.getState());
			}
			else if( ch.getLabel().equals("AreaID") ) {
				canvasPanel.setAreaIAFlag(ch.getState());
			}
		}
	}
}
