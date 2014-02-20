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
	
	public Area(int w, int h, int ID) {
		pixels = new ArrayList<Pixel>();
		width = w;
		height = h;
		areaID = ID;
		depth = -1.0;
		buf = null;
		
		// decide color
		color = new Color(
				(int)(Math.random()*255),
				(int)(Math.random()*255),
				(int)(Math.random()*255));
		//color = Color.cyan;
	}
	
	public void draw(Graphics g, int _x, int _y, boolean drawAreaIDFlag) {
		// draw area
		if( buf != null ) {
			g.drawImage(buf, x+_x, y+_y, null);
		}
		
		// draw areaID
		if( drawAreaIDFlag ) {
			g.setColor(Color.red);
			g.drawString("[" + areaID + "]", x+_x, y+_y+20);		
		}
	}
	
	public void prepareImage() {
		int minx = width, miny = height, maxx = 0, maxy = 0;
		
		// find position and size
		for(Pixel p : pixels) {
			// find min, max point
			if( p.x < minx ) minx = p.x;
			if( p.y < miny ) miny = p.y;
			if( p.x > maxx ) maxx = p.x;
			if( p.y > maxy ) maxy = p.y;
		}
		
		// set position and size
		x = minx;
		y = miny;
		width = maxx - minx + 1;
		height = maxy - miny + 1;

		// create buffer
		buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// prepare image
		for(Pixel p : pixels) {
			int posx = p.x - minx, posy = p.y - miny;
			if( p.border ) {
				buf.setRGB(posx, posy, Color.black.getRGB());
			} else {
				if( depth < 0.0 ) {
					buf.setRGB(posx, posy, color.getRGB());
				} else {
					int val = (int)(depth * 255);
					buf.setRGB(posx, posy, new Color(val, val, val).getRGB());
				}
			}
		}
	}
	
	public void addPixel(Pixel p) {
		// add pixel
		pixels.add(p);
	}
	
	public void deleteBorder(Pixel p) {
		buf.setRGB(p.x, p.y, color.getRGB());
	}
	
	public Pixel getInnerPixel() {
		Pixel ret = null;
		for(Pixel p : pixels) {
			if( !p.border ) {
				ret = p;
				break;
			}
		}
		return ret;
	}
}