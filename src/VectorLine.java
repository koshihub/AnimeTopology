
public class VectorLine {

	final int SearchDepth = 15;
	int[] vector;
	
	// Infer vector line that starts from "s" and directs to "d"
	public VectorLine(Pixel s, Pixel d) {
		
		Pixel current = d, prev = s;
		double prevAngle = 0.0f, angleDiff = 360.0f;
		
		for(int i=0; i<SearchDepth; i++) {
			System.out.println(""+i+"th iteration");
			
			if( current.connect.size() != 2 ) {
				// end of line
				System.out.println("end of line");
				break;
			} else {
				// find next point
				if( current.connect.get(0) == prev ) {
					prev = current;
					current = current.connect.get(1);
				} else {
					prev = current;
					current = current.connect.get(0);
				}
				
				// calculate angle
				double angle = calculateAngle((int)(current.x-s.x), (int)(current.y-s.y));
				double diff = Math.abs(angle - prevAngle);

				// if the angle difference between current and prev gets bigger, break loop
				if( diff <= angleDiff ) {
					angleDiff = diff;
					prevAngle = angle;
				} else {
					break;
				}
			}
		}

		vector = new int[] {prev.x - s.x, prev.y - s.y};
	}
	
	// returns angle (0.0f ~ 360.0f)
	private final double calculateAngle(double x, double y) {
		double deg = Math.acos(x / Math.sqrt(x*x + y*y)) / Math.PI * 180.0d;
		return (y < 0.0 ? 360.0d - deg : deg);
	}
}
