import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Canvas {

	int width, height;
	Pixel[][] canvas;
	List<Pixel> borders;
	List<Area> areas;
	List<Junction> junctions;
	BufferedImage buf;
	
	public Canvas(int w, int h) {
		width = w;
		height = h;
		canvas = new Pixel[w][h];
		
		for(int i=0; i<w; i++)
			for(int j=0; j<h; j++)
				canvas[i][j] = new Pixel(i, j);

		borders = new ArrayList<Pixel>();
		areas = new ArrayList<Area>();
		junctions = new ArrayList<Junction>();
		buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	// draw canvas
	public void draw(Graphics g) {
		
		// draw areas
		for(int i=0; i<areas.size(); i++) {
			areas.get(i).drawArea(g);
		}
		
		// draw junctions
		for(int i=0; i<junctions.size(); i++) {
			junctions.get(i).drawJunction(g);
		}

		g.drawImage(buf, 0, 0, null);
	}
	
	// set data by line
	public void setLine(int x1, int y1, int x2, int y2) {
		int minx = Math.min(x1, x2), maxx = Math.max(x1, x2);
		int miny = Math.min(y1, y2), maxy = Math.max(y1, y2);
		double slope = (y1 - y2) / (double)(x1 - x2); 

		if( Math.abs(slope) <= 1.0 ) {
			for(int x=minx; x<maxx+1; x++) {
				int y = (slope<0.0 ? maxy : miny) + (int)Math.round((x-minx)*slope);
				
				// write border information
				canvas[x][y].border = true;
				
				// add to borders
				if( !borders.contains(canvas[x][y]) ) {
					borders.add(canvas[x][y]);
				}
				
				buf.setRGB(x, y, Color.black.getRGB());
			}
		} else {
			slope = (x1 - x2) / (double)(y1 - y2); 
			for(int y=miny; y<maxy+1; y++) {
				int x = (slope<0.0 ? maxx : minx) + (int)Math.round((y-miny)*slope);
				
				// write border information
				canvas[x][y].border = true;

				// add to borders
				if( !borders.contains(canvas[x][y]) ) {
					borders.add(canvas[x][y]);
				}
				
				buf.setRGB(x, y, Color.black.getRGB());
			}
		}
	}
	
	// connect a border pixel to neighbors
	private void connetBorderPixel() {
		int[][][] dir = {
				{{-1,0},{1,0},{0,-1},{0,1}},	// +
				{{-1,-1},{1,1},{-1,1},{1,-1}}	// ×
		};
		
		// + directions
		for(int b=0; b<borders.size(); b++) {
			Pixel current = borders.get(b);
			current.connect.clear();
			
			for(int i=0; i<4; i++) {
				int xx = current.x+dir[0][i][0], yy = current.y+dir[0][i][1];
	
				if( !(xx < 0 || xx >= width || yy < 0 || yy >= height) ) {
					Pixel neighbor = canvas[xx][yy];
					if( neighbor.border ) {
						if( !neighbor.connect.contains(current) ) {
							neighbor.connect.add(current);
						}
						if( !current.connect.contains(neighbor) ) {
							current.connect.add(neighbor);
						}
					}
				}
			}
		}

		// × directions
		for(int b=0; b<borders.size(); b++) {
			Pixel current = borders.get(b);
			
			for(int i=0; i<4; i++) {
				int xx = current.x+dir[1][i][0], yy = current.y+dir[1][i][1];
	
				if( !(xx < 0 || xx >= width || yy < 0 || yy >= height) ) {
					Pixel neighbor = canvas[xx][yy];
					if( neighbor.border ) {
						if( examineIndirectConnection(neighbor, current) == 0 ) {
							if( !neighbor.connect.contains(current) ) {
								neighbor.connect.add(current);
							}
							if( !current.connect.contains(neighbor) ) {
								current.connect.add(neighbor);
							}
						}
					}
				}
			}
		}
		
	}
	
	// examine if two pixels are connected indirectly
	private int examineIndirectConnection(Pixel p1, Pixel p2) {

		int indirectConnections = 0;
		
		for(int k=0; k<p1.connect.size(); k++) {
			for(int l=0; l<p2.connect.size(); l++) {
				if( p1.connect.get(k) == p2.connect.get(l) ) {
					indirectConnections ++;
				}
			}
		}
		
		return indirectConnections;
	}
	
	// delete pixel
	public void deleteBorder(Pixel p) {
		
		int areaID = -1;
		int x = p.x, y = p.y;
		
		// disconnect
		for(int i=-1; i<2; i++) {
			for(int j=-1; j<2; j++) {
				if( i==0 && j==0 ) {
					continue;
				}
				
				int xx = i+x, yy = j+y;
				if( !(xx < 0 || xx >= width || yy < 0 || yy >= height) ) {
					if( canvas[xx][yy].border ) {
						// disconnect
						canvas[xx][yy].connect.remove(p);
					}
					else {
						// propagate areaID
						areaID = canvas[xx][yy].areaID;
					}
				}
			}
		}
		
		// delete data
		borders.remove(p);
		buf.setRGB(x, y, 0);
		for(int i=0; i<areas.size(); i++) {
			if( areas.get(i).pixels.contains(p) ) {
				areas.get(i).deleteBorder(p);
			}
		}
		canvas[x][y] = new Pixel(x, y);
		canvas[x][y].areaID = areaID;
	}
	
	// analyze areas
	public void analyzeAreas() {
		
		int areaIndex = 1;
		boolean[][] flag = new boolean[width][height];
		
		areas.clear();

		// connect pixels
		connetBorderPixel();
		
		// separate areas 
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				// find undecided area
				if( !flag[i][j] && !canvas[i][j].border ) {
					// new area
					Area area = new Area(width, height, areaIndex, 
							new Color(
									(int)(Math.random()*255),
									(int)(Math.random()*255),
									(int)(Math.random()*255)));
					
					// start filling
					Stack<Point> stack = new Stack<Point>();
					stack.push(new Point(i,j));
					
					while( !stack.empty() ) {
						Point p = stack.pop();
						
						// out of bounds
						if( p.x < 0 || p.x >= width || p.y < 0 || p.y >= height ) {
							continue;
						}
						
						// border
						if( canvas[p.x][p.y].border ) {
							flag[p.x][p.y] = true;
							area.addPixel(canvas[p.x][p.y], true);
							continue;
						}
						
						// else
						if( !flag[p.x][p.y] ) {
							flag[p.x][p.y] = true;
							canvas[p.x][p.y].areaID = areaIndex;
							area.addPixel(canvas[p.x][p.y], false);
							
							int[][] offset = {{-1,0}, {1,0}, {0,-1}, {0,1}};
							for(int off=0; off<offset.length; off++) {
								stack.push(new Point(p.x+offset[off][0], p.y+offset[off][1]));
							}
						}
					}
					
					// append new area
					areas.add(area);
					
					// next groupIndex
					areaIndex ++;
				}
			}
		}

		// set border separateAreaIDs
		for(int b=0; b<borders.size(); b++ ) {
			Pixel p = borders.get(b);
			p.separateAreaIDs.clear();
			
			for(int i=-1; i<2; i++) {
				for(int j=-1; j<2; j++) {
					if( i==0 && j==0 ) {
						continue;
					}
					
					int xx = i+p.x, yy = j+p.y;
					if( !(xx < 0 || xx >= width || yy < 0 || yy >= height) ) {
						if( !canvas[xx][yy].border ) {
							if( !p.separateAreaIDs.contains(canvas[xx][yy].areaID) ) {
								p.separateAreaIDs.add(canvas[xx][yy].areaID);
							}
						}
					}
				}
			}
			
			// if a border is totally surrounded by borders, it will be done afterwards. 
			if( p.separateAreaIDs.size() == 0 ) {
				borders.remove(p);
				borders.add(p);
				b--;
			}
			else {
				// delete unnecessary border
				if( p.separateAreaIDs.size() < 2 ) {
					deleteBorder(p);
					b--;
				}
				
				// clear canvas
				buf.setRGB(p.x, p.y, 0);
			}
		}
		
		// find junctions
		junctions.clear();
		for(Pixel p : borders) {
			if( p.connect.size() == 3 ) {
				junctions.add(new Junction(p));
			}
		}
		
		// propagate depth
		Hierarchy hi = new Hierarchy();
		for(Junction j : junctions) {
			hi.addHierarchy(j.front, j.back[0]);
			hi.addHierarchy(j.front, j.back[1]);
		}
		
		System.out.println("finish" + hi.hierarchy);
	}
}
