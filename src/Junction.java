import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


	
public class Junction {
	
	Pixel p;
	int[][] vectors;
	int front;
	int[] back;
	
	int[][] base;
	int[] normal;
	
	public Junction(Pixel p) {
		this.p = p;
		vectors = new int[3][];
		base = new int[2][];
		back = new int[2];
		
		analyzeStructure();
	}
	
	private void analyzeStructure() {
		
		/*
		for(int i=0; i<3; i++) {
			VectorLine vl = new VectorLine(p, p.connect.get(i));
			vectors[i] = vl.vector;
		}
		*/
		
		// get three vectors
		for(int i=0; i<3; i++) {
			vectors[i] = getLine(p, p.connect.get(i));
		}
		
		// get each cosθ and find biggest one
		double[] cos = new double[3];
		double max = -1;
		int maxi = 0;
		for(int i=0; i<3; i++) {
			cos[i] = Math.abs( getCos(vectors[i], vectors[(i+1)%3]) );
			
			if(cos[i] > max) {
				max = cos[i];
				maxi = i;
			}
		}

		// save
		base[0] = vectors[maxi];
		base[1] = vectors[(maxi+1)%3];
		normal = vectors[(maxi+2)%3];
		
		// front and back information
		back[0] = p.connect.get((maxi+2)%3).separateAreaIDs.get(0);
		back[1] = p.connect.get((maxi+2)%3).separateAreaIDs.get(1);
		for(int id : p.separateAreaIDs) {
			if( id != back[0] && id != back[1] ) {
				front = id;
			}
		}
	}
	
	// get cos of two vectors
	private double getCos(int[] v1, int[] v2) {
		int a = v1[0]*v2[0] + v1[1]*v2[1];
		double b = Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1]) * Math.sqrt(v2[0]*v2[0] + v2[1]*v2[1]);
		return  a / b;
	}
	
	// get a line which starts from "origin" and direct to "dir"
	private int[] getLine(Pixel origin, Pixel dir) {
		
		Pixel current = dir, prev = origin;
		int depth = 5;
		
		for(int i=0; i<depth; i++) {
			if( current.connect.size() != 2 ) {
				// end of line
				break;
			} else {
				// line continues
				if( current.connect.get(0) == prev ) {
					prev = current;
					current = current.connect.get(1);
				} else {
					prev = current;
					current = current.connect.get(0);
				}
			}
		}
		
		return new int[]{current.x - origin.x, current.y - origin.y};
	}
	
	public void draw(Graphics g, int _x, int _y) {
		//g.drawOval(p.x-3, p.y-3, 6, 6);
		BasicStroke BStroke = new BasicStroke(3.0f);
        ((Graphics2D)g).setStroke(BStroke);
        
		// base
		g.setColor(Color.red);
		g.drawLine(p.x + _x, p.y + _y, p.x+base[0][0] + _x, p.y+base[0][1] + _y);
		g.drawLine(p.x + _x, p.y + _y, p.x+base[1][0] + _x, p.y+base[1][1] + _y);
		
		// normal
		g.setColor(Color.blue);
		g.drawLine(p.x + _x, p.y + _y, p.x+normal[0] + _x, p.y+normal[1] + _y);
	}
}