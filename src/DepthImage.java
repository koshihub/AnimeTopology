import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DepthImage {

	int width, height;
	Pixel[][] canvas;
	List<Pixel> borders;
	List<Area> areas;
	List<Junction> junctions;

	public DepthImage(int w, int h) {
		width = w;
		height = h;
		canvas = new Pixel[w][h];
		
		for(int i=0; i<w; i++)
			for(int j=0; j<h; j++)
				canvas[i][j] = new Pixel(i, j);

		borders = new ArrayList<Pixel>();
		areas = new ArrayList<Area>();
		junctions = new ArrayList<Junction>();
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
	private void deleteBorder(Pixel p) {
		
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
	}
}
