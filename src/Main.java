
import java.awt.event.*;
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
		
		frame.add("Center", canvasPanel);
		frame.setBounds(100, 100, 400, 400);
		frame.setVisible(true);
	}

	public static class Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String label = e.getActionCommand();
			if( label.equals("Button") ) {
			}
		}
	}
}
