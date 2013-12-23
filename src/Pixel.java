import java.util.ArrayList;
import java.util.List;

	
public class Pixel {
	
	int x, y;
	int areaID;
	List<Integer> separateAreaIDs;
	List<Pixel> connect;
	boolean border;
	
	public Pixel(int x, int y) {
		
		connect = new ArrayList<Pixel>();
		separateAreaIDs = new ArrayList<Integer>();
		border = false;
		this.x = x;
		this.y = y;
		
	}
	
}