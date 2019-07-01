package chaahk;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;

// This EdgeCreator subclass creates the Edge subclasses feature in CHAAHK
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



