package chaahk;

import java.awt.Color;

import repast.simphony.space.graph.RepastEdge;
import repast.simphony.visualizationOGL2D.EdgeStyleOGL2D;

// this sets the width and color of Routes to show their type and, if bajo, weight

public class RouteEdgeStyle implements EdgeStyleOGL2D {

	@Override
	public int getLineWidth(RepastEdge<?> edge) {
		Route route = (Route) edge;
		String type = route.getType();
		if (type.equals("bajo")) {
			return 6;
		} else {
			return 3;
		}
	}

	@Override
	public Color getColor(RepastEdge<?> edge) {
		
		Route route = (Route) edge;
		String type = route.getType();
		if (type.equals("bajo")) {
			return route.getColorBajo();
		} else {
			return route.getColorUpland();
		}

	}



}
