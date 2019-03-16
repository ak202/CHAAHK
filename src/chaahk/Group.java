package chaahk;

import java.util.ArrayList;
import java.util.Hashtable;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;


public class Group {
	
	private Center homeCenter;
	private boolean migrantStatus;
	private boolean prioritizesImports;
	private int acclimation;
	private int resourceLackCount;

	public Group (Center homeCenter, boolean migrant) {
		this.homeCenter = homeCenter;
		this.migrantStatus = migrant;
		prioritizesImports = true;
		acclimation = 0;
		resourceLackCount = 0;
	}
	
	// endemic citizens recieve priority access to both resources
	@ScheduledMethod(start = 3, interval = 5)
	public void endemicsConsume() {
		if (!migrantStatus) {
			consumeResources();
		}
	}
	
	@ScheduledMethod(start = 4, interval = 5)
	public void migrantsConsume() {
		if (migrantStatus) {
			acclimate();
			consumeResources();
		}
	}

	// the Group will ultimately attept to consume both resources, but the order 
	// in which they do is swapped each turn. if statements prevent Groups that are
	// removed fron their first attempt from consuming further resources, which can cause
	// an error (in addition to being simply onfair to the other Groups!).
	private void consumeResources() {
		swapPrioritizedResource();
		if (prioritizesImports) {
			if (consumeImports()) {
				consumeStaples();
				return;
			} 
		} else {
			if (consumeStaples()) {
				consumeImports();
				return;
			} 
		}
	}
		
	private boolean consumeStaples() {
		if (homeCenter.getStaples() >= 1) {
			homeCenter.decreaseStaples();
			resourceLackCount = 0;
			return(true);
		} else {
			if (!migrantStatus) {
				homeCenter.incStapleRemovals();;
			}
			resourceLack();
			return(false);
		} 
	}
	
	private boolean consumeImports() {
		if (homeCenter.getImports() >= 1) {
			homeCenter.decreaseImports();
			resourceLackCount = 0;
			return(true);
		} else {
			if (!migrantStatus) {
				homeCenter.incImportRemovals();;
			}
			resourceLack();
			return(false);
		} 
	}
	
	// Groups that lack resources have equal odds to migrate, be removed, or be unaffected.
	// However, if the Group fails to consume the relavent resouce two times in a row,
	// they are automatically removed. The randomness ensures that Centers
	// have a vaguely stable population, while resourceLackCount ensures
	// that truelly exhausted Centers do indeed lose population. This is
	// very much a "top down" solution to these problems, but it is both very 
	// simple and effective towards allowing the simulation's other elements 
	// to preform as intended.
	private void resourceLack() {
		double chance = RandomHelper.nextDoubleFromTo(0, 1);
		if (chance < .33 | resourceLackCount == 2) {
			homeCenter.removeGroup(this);
		} else if (chance < .66) {
			migrate();
		} resourceLackCount ++;
	}


	// Centers with higher pullFractions are more likely to become the Groups new
	// homeCenter
	private void migrate() {
		double chance = RandomHelper.nextDoubleFromTo(0,1);
		ArrayList<Double> pullFractions = homeCenter.getPullFractions();
		Hashtable<Double, Center> destinations = homeCenter.getDestinations();
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
		homeCenter.incEmigrations();
		homeCenter.emmigrate(this);
		homeCenter = newHome;
		homeCenter.immigrate(this);
	}
	
	// the swapping of the prioritized resource ensures there is not disproportionate
	// demand for either of the resources.
	private void swapPrioritizedResource() {
		if (prioritizesImports) {
			prioritizesImports = false;
		} else {
			prioritizesImports = true;
		}
	}
	
	public boolean getMigrant() {
		return migrantStatus; 
	}
	
	private void acclimate() {
		if (acclimation == 5) {
			this.migrantStatus = false;
			homeCenter.settle();
		} else if (acclimation < 5){
			acclimation ++;
		}
	}
	
	public Center getHomeCenter() {
		return homeCenter;
	}
}

