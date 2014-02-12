import java.util.ArrayList;
import java.util.List;


public class Implication {

	List<Pixel> borders;
	
	public Implication(List<Pixel> borders) {
		this.borders = new ArrayList<Pixel>(borders);
	}
	
	public void doAnalyze() {
		while( !borders.isEmpty() ) {
			Pixel start = borders.get(0), cur = start.connect.get(0);
			borders.remove(0);
			
			List<ImplicationInfo> infoList = searchImplications(start, cur, start.separateAreaIDs, new ArrayList<Pixel>(), 0);
			if( infoList.size() > 0 ) {
				for(ImplicationInfo info : infoList) {
					//
					// debug print
					//
					System.out.println("------------IMPLICATION------------");
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
				}
			}
		}
	}
	
	// search possible implications
	private List<ImplicationInfo> searchImplications(Pixel start, Pixel cur, List<Integer> areas, List<Pixel> _visited, int depth) {
		// end of the border
		if( start == cur ) {
			if( depth <= 1 ) {
				return null;
			} else {
				// create and return an implication info
				ImplicationInfo impInfo = new ImplicationInfo(areas.get(0), _visited);
				List<ImplicationInfo> impInfoList = new ArrayList<ImplicationInfo>();
				impInfoList.add(impInfo);
				return impInfoList;
			}
		}
		
		// check if the border is already visited
		if( _visited.contains(cur) ) {
			return null;
		}
		
		// if the border pixel doesn't separate previously separated areas,
		// it indicates that there is no implication
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
		List<ImplicationInfo> impInfos = new ArrayList<ImplicationInfo>();
		for(int i=0; i<cur.connect.size(); i++) {
			List<ImplicationInfo> sub = searchImplications(start, cur.connect.get(i), common, visited, depth+1);
			if( sub != null ) {
				impInfos.addAll( sub );
			}
		}
		
		return impInfos;
	}
}

class ImplicationInfo {
	int outerAreaID;
	List<Integer> innerAreaIDs;
	List<Pixel> border;
	boolean isVague;
	
	public ImplicationInfo(int outer, List<Pixel> visited) {
		outerAreaID = outer;
		innerAreaIDs = new ArrayList<Integer>();
		border = visited;
		isVague = false;
		
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