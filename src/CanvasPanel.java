
import java.awt.*;
import java.awt.event.*;


class CanvasPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	int width, height;
	Image buf;
	DepthImage depthImage;
	
	CanvasPanel() {
		// mouse event
		MouseDispatcher mouseDispatcher = new MouseDispatcher();
		addMouseListener( mouseDispatcher );
		addMouseMotionListener( mouseDispatcher );
		addMouseWheelListener( mouseDispatcher );
		
		// resize event
		addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resize();
			}
		});
	}
	
	public void resize() {
		width = getSize().width;
		height = getSize().height;
	}
	
	public void repaint(Graphics g) {
		paint(g);
	}

	public void paint(Graphics bg) {
		if( buf == null ) {
			buf = createImage(width, height);
		} else {
			Graphics g = buf.getGraphics();
			
			g.setColor(Color.red);
			g.clearRect(0, 0, width, height);
			
			System.out.println("paint");
			bg.drawImage(buf, 0, 0, this);
		}
	}
	
	public class MouseDispatcher extends MouseAdapter {
		
		MouseDispatcher() {
		}
		
		public void mouseDragged(MouseEvent e) {
			repaint();
		}
		
		public void mouseReleased(MouseEvent e) {
		}
	}
}