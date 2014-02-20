import java.util.ArrayList;
import java.util.List;

public class Ordering {	
	List<Order> orders;
	
	public Ordering() {
		orders = new ArrayList<Order>();
	}
	
	public void addOrder(int front, int back, double reliability) {
		// the order is already exist
		for(Order o : orders) {
			if( o.start == front && o.end == back ) {
				o.weight += reliability;
				o.count ++;
				return;
			} else if( o.start == back && o.end == front ) {
				o.weight += (1 - reliability);
				o.count ++;
				return;
			}
		}

		// new order
		orders.add( new Order(front, back, reliability) );
	}
	
	public List<Order> getOrders() {
		return orders;
	}
}

class Order {
	int start, end, count;
	double weight;
	
	public Order(int start, int end, double weight) {
		this.start = start;
		this.end = end;
		this.count = 1;
		this.weight = weight;
	}
	
	public double getScore() {
		return weight / count;
	}
}