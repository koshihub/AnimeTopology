import java.util.List;
import java.util.ArrayList;

public class Graph<T> {
	List<Node> nodes;
	List<Edge> edges;
	
	public Graph() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
	}
	
	public int addNode(T object) {
		if( getNodeByObject(object) != null ) {
			return -1;
		}
		nodes.add(new Node(object));
		return 0;
	}
	
	public int addEdge(T obj1, T obj2, double weight) {
		for(Edge e : edges) {
			// if there exists a same edge, a new edge is not added to the graph,
			// but the weight is added.
			if( e.node1.object == obj1 && e.node2.object == obj2 ) {
				e.weight += weight;
				return 0;
			}
		}
		
		Node node1, node2;
		node1 = getNodeByObject(obj1);
		node2 = getNodeByObject(obj2);
		if( node1 == null || node2 == null ) {
			return -1;
		}
		Edge newedge = new Edge(node1, node2, weight); 
		edges.add(newedge);
		node1.edges.add(newedge);
		return 1;
	}
	
	private Node getNodeByObject(T object) {
		for(Node n : nodes) {
			if( n.object == object ) {
				return n;
			}
		}
		return null;
	}
	
	/*
	private List<Edge> getEdgesStartBy(Node node) {
		List<Edge> ret = new ArrayList<Edge>();
		for(Edge e : edges ) {
			if( e.node1 == node ) {
				ret.add(e);
			}
		}
		return ret;
	}
	*/
	
	private class Node {
		List<Edge> edges;
		T object;
		
		public Node(T node) {
			edges = new ArrayList<Edge>();
			this.object = node;
		}
	}
	
	private class Edge {
		Node node1, node2;
		double weight;
		
		public Edge(Node node1, Node node2, double weight) {
			this.node1 = node1;
			this.node2 = node2;
			this.weight = weight;
		}
	}
}
