
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
		Panel settingPanel = new Panel();
		Button button = new Button("Open");
		settingPanel.setLayout(new FlowLayout());
		settingPanel.add(button);
		button.addActionListener(act);
	    
		frame.setLayout(new BorderLayout());
		frame.add("Center", canvasPanel);
		frame.add("North", settingPanel);
		frame.setBounds(100, 100, 550, 550);
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
}
