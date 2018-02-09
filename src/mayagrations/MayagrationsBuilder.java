package mayagrations;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.graph.DefaultEdgeCreator;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class MayagrationsBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("Mayagrations");

		ContinuousSpaceFactory spFa = ContinuousSpaceFactoryFinder.
				createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spFa.createContinuousSpace("space", 
				context, new SimpleCartesianAdder<Object>(), 
				new repast.simphony.space.continuous.StrictBorders(),
				314, 314);
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		double x = 17.32;
		double y = 17.32;
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("market strength", context, false); 
		netBuilder.setEdgeCreator(new DefaultEdgeCreator<Object>());
		netBuilder.setEdgeCreator(new RouteCreator<Object>());
		netBuilder.buildNetwork();
		Network<Object> net = (Network<Object>)context.getProjection("market strength");

		List<Center> centers = new ArrayList<Center>();
		for (int i = 1; i <= 17; i++) {
			double y_temp = y * i;
			double x_shift = 0;
			if (i % 2 == 1) {
				x_shift = 8.66;
			}
			Center oldCenter = null;
			for (int j = 1; j <= 17; j++) {			
				double x_temp = (x * j) + x_shift;
				double x_off = RandomHelper.nextDoubleFromTo(-3.0, 3.0);
				double y_off = RandomHelper.nextDoubleFromTo(-3.0, 3.0);
				x_temp += x_off;
				double y_temp2 = y_off + y_temp;
				Center newCenter = new Center(0, i*17 + j - 18, context);
				context.add(newCenter);
				space.moveTo(newCenter, x_temp, y_temp2);
				centers.add(newCenter);
				
				if (oldCenter == null) {
					oldCenter = newCenter;					
				} else {
					addMayaEdge(net, space, oldCenter, newCenter);
					oldCenter = newCenter;
				}
			}
			
		}
		
		
		
		for (int i = 17; i < centers.size(); i++) {
			addMayaEdge(net, space, centers.get(i), centers.get(i-17));
			if(i % 17 != 0) {
				if((i/17)%2 == 1) {
					addMayaEdge(net, space, centers.get(i), centers.get(i-18));
				}
			} if(i % 17 != 16) {
				if((i/17)%2 == 0) {
					addMayaEdge(net, space, centers.get(i), centers.get(i-16));
				}
			}
		}
		
		for (Center center : centers) {
			int bajo = RandomHelper.nextIntFromTo(0,10);
			if (bajo > 7) {
				
				center.makeBajo();
			}
		}
		
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			if (m.getSourceCenter().getBajo() == true | m.getTargetCenter().getBajo() ==  true) {
				if (m.getTerrain() == false) {
					m.makeUplands();
				}
			}
		}

		Region graph = new Region(centers, context);
		context.add(graph);

		
		RunEnvironment.getInstance().endAt(16500);
		
		return context;
	}
	
	public void addMayaEdge(Network<Object> net, ContinuousSpace<Object> space, Center source, Center target) {
		NdPoint sourceLoc = space.getLocation(source);
		NdPoint targetLoc = space.getLocation(target);
		double distance = space.getDistance(sourceLoc, targetLoc);
		net.addEdge(source, target, distance);
	}
	
}
