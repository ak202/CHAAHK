package chaahk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class CHAAHKBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("CHAAHK");
		Parameters params = RunEnvironment.getInstance().getParameters();

		// builds the two contexts: continuous space and network
		ContinuousSpaceFactory spFa = ContinuousSpaceFactoryFinder.
				createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spFa.createContinuousSpace("space", 
				context, new SimpleCartesianAdder<Object>(), 
				new repast.simphony.space.continuous.StrictBorders(),
				314, 314);
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("market strength", context, false); 
		netBuilder.setEdgeCreator(new RouteCreator<Object>());
		netBuilder.buildNetwork();
		Network<Object> net = (Network<Object>)context.getProjection("market strength");

		// lays out the simulation's Centers and horizontal Routes
		List<Center> centers = new ArrayList<Center>();
		double x = 17.32;
		double y = 17.32;
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
				Center newCenter = new Center(i*17 + j - 18, context);
				context.add(newCenter);
				space.moveTo(newCenter, x_temp, y_temp2);
				centers.add(newCenter);
				
				// adds horizontal Routes
				if (oldCenter == null) {
					oldCenter = newCenter;					
				} else {
					addMayaEdge(net, space, oldCenter, newCenter);
					oldCenter = newCenter;
				}
			}
		}

		//adds diagonal Routes
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

		// determines which centers are uplands i.e. active, dynamic ones

		int uplandAmount = (Integer)params.getValue("uplandAmount");
		int[] potentialUplands = PotentialUplands.get();
		for(int i = 0; i < uplandAmount; i++) {
			Center uplandCenter = centers.get(potentialUplands[i]);
			uplandCenter.makeUpland();
		}
		
		// this part makes any center adjacent to the original "uplands" into uplands itself
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			if (m.getSourceCenter().getUpland()) {
				m.makeUpland();
				m.getTargetCenter().setWater(false);
			} else if (m.getTargetCenter().getUpland()) {
				m.makeUpland();
				m.getSourceCenter().setWater(false);
			}	
		}
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			if (m.getType() == "Bajo") {
				m.initBajo();
			} else {
				m.initUpland();
			}
		}
		
		//connects the four "exporter" centers on corners to the imaginary source of imports
		Center exporter = new Center(1337, context);
		context.add(exporter);
		for (Center c : centers) {
			int id = c.getID();
			if (id == 0 | id == 16 | id == 288 | id ==272) {
				net.addEdge(c, exporter, 0);
			}
		}

		Region region = new Region(centers, context, exporter);
		context.add(region);
		OutputAggregator outputAgg = new OutputAggregator(centers, net);
		context.add(outputAgg);

		// simulation ends at timestep 1650
		RunEnvironment.getInstance().endAt(1650);

		if (ParameterCheck.check(params, net)) {
			// Parameter check will throw the actual exception, but this conditional prevents 
			// chaahk from running when that happens in a way where users can re-enter the parameter
			// values
			return null;
		} else {
			return context;
		}
	}
	
	public void addMayaEdge(Network<Object> net, ContinuousSpace<Object> space, Center source, Center target) {
		NdPoint sourceLoc = space.getLocation(source);
		NdPoint targetLoc = space.getLocation(target);
		double distance = space.getDistance(sourceLoc, targetLoc);
		net.addEdge(source, target, distance);
	}
}
