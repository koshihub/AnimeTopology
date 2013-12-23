import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Area {
	
	int width, height;
	List<Pixel> pixels;
	BufferedImage buf;
	Color color;
	
	int areaID;
	int depth;
	
	public Area(int w, int h, int ID, Color c) {
		
		pixels = new ArrayList<Pixel>();
		width = w;
		height = h;
		areaID = ID;
		depth = -1;
		
		// create buffer
		buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// decide color
		color = c;
	}
	
	public void drawArea(Graphics g) {
		g.drawImage(buf, 0, 0, null);
	}
	
	public void addPixel(Pixel p, boolean border) {
		// add pixel
		pixels.add(p);
		
		// draw pixel to buffer
		if( border ) {
			buf.setRGB(p.x, p.y, Color.black.getRGB());
		} else {
			buf.setRGB(p.x, p.y, color.getRGB());
		}
	}
	
	public void deleteBorder(Pixel p) {
		buf.setRGB(p.x, p.y, color.getRGB());
	}
}