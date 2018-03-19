package mayagrations;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;

public class RouteCreator<T> implements EdgeCreator<Route<T>, T> {
	
	public Route<T> createEdge(T source, T target, boolean isDirected, double weight) {
		if (weight > 0) {
			return new Route<T>(source, target, isDirected, weight);
		} else {
			return new EntryRoute<T>(source, target, isDirected, weight);
		}
	}
	
	public Class<Route> getEdgeType() {
		return Route.class;
	}
	
}



//public class RouteCreator<T> implements EdgeCreator<RepastEdge<T>, T> {
//
//  /**
//   * Creates an Edge with the specified source, target, direction and weight.
//   *
//   * @param source     the edge source
//   * @param target     the edge target
//   * @param isDirected whether or not the edge is directed
//   * @param weight     the weight of the edge
//   * @return the created edge.
//   */
//  public RepastEdge<T> createEdge(T source, T target, boolean isDirected, double weight) {
//	  return new RepastEdge<T>(source, target, isDirected, weight);
//  }
//
//  /**
//   * Gets the edge type produced by this EdgeCreator.
//   *
//   * @return the edge type produced by this EdgeCreator.
//   */
//  public Class<RepastEdge> getEdgeType() {
//	  return RepastEdge.class;
//  }
//}