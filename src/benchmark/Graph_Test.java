package benchmark;

public class Graph_Test {
	Graph<String> graph;
	
	public Graph_Test() {
		graph = new Graph<String>();
	}
	
	public void execute() {
		graph.addNode("node1");
		graph.addNode("node2");
		graph.addNode("node3");
		graph.addNode("node4");
		graph.addNode("source");
		graph.addNode("sink");
		graph.addEdge("source", "node1", 0.5);
		graph.addEdge("source", "node1", 0.15);
		graph.addEdge("source", "node4", 0.3);
		graph.addEdge("source", "node3", 0.8); 
		
		graph.show();
	}
}
