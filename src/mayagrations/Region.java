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
	
//	
//	public double getRecentMod() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getRecentMod();
//		}
//		return recentMod;	
//	}
//	public double getTotalMod() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getTotalMod();
//		}
//		return recentMod;
//	}
//	public double getTempHurt() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getTempHurt();
//		}
//		return recentMod;
//	}
//	public double getTotalHurt() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getTotalHurt();
//		}
//		return recentMod;
//	}
//	public double getTempMod() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getTempMod();
//		}
//		return recentMod;
//	}
//	public double getWeight() {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		double recentMod = 0;
//		for (RepastEdge<Object> e : net.getEdges()) {
//			Route<Object> m = (Route<Object>) e;
//			recentMod += m.getWeight();
//		}
//		return recentMod;
//	}
//	



//private double distribute(double supply, List<Center> destinations, Network<Object> net, ShortestPath<Object> sp) {
////	System.out.println();
////	System.out.println();
////	System.out.println("start distribute ...");
////	System.out.println();
////	System.out.print("initial supply is ");
////	System.out.println(supply);
//	int n = 0;
//	for (RepastEdge<Object> i : net.getEdges()) {
//		n++;
//	}
//	
//	double totalcost = 0;
//	
//
//	double relSupplySum = 0;
//	for (Center center : destinations) {
//	
//		if (center.equals(mine)) {
//			center.setMineDistance(1);
//		} else {
//			List<RepastEdge<Object>> path;
//			path = sp.getPath(center,mine);
//			double distToMine = 0;
//			List<Route<Object>> mayaPath = new ArrayList();
//			for (RepastEdge<Object> e : path) {
//				distToMine += e.getWeight();
//				Route<Object> m = (Route<Object>) e;
//				mayaPath.add(m);
//			}
//			center.setPath(mayaPath);
//
//			totalcost += distToMine;
////			System.out.println("distance = " + Double.toString(distToMine));
//			center.setMineDistance(distToMine);
//		}
//		center.calcRelSupply();
////			System.out.print("adding ");
////			System.out.println(Double.toString(center.getRelSupply()));
//		relSupplySum += center.getRelSupply();
//	}
//
////	printFatCats();
//	double totalsupply = 0;
//	double newSupply = supply;
//	double newRelSupplySum = relSupplySum;
//	int totalSurplus = 0;
//	int fatcats = 0;
//	List<Center> newDestinations = new ArrayList<Center>(destinations);
//	double relsum2 = 0;
//	for (Center center : destinations) {
//		relsum2 += center.getRelSupply();
//	}
//	for (Center center : destinations) {
//		double allocation = (center.getRelSupply() / relSupplySum) * supply;
//		center.setSupply(allocation);
//		newSupply -= allocation;
////		double devMod = allocation/100;
////		if (center.equals(mine)) {
////			;
////		} else {
////			for (Route<Object> m : center.getPath()) {
////				m.helpWeight(devMod);
////			}
////		}
////		System.out.println();
////		System.out.print("id = ");
////		System.out.println(center.getID());
////		System.out.print("labor = ");
////		System.out.println(center.getLabor());
////		System.out.print("allocation = ");
////		System.out.println(allocation);
////		System.out.print("spc = ");
////		System.out.println(center.getSupplyPerCap());
//		totalsupply += allocation;
//		
//		if (center.getSupply()/center.getResidents().size() > 3) {
//			fatcats ++;
////			System.out.print("center ");
////			System.out.print(center.getID());
////			System.out.print(" has ");
////			System.out.print(center.getSupply());
////			System.out.print(" for ");
////			System.out.print(center.getLabor());
////			System.out.println(" citizens before surplus extraction.");
//			newDestinations.remove(center);
//			newRelSupplySum -= center.getRelSupply();
//			double surplus = center.getSupply() - (center.getResidents().size() * 3);
//			center.modSupply(- surplus);
////			newSupply += surplus;
//			totalsupply -= surplus;
//			totalSurplus += surplus;
////			System.out.print("center ");
////			System.out.print(center.getID());
////			System.out.print(" has ");
////			System.out.print(center.getSupply());
////			System.out.print(" for ");
////			System.out.print(center.getLabor());
////			System.out.print(" citizens after surplus extraction.");
////			System.out.println();
//		}
//
//	}
////	System.out.print("surplus is ");
////	System.out.println(totalSurplus);
////	System.out.print("remaining supply before surplus is ");
////	System.out.println(newSupply);
//	newSupply += totalSurplus;
////	System.out.print("remaining supply after surplus is ");
////	System.out.println(newSupply);
////	System.out.print("allocation is ");
////	System.out.println(totalsupply);
////	System.out.print("supposed fatcats ");
////	System.out.println(fatcats);
////	System.out.print("actual fatcats ");
//	printFatCats();
//	if (destinations.equals(newDestinations)) {
//		return totalsupply;
//	} else {
//		totalsupply += distribute(newSupply, newDestinations, net, sp);
//	} 
////	System.out.print("total supply after recursion is ");
////	System.out.println(totalsupply);
//	
//	return totalsupply;
//public void updateMarket() {
//
//for (Center i : centers) {
//	i.setMarket(i.getLabor());
//}
//
//Network<Object> net = (Network<Object>) context.getProjection("market strength");
//
//for (int h = 0; h < 5; h++) {
//	for (RepastEdge<Object> i : net.getEdges()) {
//		Route<Object> j = (Route<Object>) i;
//		Center c1 = (Center) j.getSource();
//		double m1 = c1.getMarket();
//		Center c2 = (Center) j.getTarget();
//		double m2 = c2.getMarket();
//		double avg = (m1 + m2) /j.getLength();
//		if (h == 4) {
//			j.setMarketFactor(avg);;
//		} else {
//			j.setMarket(avg);
//		}
//	}
//	
//	if (h == 4) {
//		for (RepastEdge<Object> i : net.getEdges()) {
//			Route<Object> j = (Route<Object>) i;
//			System.out.print("market = ");
//			System.out.println(j.getMarket());
//			System.out.print("weight = ");
//			System.out.println(j.getWeight());
//		}
//		break;
//	} else {
//		for (Center i : centers) {
//			List<Double> edgeMarkets = new ArrayList<Double>();
//			for (RepastEdge<Object> j : net.getEdges(i)) {
//				Route<Object> k = (Route<Object>) j; 
//				edgeMarkets.add(k.getMarket());
//			}
//			double avg = 0;
//			for (double j : edgeMarkets) {
//				avg += j;
//			}
//			avg = avg/edgeMarkets.size();
//			avg = (avg + i.getMarket())/2;
//			i.setMarket(avg);
//		}
//	}
//}
//}
	

//public void updateSupply(Context<Object> context) {
//Network<Object> net = (Network<Object>) context.getProjection("market strength");
//Queue<SupRoute> queue = new LinkedList <SupRoute>();
//queue.addAll(sup(net, mine, supply_mine));
//while (queue.peek() != null) {
//	System.out.println("testing 123");
//	SupRoute route = queue.remove(); 
//	System.out.println(route.supply);
//	queue.addAll(sup(net, route.center, route.supply));
//}
//}
//
//public List<SupRoute> sup(Network<Object> net, Center center, int supply) {
//System.out.println("sup");
//supply = supply - center.getLabor();
//center.setSupply(supply);
//if (supply < 0) {
//	System.out.println("rant out");
//	center.setSupply(center.getLabor());
//	return(new ArrayList<SupRoute>());
//}
//List<Route> edgeList = new ArrayList<Route>();
//double marketTotal = 0;
//for (RepastEdge<Object> i : net.getEdges(center)) {
//	Route<Object> j = (Route<Object>) i;
//	Center neighbor = (Center) j.getNeighbor(center);
//	if (neighbor.getVisited() == false) {
//		neighbor.setVisited(true);
//		marketTotal += j.getMarket();
//		edgeList.add(j);
//	} else {
//		System.out.println("has been visited");
//	}
//} System.out.print("edgeList size = ");
//System.out.println(edgeList.size());
//List<SupRoute> neighbors = new ArrayList<SupRoute>();
//for (Route<Object> i : edgeList) {
//	double supply_fraction = i.getMarket()/marketTotal;
//	double supply_sent = supply * supply_fraction;
//	Center neighbor = (Center) i.getNeighbor(center);
//	neighbors.add(new SupRoute(neighbor, (int)supply_sent));
//}
//System.out.println(neighbors);
//return(neighbors);
//}

//private int locatePlace(int sector) {
//	int centerIndex;
//	if (sector % 2 == 0) {
//		centerIndex = RandomHelper.nextIntFromTo(0, 7);
//		if (sector == 0) {
//			int[] indices = {68, 51, 34, 17,  1,  2,  3,  4};
//			return indices[centerIndex];
//		}
//		if (sector == 2) {
//			int[] indices = {12, 13, 14, 15, 33, 50, 67, 84};
//			return indices[centerIndex];
//		}
//		if (sector == 4) {
//			int[] indices = {220, 237, 254, 271, 287, 286, 285, 284};
//			return indices[centerIndex];
//		}
//		if (sector == 6) {
//			int[] indices = {204, 221, 238, 255, 273, 274, 275, 276};
//			return indices[centerIndex];
//		}
//	} else {
//		centerIndex = RandomHelper.nextIntFromTo(0, 6);
//		if (sector == 1) {
//			int[] indices = { 5, 6, 7, 8, 9, 10, 11};
//			return indices[centerIndex];
//		}
//		if (sector == 3) {
//			int[] indices = {101, 118, 135, 152, 169, 186, 203};
//			return indices[centerIndex];
//		}
//		if (sector == 5) {
//			int[] indices = {283, 282, 281, 280, 279, 278, 277};
//			return indices[centerIndex];
//		}
//		if (sector == 7) {
//			int[] indices = {187, 170, 153, 136, 119, 102, 85};
//			return indices[centerIndex];
//		}
//	}
//	return 0;
