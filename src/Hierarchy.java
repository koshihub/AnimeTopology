import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Hierarchy {

	List<HierarchyArea> hierarchy;
	
	public Hierarchy() {
		hierarchy = new ArrayList<HierarchyArea>();
	}
	
	public void addHierarchy(int front, int back) {
		HierarchyArea fa, ba;
		
		fa = getHierarchyAreaByAreaID(front);
		if(fa == null) {
			fa = new HierarchyArea(front);
			hierarchy.add(fa);
		}
		ba = getHierarchyAreaByAreaID(back);
		if(ba == null) {
			ba = new HierarchyArea(back);
			hierarchy.add(ba);
		}
		
		// add
		if( !fa.back.contains(ba) ) {
			fa.back.add(ba);
		}
		if( !ba.front.contains(fa) ) {
			ba.front.add(fa);
		}
	}
	
	public void addDepthIndex() {
		// prepare stack
		List<HierarchyArea> remain = new ArrayList<HierarchyArea>();
		for(HierarchyArea ha : hierarchy) {
			remain.add(ha);
		}
		
		while( remain.size() > 0 ) {
			// choose most front area
			for( HierarchyArea ha : remain ) {
				if( ha.isDecided() ) {
					remain.remove(ha);
					break;
				}
				if( ha.getUndecidedFront() == null ) {
					double frontDepth, backDepth;
					frontDepth = ha.getFrontDepth();
					backDepth = ha.getBackDepth();
					
					List<HierarchyArea> save = new ArrayList<HierarchyArea>();
					save.add(ha);
					
					// DFS
					HierarchyArea back = ha.getUndecidedBack();
					while( back != null ) {
						save.add(back);
						backDepth = back.getBackDepth();
						back = back.getUndecidedBack();
					}
					
					// decide depth
					double interval = (frontDepth - backDepth) / (double)(save.size()+1);
					for(int i=0; i<save.size(); i++) {
						save.get(i).depth = frontDepth - interval * (i+1);
						remain.remove(save.get(i));
					}
					break;
				}
			}
		}
	}
	
	public double getDepth(int areaID) {
		HierarchyArea ha = getHierarchyAreaByAreaID(areaID);
		if(ha != null) {
			System.out.println(ha.depth);
			return ha.depth;
		}
		else return 0.0;
	}
	
	public void setDepth(int areaID, double depth) {
		getHierarchyAreaByAreaID(areaID).depth = depth;
	}
	
	private HierarchyArea getHierarchyAreaByAreaID(int areaID) {
		for(HierarchyArea ha : hierarchy) {
			if( ha.areaID == areaID ) {
				return ha;
			}
		}
		return null;
	}
	
	private class HierarchyArea {
		int areaID;
		double depth;
		List<HierarchyArea> front;
		List<HierarchyArea> back;
		
		public HierarchyArea(int id) {
			areaID = id;
			depth = -1.0;
			front = new ArrayList<HierarchyArea>();
			back = new ArrayList<HierarchyArea>();
		}
		
		// check if the area is the most front of undecided areas
		public HierarchyArea getUndecidedFront() {
			for(HierarchyArea ha : front) {
				if( !ha.isDecided() ) {
					return ha;
				}
			}
			return null;
		}
		
		// returns an undecided area which is back of this
		public HierarchyArea getUndecidedBack() {
			for(HierarchyArea ha : back) {
				if( !ha.isDecided() ) {
					return ha;
				}
			}
			return null;
		}
		
		// returns front depth
		public double getFrontDepth() {
			for(HierarchyArea ha : front) {
				if( ha.isDecided() ) {
					return ha.depth;
				}
			}
			return 1.0;
		}
		
		// returns back depth
		public double getBackDepth() {
			for(HierarchyArea ha : back) {
				if( ha.isDecided() ) {
					return ha.depth;
				}
			}
			return 0.0;
		}
		
		public boolean isDecided() {
			return !(depth < 0.0);
		}
	}
}
