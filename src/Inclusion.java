import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Inclusion {

	int width, height;
	List<Pixel> borders;
	List<Area> areas;
	List<InclusionInfo> inclusions;
	
	public Inclusion(List<Pixel> borders, List<Area> areas, int w, int h) {
		this.borders = new ArrayList<Pixel>(borders);
		this.areas = areas;
		this.inclusions = new ArrayList<InclusionInfo>();
		this.width = w;
		this.height = h;
	}
	
	public void doAnalyze() {
		while( !borders.isEmpty() ) {
			Pixel start = borders.get(0), cur = start.connect.get(0);
			borders.remove(0);
			
			List<InclusionInfo> infoList = searchInclusions(start, cur, start.separateAreaIDs, new ArrayList<Pixel>(), 0);
			if( infoList.size() > 0 ) {
				for(InclusionInfo info : infoList) {
					inclusions.add(info);
					
					// check the validity of the inclusion
					if( !checkInside( areas.get(info.outerAreaID-1).getInnerPixel(), info.border ) ) {
						info.isValid = true;
					}
					
					//
					// debug print
					//
					System.out.println("------------INCLUSION------------");
					System.out.println("Outer: " + info.outerAreaID);
					System.out.print("Inner: ");
					for(int id : info.innerAreaIDs) {
						System.out.print(id + ",");
					}
					if(info.isVague) {
						System.out.println("(VAGUE);");
					} else {
						System.out.println(";");
					}
					if(info.isValid) {
						System.out.println("(VALID);");
					} else {
						System.out.println(";");
					}
				}
			}
		}
	}
	
	public List<InclusionInfo> getInclusions() {
		return inclusions;
	}
	
	// search possible implications
	private List<InclusionInfo> searchInclusions(Pixel start, Pixel cur, List<Integer> areas, List<Pixel> _visited, int depth) {
		// end of the border
		if( start == cur ) {
			if( depth <= 1 ) {
				return null;
			} else {
				// create and return an implication info
				InclusionInfo incInfo = new InclusionInfo(areas.get(0), _visited);
				List<InclusionInfo> incInfoList = new ArrayList<InclusionInfo>();
				incInfoList.add(incInfo);
				return incInfoList;
			}
		}
		
		// check if the border is already visited
		if( _visited.contains(cur) ) {
			return null;
		}
		
		// if the border pixel doesn't separate previously separated areas,
		// it indicates that there is no inclusion
		List<Integer> common = new ArrayList<Integer>(areas);
		common.retainAll(cur.separateAreaIDs);
		if( common.isEmpty() ) {
			return null;
		}
		
		// save visited info
		List<Pixel> visited = new ArrayList<Pixel>(_visited);
		visited.add(cur);
		borders.remove(cur);
		
		// search further
		List<InclusionInfo> incInfos = new ArrayList<InclusionInfo>();
		for(int i=0; i<cur.connect.size(); i++) {
			List<InclusionInfo> sub = searchInclusions(start, cur.connect.get(i), common, visited, depth+1);
			if( sub != null ) {
				incInfos.addAll( sub );
			}
		}
		
		return incInfos;
	}
	
	// check whether a given point is inside or outside of an area
	private boolean checkInside(Pixel p, List<Pixel> border) {
		List<Integer> xvals = new ArrayList<Integer>();
		for( Pixel b : border ) {
			if( b.y == p.y ) {
				xvals.add(b.x);
			}
		}
		
		// remove continuous values
		Collections.sort(xvals);
		for(int i=0; i<xvals.size()-1; i++) {
			if( xvals.get(i+1) - xvals.get(i) == 1 ) {
				xvals.remove(i);
				i--;
			}
		}
		
		int count = 0;
		for( int xval : xvals ) {
			if( xval > p.x ) {
				count ++;
			}
		}
		
		if( count % 2 == 1 ) {
			// inside
			return true;
		} else {
			// outside
			return false;
		}
	}
}

class InclusionInfo {
	int outerAreaID;
	List<Integer> innerAreaIDs;
	List<Pixel> border;
	boolean isVague;
	boolean isValid;
	
	public InclusionInfo(int outer, List<Pixel> visited) {
		outerAreaID = outer;
		innerAreaIDs = new ArrayList<Integer>();
		border = visited;
		isVague = false;
		isValid = false;
		
		for(Pixel p : visited) {
			for(int id : p.separateAreaIDs) {
				if( id != outer && !innerAreaIDs.contains(id)) {
					innerAreaIDs.add(id);
				}
			}
		}
		
		// if there is only one inner area, 
		// it is vague which area is inner or outer.
		if( innerAreaIDs.size() == 1 ) {
			isVague = true;
		}
	}
}