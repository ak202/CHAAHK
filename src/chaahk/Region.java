package chaahk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.ShortestPath;

// This class controls much of the simulation's scheduling behavior and calls methods drawing on global information.
public class Region {
	
	private List<Center> centers;
	private List<Center> gateways;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	private Network<Object> net;	//excluded
	private ShortestPath<Object> sp;
	private Object exporter;
	private int disturbanceDelay;
	private double disturbanceRemovalChance;
	
//	OUTPUT VARIABLES - do not affect model operation	
	private int minPop;
	private int maxPop;

	// this object controls simulation-wide scheduling and behavior
	public Region(List<Center> centers, Context<Object> context, Center exporter) {
		
		Parameters params = RunEnvironment.getInstance().getParameters();

//		DYNAMIC VARIABLES
		
		net = (Network<Object>) context.getProjection("market strength");		//excluded
		sp = new ShortestPath<Object>(net);
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			context.add(m);
		}

//		FIXED VARIABLES
		
		this.centers = centers;
		gateways = new ArrayList();
		for (Center c : this.centers) {
			int id = c.getID();
			if (id == 0 | id == 16 | id == 288 | id ==272) {
				gateways.add(c);
			}
		}
		this.exporter = exporter;
		disturbanceDelay = (Integer)params.getValue("disturbanceDelay");
		disturbanceRemovalChance = (Double)params.getValue("disturbanceRemovalChance");
		
//		OUTPUT VARIABLES

		minPop = 0;
		maxPop = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 5)
	public void calculateCenterResources() {
		calculateTrafficLong();
		sp.finalize();
		disturbance();
		for (Center c : centers) {
			c.reproduce();
			c.calculateStaples();
			c.calculateImports();
		}
		immigrate();
		rankCenters();
	}
	
	// calulates each Route's trafficLong and each Center's distanceToExporter via calculated the shortest
	// path to the nearest gateway center (those at simulation's 4 corners)
	private void calculateTrafficLong() {
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			m.resetTrafficLong();
		}
		for (Center c : centers) {
			List<RepastEdge<Object>> pathToGateway;
			pathToGateway = sp.getPath(c,exporter);
			double distToExporter = 0;
			for (RepastEdge<Object> e : pathToGateway) {
				Route<Object> m = (Route<Object>) e;
				distToExporter += m.getWeight();
				m.addTrafficLong(c.getEndemic());
			}
			c.setDistanceToExporter(distToExporter);
		}
	}
	
	// calculates each Center's pull (attraction to immigrants) and puts them in a list sorted according
	// to these values
	public void rankCenters() {
		destinations = new Hashtable<Double, Center>(17);
		pullFractions = new ArrayList<Double>();
		double totalPull = 0;
		for (Center center : centers) {
			if (center.getDistToExporter() < 1) {
				center.setPull(0);
			} else {
				center.setPull(Math.pow(1/center.getDistToExporter(),2)); 
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
		}
	}
	
	// this represents immigration from the external areas surrounding that represented in CHAAHK
	private void immigrate() {
		for (Center g : gateways) {
				Group dude = new Group(g, true);
				g.addGroup(dude);
		}
	}
	
	// every time disturbance is called (each 5 steps) each Group has a disturbanceRemovalChance chance to be removed
	private void disturbance() {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
    		if (tick > 1000+disturbanceDelay & tick < 1100+disturbanceDelay) {
    			List<Group> removalList = new ArrayList<Group>();
    			for (Center c : centers) {
    				for (Group group : c.getResidents()) {
    					if (RandomHelper.nextDoubleFromTo(0, 1) < disturbanceRemovalChance) {
    						removalList.add(group);
    					}
    				}
    			}
    			for (Group group : removalList) {
    				group.getHomeCenter().removeGroup(group);
    			}
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
			totalPop += c.getLabor();
		}
		return totalPop;
	}
	
	
	public double bajoFrac() {
		double routes = 0;
		double bajos = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			if (m.getType().equals("bajo")) {
				bajos++;
			} routes ++;
		}
		double frac = bajos/routes;
		return frac;
	}

	public int getMinPop() {
		return minPop;
	}
	
	public int getMaxPop() {
		return maxPop;
	}	

}
