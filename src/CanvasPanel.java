
import java.awt.*;
import java.awt.event.*;
import java.io.File;


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
	
	public void openImage(File file) {
		depthImage = new DepthImage(file);
		repaint();
	}
	
	public void resize() {
		width = getSize().width;
		height = getSize().height;
		buf = createImage(width, height);
	}
	
	public void repaint(Graphics g) {
		paint(g);
	}

	public void paint(Graphics bg) {
		if( buf == null ) {
			buf = createImage(width, height);
		} else {
			Graphics g = buf.getGraphics();
			
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			
			if( depthImage != null ) {
				depthImage.draw(g);
			}
			
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