package mayagrations;

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

public class Region {
	
	private List<Center> centers;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	private Network<Object> net;	//excluded
	private ShortestPath<Object> sp;
	private List<Center> spawns;	 //excluded
	private Object exporter;
	private int droughtMod;
	private double deathChance;
	
//	OUTPUT VARIABLES	
	
	private int minPop;
	private int maxPop;
	
	public Region(List<Center> centers, Context<Object> context, Center exporter) {
		
//		DYNAMIC VARIABLES
		
		this.centers = centers;
		
		net = (Network<Object>) context.getProjection("market strength");		//excluded
		sp = new ShortestPath<Object>(net);
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			context.add(m);
		}

//		STATIC VARIABLES
		
		this.spawns = new ArrayList();
		for (Center c : this.centers) {
			int id = c.getID();
			if (id == 0 | id == 16 | id == 288 | id ==272) {
				spawns.add(c);
			}
		}
		this.exporter = exporter;
		Parameters params = RunEnvironment.getInstance().getParameters();
		droughtMod = (Integer)params.getValue("disturbanceDelay");
		deathChance = (Double)params.getValue("deathChance");
		
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
	
	private void calculateTrafficLong() {
		for (RepastEdge<Object> e : net.getEdges()) {
			Route<Object> m = (Route<Object>) e;
			m.setTrafficLong(0);
		}
		for (Center c : centers) {
			List<RepastEdge<Object>> path;
			path = sp.getPath(c,exporter);
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
	}
	
	//at the moment this method sets the pull of all centers to equal 1, which basically
	//renders the entire method pointless. A more interesting equation will eventually 
	//be added to give centers different pull values based on their various attributes.
	
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
			for (Group maya : center.getResidents()) {
				maya.setDestinations(destinations);
				maya.setPullFractions(pullFractions);
			}
		}
	}
	
	private void immigrate() {
		for (Center c : centers) {
			if (spawns.contains(c)){
//				if (c.getLabor()==0) {
					Group dude = new Group(c, true, "graph");
					c.addGroup(dude);
//				}
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
	
	private void disturbance() {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
    	if (tick > 1000+droughtMod & tick < 1100+droughtMod) {
    		List<Group> deathList = new ArrayList<Group>();
    		for (Center c : centers) {
    			for (Group group : c.getResidents()) {
    				if (RandomHelper.nextDoubleFromTo(0, 1) < deathChance) {
    					deathList.add(group);
    				}
    			}
    		}
    		for (Group group : deathList) {
    			group.getHomeCenter().killGroup(group);
    		}
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
	
	private void print(String phrase, double number) {
		System.out.print(phrase);
		System.out.print(" is ");
		System.out.println(number);
	}
	
	
}
