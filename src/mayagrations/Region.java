package mayagrations;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.ShortestPath;

public class Region {
	
//	DYNAMIC VARIABLES

	private List<Center> centers;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	
	private Network<Object> net;	//excluded
	private ShortestPath<Object> sp;
	
//	STATIC VARIABLES
	
	private List<Center> spawns;	 //excluded
	private Center exporter;
	private Center mine;
	
//	OUTPUT VARIABLES	
	
	int minPop;
	int maxPop;
	int finalPop;
	
	public Region(List<Center> centers, Context<Object> context) {
		
//		DYNAMIC VARIABLES
		
		this.centers = centers;
		
		net = (Network<Object>) context.getProjection("market strength");		//excluded
		sp = new ShortestPath<Object>(net);
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			context.add(m);
		}

//		STATIC VARIABLES
		
		int mineLocation = 8;													//excluded
		this.mine = centers.get(mineLocation);
		this.spawns = new ArrayList();
		for (Center c : this.centers) {
			int id = c.getID();
			if (id == 0 | id == 16 | id == 288 | id ==272 | id == mineLocation) {
				spawns.add(c);
			}
		}
		
//		OUTPUT VARIABLES	
		
		minPop = 0;
		maxPop = 0;
		finalPop = 0;
		
	}
	
	@ScheduledMethod(start = 1, interval = 5)
	public void calculateTrafficLong() {
		resetNetworkTraffic();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		for (Center c : centers) {
			c.reproduce();
		}
		for (Center c : centers) {
			if (spawns.contains(c)){
				Group dude = new Group(c, true, "graph");
				c.addGroup(dude);
			}
			if (c.equals(mine)) {
				c.setMineDistance(0.1);
			} else {
				List<RepastEdge<Object>> path;
				path = sp.getPath(c,mine);
				double distToMine = 0;
				for (RepastEdge<Object> e : path) {
					distToMine += e.getWeight();
				}
				c.setMineDistance(distToMine);
				for (RepastEdge<Object> e : path) {
					Route<Object> m = (Route<Object>) e;
					m.setTrafficLong(m.getTrafficLong() + c.getEndemic());
				}
			}
			c.calculateStaples();
		}
		for (Center c : centers) {
			c.calculateStaples();
			c.calculateImports();
		}
		sp.finalize();
		rankCenters();
	}
	
	public void rankCenters() {
		destinations = new Hashtable<Double, Center>(17);
		pullFractions = new ArrayList<Double>();
		double totalPull = 0;
		for (Center center : centers) {
			if (center.getResSize() > 0) {
				center.setPull(Math.pow(Math.sqrt(center.getStaplesPerCap()) + Math.sqrt(center.getImportsPerCap()), 2));
			} else {
				center.setPull(1/center.getDistToExporter());
			}
			totalPull += center.getPull();
		}
		for (Center center : centers) {
			double pullFraction = center.getPull()/totalPull;
			pullFraction += RandomHelper.nextDoubleFromTo(-0.0000001, 0.0000001);
			destinations.put(pullFraction, center);
			pullFractions.add(pullFraction);
		}
		Collections.sort(pullFractions); 
		for (Center center : centers) {
			center.setDestinations(destinations);
			center.setPullFractions(pullFractions);
			for (Group maya : center.getResidents()) {
				maya.setDestinations(destinations);
				maya.setPullFractions(pullFractions);
			}
		}
	}
	
	public void resetNetworkTraffic() {
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			m.setTrafficLong(0);
		}
	}											
	
	@ScheduledMethod(start = 5, interval = 5)
	public void recordPop() {
		int pop = countPop();
		if (pop >= maxPop) {
			maxPop = pop;
			minPop = pop;
		} else if ( pop < minPop) {
			minPop = pop;
		}
	}
	
	public int countPop() {
		int totalPop = 0;
		for (Center c : centers) {
			totalPop += c.getPop();
		}
		return totalPop;
	}
	
	public int getMinPop() {
		return minPop;
	}
	
	public int getMaxPop() {
		return maxPop;
	}	
}
