import static img.ImageUtility.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DepthImage {

	int width, height;
	Pixel[][] canvas;
	List<Pixel> borders;
	List<Area> areas;
	List<Junction> junctions;
	BufferedImage image;

	public DepthImage(File file) {
		// load image
		try {
			image = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
			image = null;
		}
		
		if( image != null ) {
			width = image.getWidth();
			height = image.getHeight();
			canvas = new Pixel[width][height];
			
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++)
					canvas[i][j] = new Pixel(i, j);

			borders = new ArrayList<Pixel>();
			areas = new ArrayList<Area>();
			junctions = new ArrayList<Junction>();
		}
		
		// median filter
		//image = medianFilter(image);
		
		// analyze borders
		analyzeBorders();
		
		// analyze areas
		analyzeAreas();
	}
	
	public void draw(Graphics g, int x, int y, boolean drawAreaIDFlag, boolean drawJunctionFlag) {
		// reset by black
		g.setColor(Color.red);
		g.fillRect(0, 0, width, height);
		
		// draw areas
		for(int i=0; i<areas.size(); i++) {
			areas.get(i).draw(g, x, y, drawAreaIDFlag);
		}
		
		// draw junctions
		if( drawJunctionFlag ) {
			for(int i=0; i<junctions.size(); i++) {
				junctions.get(i).draw(g, x, y);
			}
		}
	}
	
	public void drawOriginal(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
	}
	
	// analyze borders
	private void analyzeBorders() {
		
		borders.clear();
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				int color = image.getRGB(i, j);
				if( r(color) < 10 && g(color) < 10 && b(color) < 10 ) {
					borders.add(canvas[i][j]);
					canvas[i][j].border = true;
				}
			}
		}

		// connect pixels
		connetBorderPixel();
	}
	
	// assign areaIDs to pixels
	private void assignAreaID() {
		int areaIndex = 1;
		boolean[][] flag = new boolean[width][height];
		
		areas.clear();
		
		// separate areas 
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				// find undecided area
				if( !flag[i][j] && !canvas[i][j].border ) {
					// new area
					Area area = new Area(width, height, areaIndex);
					
					// start filling
					Stack<Point> stack = new Stack<Point>();
					stack.push(new Point(i,j));
						
					while( !stack.empty() ) {
						Point p = stack.pop();
						
						// already filled pixel
						if( flag[p.x][p.y] ) continue;
							
						// search for left and right
						int left, right;
						for(left=p.x; left>0; left--) {
							if( canvas[left-1][p.y].border ) {
								break;
							}
						}
						for(right=p.x; right<width-1; right++) {
							if( canvas[right+1][p.y].border ) {
								break;
							}
						}
							
						// fill from left to right
						for(int x=left; x<=right; x++) {
							flag[x][p.y] = true;
							canvas[x][p.y].areaID = areaIndex;
							area.addPixel(canvas[x][p.y]);
						}
							
						// scan new points
						for(int d=-1; d<=1; d+=2) {
							boolean cont = false;
							int y = p.y + d;
							if( y >= 0 && y < height ) {
								for(int x=left; x<=right; x++) {
									if( cont && canvas[x][y].border ) {
										stack.push(new Point(x-1, y));
										cont = false;
									}
									else if( !canvas[x][y].border ) {
										if( x == right ) {
											stack.push(new Point(x, y));
										} else {
											cont = true;
										}
									}
								}
							}
						}
					}
					
					// check area size
					if( area.pixels.size() >= 0 ) {
						// append new area
						areas.add(area);
						
						// next groupIndex
						areaIndex ++;
					} else {
						for(Pixel p : area.pixels) {
							p.border = true;
						}
					}
				}
			}
		}
		
		// remove small areas
		for(Area area : areas) {
			if( area.pixels.size() < 20 ) {
				for(Pixel p : area.pixels) {
					p.border = true;
				}
			}
		}
		
		// connect pixels
		connetBorderPixel();
	}
	
	// analyze areas
	private void analyzeAreas() {
		// assign areaID
		assignAreaID();
		
		// make borders clean
		cleanBorders();

		/*
		// final assign
		assignAreaID();
		
		// reassign separateAreaIDs
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
		}
		*/
		
		// find junctions
		junctions.clear();
		for(Pixel p : borders) {
			if( p.connect.size() == 3 ) {
				junctions.add(new Junction(p));
			}
		}
		
		Ordering ordering = new Ordering();
		// junction clues
		for(Junction j : junctions) {
			for(int i=0; i<2; i++) {
				ordering.addOrder(j.front, j.back[i], j.reliability[i]);
			}
		}
		// inclusion clues
		Inclusion inc = new Inclusion(borders, areas, width, height);
		inc.doAnalyze();
		for(InclusionInfo info : inc.getInclusions()) {
			if( info.isValid ) {
				for(int inner : info.innerAreaIDs) {
					ordering.addOrder(inner, info.outerAreaID, 0.6);
				}
			} else {
				for(int inner : info.innerAreaIDs) {
					//ordering.addOrder(inner, info.outerAreaID, 10);
				}
			}
		}
		
		System.out.println("----------------------");
		for(Order order : ordering.getOrders()) {
			System.out.println(order.start + "->" + order.end + "(" + order.getScore() + ")");
		}
		System.out.println("----------------------");
		
		
		Hierarchy hi = new Hierarchy();
		for(Order o : ordering.getOrders()) {
			if( o.getScore() > 0.5 ) {
				hi.addHierarchy(o.start, o.end);
			} else {
				hi.addHierarchy(o.end, o.start);
			}
		}
		hi.addDepthIndex();
/*
		// propagate depth
		Hierarchy hi = new Hierarchy();
		for(Junction j : junctions) {
			hi.addHierarchy(j.front, j.back[0]);
			hi.addHierarchy(j.front, j.back[1]);
		}
		hi.addDepthIndex();
*/
		// set depths and prepare image
		for(Area a : areas) {
			a.depth = hi.getDepth(a.areaID);
			a.prepareImage();
		}
		
	}
	
	// make borders clean
	private void cleanBorders() {
		boolean flag;
		do {
			System.out.println("loopppp");
			
			// set border separateAreaIDs
			for(Pixel p : borders) {
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
			}
			
			// remove unnecessary borders
			flag = false;
			for(int i=0; i<borders.size(); i++) {
				Pixel p = borders.get(i);
				if( p.separateAreaIDs.size() == 1 ) {
					flag = true;
					deleteBorder(p);
					i--;
				}
			}
		} while (flag);
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
				}
			}
		}
		
		// propagate separate area id
		p.areaID = p.separateAreaIDs.get(0);
		for(Pixel b : p.connect) {
			if( !b.separateAreaIDs.contains(p.areaID) ) {
				b.separateAreaIDs.add(p.areaID);
			}
		}
		
		// remove border attributes
		p.border = false;
		p.connect.clear();
		p.separateAreaIDs.clear();
		borders.remove(p);
	}
}
