import java.util.ArrayList;
import java.util.List;


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
		
		// add depth index
		addDepthIndex();
	}
	
	private void addDepthIndex() {
		
		// search most front area
	}
	
	public HierarchyArea getHierarchyAreaByAreaID(int areaID) {
		for(HierarchyArea ha : hierarchy) {
			if( ha.areaID == areaID ) {
				return ha;
			}
		}
		return null;
	}
	
	private class HierarchyArea {
		int areaID;
		List<HierarchyArea> front;
		List<HierarchyArea> back;
		
		public HierarchyArea(int id) {
			areaID = id;
			front = new ArrayList<HierarchyArea>();
			back = new ArrayList<HierarchyArea>();
		}
	}
}
