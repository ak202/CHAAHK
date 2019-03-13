package chaahk;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;

// this creates the imaginary Routes that allows Center.calculateImports() to give more central Centers
// less imports
public class RouteCreator<T> implements EdgeCreator<Route<T>, T> {
	
	public Route<T> createEdge(T source, T target, boolean isDirected, double weight) {
		if (weight > 0) {
			return new Route<T>(source, target, isDirected, weight);
		} else {
			return new EntryRoute<T>(source, target, isDirected, 10);
		}
	}
	
	public Class<Route> getEdgeType() {
		return Route.class;
	}
	
}



