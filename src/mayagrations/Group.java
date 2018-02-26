package mayagrations;

import java.util.ArrayList;
import java.util.Hashtable;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.ShortestPath;
import repast.simphony.util.ContextUtils;


public class Group {
	
	private Center homeCenter;
	private boolean migrantStatus;
	private int stay;
	private String source;
	private double migrationDistanceThreshold;

	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;

	public Group (Center homeCenter, boolean migrant, String source) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		this.homeCenter = homeCenter;
		this.migrantStatus = migrant;
		stay = 0;
		this.source = source;
		migrationDistanceThreshold = (Double)params.getValue("migrationDistanceThreshold");
		destinations = null;
		pullFractions = null;
	}
	


	
	@ScheduledMethod(start = 3, interval = 5)
	public void consumeEndemic() {
		if (!migrantStatus) {
			if (homeCenter.getStaples() >= 1) {
				homeCenter.modStaples(-1);
			} else {
				homeCenter.incStaplesDeaths();;
				trouble();
				return;
			} 
			if (homeCenter.getImports() >= 1) {
				homeCenter.modImports(-1);
			} else {
				homeCenter.incImportsDeaths();;
				trouble();
				return;
			} 
		}
	}
	
	@ScheduledMethod(start = 4, interval = 5)
	public void consumeMigrant() {
		if (migrantStatus) {
			if (homeCenter.getStaples() >= 1) {
				homeCenter.modStaples(-1);
			} else {
				trouble();
				return;
			}
			if (homeCenter.getImports() >= 1) {
				homeCenter.modImports(-1);
			} else {
				trouble();
				return;
			}
			setMigrant();
		}
	}
	
	
	public void trouble() {
		double chance = RandomHelper.nextDoubleFromTo(0, 1);
		if (chance < .33) {
			homeCenter.killGroup(this);
		} else if (chance < .66) {
			migrate();
		}
	}


	public void migrate() {
		double chance = RandomHelper.nextDoubleFromTo(0,1);
		Center newHome = homeCenter;
		for (int i = 288; i >= 0; i--) {
			double pullFraction = pullFractions.get(i);
			if (chance < pullFraction) {
				newHome = destinations.get(pullFraction);
				break;
			} else {
				chance -= pullFraction; 
			}
		} 
//		Context<Object> context = ContextUtils.getContext(homeCenter);
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		ShortestPath<Object> sp2 = new ShortestPath<Object>(net);
//		double distance = sp2.getPathLength(homeCenter, newHome);
//		homeCenter.emmigrate(this);
//		if (distance > migrationDistanceThreshold) {
//			homeCenter.incMoveDeaths();
//			context.remove(this);
//		} else {
//			homeCenter.incMoveLifes();
//			homeCenter = newHome;
//			homeCenter.immigrate(this);
//		}
//		sp2.finalize();
		homeCenter.incMoveLifes();
		homeCenter.emmigrate(this);
		homeCenter = newHome;
		homeCenter.immigrate(this);
	}
	
	public boolean getMigrant() {
		return migrantStatus; 
	}
	
	public void setMigrant() {
		if (stay == 5) {
			this.migrantStatus = false;
			homeCenter.settle();
		} else if (stay < 5){
			stay ++;
		}
	}
	
	public String getSource() {
		return source;
	}
	
	public void setDestinations(Hashtable<Double, Center> destinations) {
		Object dests = destinations.clone();
		this.destinations = (Hashtable<Double, Center>) dests;
	}

	public void setPullFractions(ArrayList<Double> pullFractions) {
		Object pullfras = pullFractions.clone();
		this.pullFractions = (ArrayList<Double>) pullfras;
	}
}

