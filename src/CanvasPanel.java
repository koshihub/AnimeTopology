
import java.awt.*;
import java.awt.event.*;
import java.io.File;


class CanvasPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	int width, height;
	Image buf;
	DepthImage depthImage;
	boolean drawJunctionFlag = true, drawAreaIDFlag = true;
	
	
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
		depthImage = null;
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
				depthImage.draw(g, 0, 0, drawAreaIDFlag, drawJunctionFlag);
				depthImage.drawOriginal(g, 0, 410);
			}
			
			bg.drawImage(buf, 0, 0, this);
		}
	}
	
	public void setJunctionFlag(boolean flag) {
		this.drawJunctionFlag = flag;
		repaint();
	}

	public void setAreaIAFlag(boolean flag) {
		this.drawAreaIDFlag = flag;
		repaint();
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