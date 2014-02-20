package img;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageUtility{
	public static int a(int c){
		return c>>>24;
	}
	public static int r(int c){
		return c>>16&0xff;
	}
	public static int g(int c){
		return c>>8&0xff;
	}
	public static int b(int c){
		return c&0xff;
	}
	public static int rgb(int r,int g,int b){
		return 0xff000000 | r <<16 | g <<8 | b;
	}
	public static int argb(int a,int r,int g,int b){
		return a<<24 | r <<16 | g <<8 | b;
	}
	public static int luminance(int c) {
		return (int)(0.298912 * r(c) + 0.586611 * g(c) + 0.114478 * b(c));
	}
	
	public static BufferedImage medianFilter(BufferedImage origin) {
		int w = origin.getWidth();
		int h = origin.getHeight();
		int n = 9;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for(int x=0; x<w; x++) {
			for(int y=0; y<h; y++) {
				List<Integer> candidate = new ArrayList<Integer>();
				for(int i=x-n/2; i<=x+n/2; i++) {
					for(int j=y-n/2; j<=y+n/2; j++) {
						if(i >= 0 && i < w && j >= 0 && j < h) {
							candidate.add(luminance(origin.getRGB(i, j)));
						}
					}
				}
				Collections.sort(candidate);
				int l = Math.abs(
						candidate.get(candidate.size()/2) - 
						luminance(origin.getRGB(x, y)));
				
				if( true ) {
					if( l < 4 ) {
						image.setRGB(x, y, Color.white.getRGB());
					} 
				}
				else {
					image.setRGB(x, y, argb(0,l,l,l));
				}
			}
		}
		
		return image;
	}
}