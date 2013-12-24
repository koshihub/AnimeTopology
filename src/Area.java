import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Area {
	
	int x, y, width, height;
	List<Pixel> pixels;
	BufferedImage buf;
	Color color;
	
	int areaID;
	double depth;
	
	public Area(int w, int h, int ID, Color c) {
		pixels = new ArrayList<Pixel>();
		width = w;
		height = h;
		areaID = ID;
		depth = -1.0;
		
		// create buffer
		buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// decide color
		color = c;
	}
	
	public void draw(Graphics g) {
		g.drawImage(buf, 0, 0, null);
		g.drawString("[" + areaID + "]", x, y+20);
	}
	
	public void prepareImage() {
		int minx = width, miny = height, maxx = 0, maxy = 0;
		
		for(Pixel p : pixels) {
			if( p.border ) {
				buf.setRGB(p.x, p.y, Color.black.getRGB());
			} else {
				int val = (int)(depth * 255);
				buf.setRGB(p.x, p.y, new Color(val, val, val).getRGB());
			}

			// find min, max point
			if( p.x < minx ) minx = p.x;
			if( p.y < miny ) miny = p.y;
			if( p.x > maxx ) maxx = p.x;
			if( p.y > maxy ) maxy = p.y;
		}
		
		x = minx;
		y = miny;
		width = maxx - minx;
		height = maxy - miny;
	}
	
	public void addPixel(Pixel p) {
		// add pixel
		pixels.add(p);
	}
	
	public void deleteBorder(Pixel p) {
		buf.setRGB(p.x, p.y, color.getRGB());
	}
}