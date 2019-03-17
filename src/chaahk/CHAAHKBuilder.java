package chaahk;

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
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class CHAAHKBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("CHAAHK");

		// builds the two contexts: continuous space and network
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
		netBuilder.setEdgeCreator(new RouteCreator<Object>());
		netBuilder.buildNetwork();
		Network<Object> net = (Network<Object>)context.getProjection("market strength");

		// lays out the simulation's Centers and Routes
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
	
		//sets the type of each Route and Center: bajo or upland
		int amountUpland = (Integer)params.getValue("uplandAmount");
		int[] potentialUplands = new int[] {
			228,56,81,114,159,72,190,191,45,71,
			200,171,172,183,256,92,
			167,237,211,58,22,154,107,48,
			60,134,91,250,147,251,163,74,
			153,54,111,247,268,24,63,158,
			122,44,161,68,13,180,280,
			166,185,187,28,162,215,93,
			244,165,186,105,96,232,193,252,
			39,168,212,278,273,263,37,51,
			123,240,121,254,177,15,219,
			281,160,287,248,270,55,80,31,
			9,89,229,73,132,130,95,201,
			285,234,245,106,70,175,136,41,
			104,133,79,26,17,29,128,
			208,241,197,86,8,261,242,102,
			157,275,18,83,62,66,
			178,75,151,34,46,279,141,
			119,204,99,115,283,199,189,138,
			35,156,77,64,27,61,286,82,
			152,253,149,88,233,288,264,43,
			116,205,84,243,144,52,137,195,
			67,220,262,23,12,203,19,214,
			21,202,126,127,143,206,227,150,
			0,5,32,169,53,2,266,100,213,
			16,49,274,124,118,59,10,282,
			179,225,209,276,194,42,258,249,
			120,239,110,226,238,112,223,217,
			173,25,207,36,170,222,117,20,
			260,146,269,125,188,131,272,246,
			196,109,271,140,182,277,11,231,
			94,98,148,145,40,236,135,210,181,
			259,76,90,129,101,7,30,69,218,
			50,78,176,235,3,155,198,87,224,
			267,14,139,108,103,257,85,255,
			164,284,57,265,216,97,221,4,192,230,
			174,38,6,47,184,1,142,65,113,33
		};
		for (int i = 0; i < amountUpland; i++) {
			centers.get(potentialUplands[i]).makeUpland();
		}
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


		RunEnvironment.getInstance().endAt(1650);
		return context;
	}
	
	public void addMayaEdge(Network<Object> net, ContinuousSpace<Object> space, Center source, Center target) {
		NdPoint sourceLoc = space.getLocation(source);
		NdPoint targetLoc = space.getLocation(target);
		double distance = space.getDistance(sourceLoc, targetLoc);
		net.addEdge(source, target, distance);
	}
}
