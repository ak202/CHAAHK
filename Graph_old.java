package mayagrations;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.ShortestPath;
import repast.simphony.space.projection.Projection;
import repast.simphony.util.ContextUtils;

import com.google.common.collect.Lists;


public class Graph {
	
	Context<Object> context;
	Network<Object> net;
	List<City> cities;
	List<City> spawns;
	
	City mine = null;
	double supply_mine;
	ShortestPath<Object> sp;
	
	double offset;
	
	public Graph(List<City> cities, Context<Object> context) {
		this.cities = cities;
		int index = RandomHelper.nextIntFromTo(0, cities.size() -1);
		this.mine = cities.get(0);
		this.spawns = new ArrayList();
//		int[] spawnSpots = {19, 34, 306, 290};
		for (City c : this.cities) {
			int id = c.getID();
			if (id == 19 | id == 34 | id == 306 | id ==290) {
				spawns.add(c);
				c.makeSpawn();
			}
		}

		this.context = context;
		net = (Network<Object>) context.getProjection("market strength");
		sp = new ShortestPath<Object>(net);
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			context.add(m);
		}
	}
	
	public void updateSupply() {
		
		enhanceRoutes(net, sp);
		updateMarket();
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			m.resetMod();
		}
//		distribute(supply_mine, cities, net, sp);
		
		for (City c : cities) {
			if (spawns.contains(c)){
				Maya dude = new Maya(c, RandomHelper.nextDoubleFromTo(0,50), true);
				c.addMaya(dude);
				context.add(dude);
			}
			if (c.equals(mine)) {
				c.setMineDistance(1);
			} else {
				List<RepastEdge<Object>> path;
				path = sp.getPath(c,mine);
				double distToMine = 0;
				List<MayaEdge<Object>> mayaPath = new ArrayList();
				for (RepastEdge<Object> e : path) {
					distToMine += e.getWeight();
//					MayaEdge<Object> m = (MayaEdge<Object>) e;
//					mayaPath.add(m);
				}
				c.setMineDistance(distToMine);
			}
			c.drainSupply();
		}
		sp.finalize();

	}
	
	
	
	private void printFatCats() {
		int maxSum = 0;
		for (City c : cities) {
			if (c.getSupplyPerCap() >= 2) {
				maxSum ++;
//				System.out.print("city ");
//				System.out.print(c.getID());
//				System.out.print(" has ");
//				System.out.print(c.getSupply());
//				System.out.print(" for ");
//				System.out.print(c.getLabor());
//				System.out.print(" citizens after after surplus extraction.");
//				System.out.println();
		
			}
		} //System.out.println(maxSum);
	}
	private double distribute(double supply, List<City> destinations, Network<Object> net, ShortestPath<Object> sp) {
//		System.out.println();
//		System.out.println();
//		System.out.println("start distribute ...");
//		System.out.println();
//		System.out.print("initial supply is ");
//		System.out.println(supply);
		int n = 0;
		for (RepastEdge<Object> i : net.getEdges()) {
			n++;
		}
		
		double totalcost = 0;
		
	
		double relSupplySum = 0;
		for (City city : destinations) {
		
			if (city.equals(mine)) {
				city.setMineDistance(1);
			} else {
				List<RepastEdge<Object>> path;
				path = sp.getPath(city,mine);
				double distToMine = 0;
				List<MayaEdge<Object>> mayaPath = new ArrayList();
				for (RepastEdge<Object> e : path) {
					distToMine += e.getWeight();
					MayaEdge<Object> m = (MayaEdge<Object>) e;
					mayaPath.add(m);
				}
				city.setPath(mayaPath);

				totalcost += distToMine;
//				System.out.println("distance = " + Double.toString(distToMine));
				city.setMineDistance(distToMine);
			}
			city.calcRelSupply();
//				System.out.print("adding ");
//				System.out.println(Double.toString(city.getRelSupply()));
			relSupplySum += city.getRelSupply();
		}

//		printFatCats();
		double totalsupply = 0;
		double newSupply = supply;
		double newRelSupplySum = relSupplySum;
		int totalSurplus = 0;
		int fatcats = 0;
		List<City> newDestinations = new ArrayList<City>(destinations);
		double relsum2 = 0;
		for (City city : destinations) {
			relsum2 += city.getRelSupply();
		}
		for (City city : destinations) {
			double allocation = (city.getRelSupply() / relSupplySum) * supply;
			city.setSupply(allocation);
			newSupply -= allocation;
//			double devMod = allocation/100;
//			if (city.equals(mine)) {
//				;
//			} else {
//				for (MayaEdge<Object> m : city.getPath()) {
//					m.helpWeight(devMod);
//				}
//			}
//			System.out.println();
//			System.out.print("id = ");
//			System.out.println(city.getID());
//			System.out.print("labor = ");
//			System.out.println(city.getLabor());
//			System.out.print("allocation = ");
//			System.out.println(allocation);
//			System.out.print("spc = ");
//			System.out.println(city.getSupplyPerCap());
			totalsupply += allocation;
			
			if (city.getSupply()/city.getResidents().size() > 3) {
				fatcats ++;
//				System.out.print("city ");
//				System.out.print(city.getID());
//				System.out.print(" has ");
//				System.out.print(city.getSupply());
//				System.out.print(" for ");
//				System.out.print(city.getLabor());
//				System.out.println(" citizens before surplus extraction.");
				newDestinations.remove(city);
				newRelSupplySum -= city.getRelSupply();
				double surplus = city.getSupply() - (city.getResidents().size() * 3);
				city.modSupply(- surplus);
//				newSupply += surplus;
				totalsupply -= surplus;
				totalSurplus += surplus;
//				System.out.print("city ");
//				System.out.print(city.getID());
//				System.out.print(" has ");
//				System.out.print(city.getSupply());
//				System.out.print(" for ");
//				System.out.print(city.getLabor());
//				System.out.print(" citizens after surplus extraction.");
//				System.out.println();
			}

		}
//		System.out.print("surplus is ");
//		System.out.println(totalSurplus);
//		System.out.print("remaining supply before surplus is ");
//		System.out.println(newSupply);
		newSupply += totalSurplus;
//		System.out.print("remaining supply after surplus is ");
//		System.out.println(newSupply);
//		System.out.print("allocation is ");
//		System.out.println(totalsupply);
//		System.out.print("supposed fatcats ");
//		System.out.println(fatcats);
//		System.out.print("actual fatcats ");
		printFatCats();
		if (destinations.equals(newDestinations)) {
			return totalsupply;
		} else {
			totalsupply += distribute(newSupply, newDestinations, net, sp);
		} 
//		System.out.print("total supply after recursion is ");
//		System.out.println(totalsupply);
		
		return totalsupply;

	}
	
	public void enhanceRoutes(Network<Object> net, ShortestPath<Object> sp) {
		List<City> occupied = new ArrayList<City>();
		for (City city : cities) {
			if (city.getLabor() > 1) {
				occupied.add(city);
			}
		}
//		for (City thisCity : occupied) {
//			List<RepastEdge<Object>> path;
//			for (City otherCity : occupied) {
//				if (otherCity.equals(thisCity)) {
//					;
//				} else {
//					double distance = sp.getPathLength(thisCity, otherCity);
//					double interaction = (thisCity.getLabor() *  otherCity.getLabor()) / (distance * distance);
//					path = sp.getPath(thisCity, otherCity);
//					for (RepastEdge<Object> e : path) {
//						MayaEdge<Object> m = (MayaEdge<Object>) e;
//						m.helpWeight(interaction);
//					}
//				}
//			}
//		}
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			City sourceC = (City) m.getSource();
			City targetC = (City) m.getTarget();
//			System.out.println();
//			System.out.print("id 1 ");
//			System.out.println(sourceC.getID());
//			System.out.print("pop 2 ");
//			System.out.println(sourceC.getLabor());
//			System.out.print("id 2 ");
//			System.out.println(targetC.getID());
//			System.out.print("pop 2 ");
//			System.out.println(targetC.getLabor());
			int sPop = sourceC.getLabor();
			int tPop = targetC.getLabor();
			if (sPop > 0 & tPop > 0) {
				double interaction = (sPop * tPop) / (m.getWeight() * m.getWeight());
//				System.out.println();
//				System.out.println(interaction);
				m.helpWeight(interaction * 100);
			}
				
			}
	}
	public void adjustEdgeWeights(Network<Object> net) {
		for (RepastEdge<Object> i : net.getEdges()) {
			MayaEdge<Object> j = (MayaEdge<Object>) i;
			j.setWeight(j.getAccess());
		}
	}
	
	public void setDrought(double drought) {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			m.setDroughtStrength(drought);
		}
	}
	
	
	public double getRecentMod() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getRecentMod();
		}
		return recentMod;	
	}
	public double getTotalMod() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getTotalMod();
		}
		return recentMod;
	}
	public double getTempHurt() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getTempHurt();
		}
		return recentMod;
	}
	public double getTotalHurt() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getTotalHurt();
		}
		return recentMod;
	}
	public double getTempMod() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getTempMod();
		}
		return recentMod;
	}
	public double getWeight() {
		Network<Object> net = (Network<Object>) context.getProjection("market strength");
		double recentMod = 0;
		for (RepastEdge<Object> e : net.getEdges()) {
			MayaEdge<Object> m = (MayaEdge<Object>) e;
			recentMod += m.getWeight();
		}
		return recentMod;
	}
	
public void updateMarket() {
		
//		for (City i : cities) {
//			i.setMarket(i.getLabor());
//		}
//		
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		
//		for (int h = 0; h < 5; h++) {
//			for (RepastEdge<Object> i : net.getEdges()) {
//				MayaEdge<Object> j = (MayaEdge<Object>) i;
//				City c1 = (City) j.getSource();
//				double m1 = c1.getMarket();
//				City c2 = (City) j.getTarget();
//				double m2 = c2.getMarket();
//				double avg = (m1 + m2) /j.getLength();
//				if (h == 4) {
//					j.setMarketFactor(avg);;
//				} else {
//					j.setMarket(avg);
//				}
//			}
//			
//			if (h == 4) {
//				for (RepastEdge<Object> i : net.getEdges()) {
//					MayaEdge<Object> j = (MayaEdge<Object>) i;
//					System.out.print("market = ");
//					System.out.println(j.getMarket());
//					System.out.print("weight = ");
//					System.out.println(j.getWeight());
//				}
//				break;
//			} else {
//				for (City i : cities) {
//					List<Double> edgeMarkets = new ArrayList<Double>();
//					for (RepastEdge<Object> j : net.getEdges(i)) {
//						MayaEdge<Object> k = (MayaEdge<Object>) j; 
//						edgeMarkets.add(k.getMarket());
//					}
//					double avg = 0;
//					for (double j : edgeMarkets) {
//						avg += j;
//					}
//					avg = avg/edgeMarkets.size();
//					avg = (avg + i.getMarket())/2;
//					i.setMarket(avg);
//				}
//			}
//		}
	}
			
	
//	public void updateSupply(Context<Object> context) {
//		Network<Object> net = (Network<Object>) context.getProjection("market strength");
//		Queue<SupRoute> queue = new LinkedList <SupRoute>();
//		queue.addAll(sup(net, mine, supply_mine));
//		while (queue.peek() != null) {
//			System.out.println("testing 123");
//			SupRoute route = queue.remove(); 
//			System.out.println(route.supply);
//			queue.addAll(sup(net, route.city, route.supply));
//		}
//	}
//	
//	public List<SupRoute> sup(Network<Object> net, City city, int supply) {
//		System.out.println("sup");
//		supply = supply - city.getLabor();
//		city.setSupply(supply);
//		if (supply < 0) {
//			System.out.println("rant out");
//			city.setSupply(city.getLabor());
//			return(new ArrayList<SupRoute>());
//		}
//		List<MayaEdge> edgeList = new ArrayList<MayaEdge>();
//		double marketTotal = 0;
//		for (RepastEdge<Object> i : net.getEdges(city)) {
//			MayaEdge<Object> j = (MayaEdge<Object>) i;
//			City neighbor = (City) j.getNeighbor(city);
//			if (neighbor.getVisited() == false) {
//				neighbor.setVisited(true);
//				marketTotal += j.getMarket();
//				edgeList.add(j);
//			} else {
//				System.out.println("has been visited");
//			}
//		} System.out.print("edgeList size = ");
//		System.out.println(edgeList.size());
//		List<SupRoute> neighbors = new ArrayList<SupRoute>();
//		for (MayaEdge<Object> i : edgeList) {
//			double supply_fraction = i.getMarket()/marketTotal;
//			double supply_sent = supply * supply_fraction;
//			City neighbor = (City) i.getNeighbor(city);
//			neighbors.add(new SupRoute(neighbor, (int)supply_sent));
//		}
//		System.out.println(neighbors);
//		return(neighbors);
//	}
	
}
