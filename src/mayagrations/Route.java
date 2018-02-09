package mayagrations;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.RepastEdge;

public class Route<T> extends RepastEdge<T> {

//	DYNAMIC ENTITY VARIABLES
	
	private int obSourceID;
	private int obTargetID;
	
	private double weight;
	private double trafficFinal;
	private double trafficLong;
	private double trafficShort;
	private double costEmployable;
	private double costEngineered;

	private double costIncrease;
	private double costDecrease;
	
//	STATIC ENTITY VARIABLES
	protected T source;
	protected T target;
	private Center sourceCenter;
	private Center targetCenter;
	protected boolean directed;
	private double costBase;
	private double costMax;
	private double costMin;
	private boolean terrain;
	
//	STATIC GLOBAL VARIABLES
	private double costIncRate;
	private double costDecRate;
	private double costRegen;
	private double costResil;
	private double costDisturbance;
	private double trafficShortCoefficient;
	private double trafficLongCoefficient;
	
	private int droughtMod;

	protected Route(){
	}

	public Route(T source, T target, boolean directed) {
		this(source, target, directed, 1);
	}

	public Route(T source, T target, boolean directed, double weight) {

		Parameters params = RunEnvironment.getInstance().getParameters();
		obSourceID = (Integer)params.getValue("obSourceID");;
		obTargetID = (Integer)params.getValue("obTargetID");;;
		
//		DYNAMIC ENTITY VARIABLES
		
		this.weight = weight;
		trafficFinal = 0;
		trafficLong = 0;
		trafficShort = 0;
		costEmployable = weight;
		costEngineered = weight;
		costIncrease = 0;
		costDecrease = 0;
		terrain = false;
		
//		STATIC ENTITY VARIABLES
		this.source = source;
		this.target = target;
		sourceCenter = (Center) source;
		targetCenter = (Center) target;
	
		this.directed = directed;
		costBase = weight;
		costMax = costBase * (Double)params.getValue("costMaxFactor");
		costMin = costBase * (Double)params.getValue("costMinFactor");
		
//		STATIC GLOBAL VARIABLES
		costIncRate = (Double)params.getValue("costIncRate");
		costDecRate = (Double)params.getValue("costDecRate") * costBase;
		costResil = (Double)params.getValue("costResil");
		costRegen = (Double)params.getValue("costRegen") * costBase;
		costDisturbance = (Double)params.getValue("costDisturbance");
		trafficShortCoefficient = (Double)params.getValue("trafficShortCoefficient");
		trafficLongCoefficient = (Double)params.getValue("trafficLongCoefficient");
		
		int sourceID = sourceCenter.getID();
		int targetID = targetCenter.getID();
		

		if (sourceCenter.getBajo() == true | targetCenter.getBajo() ==  true) {
			System.out.println("Bajo");
			makeMountain(params, weight);
		}
		
		
		for (int i = 0; i < 17; i++) {
			if (sourceID == i | targetID == i) {
				makeMountain(params, weight);
			}
		}
		if ((sourceID-16)%17==0 & (targetID-16)%17==0) {
			this.weight = weight;
			makeRiver(params);
		}
		if ((sourceID-0)%17==0 & (targetID-0)%17==0) {
			this.weight = weight;
			makeRiver(params);
		}
		for (int i = 0; i < 16; i++) {
			if (sourceID == i & targetID == i + 1) {
				this.weight = weight;
				makeRiver(params);
			}
		}
		for (int i = 102; i < 106; i++) {
			if (sourceID == i & targetID == i + 1) {
				this.weight = weight;
				makeRiver(params);
			}
		}
		for (int i = 215; i < 220; i++) {
			if (sourceID == i & targetID == i + 1) {
				this.weight = weight;
				makeRiver(params);
			}
		}

		droughtMod = (Integer)params.getValue("disturbanceDelay");
		
		
		
	}
	

	@ScheduledMethod(start = 2, interval = 5)
	public void calcWeight() {
		
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		double pop1 = center1.getEndemic();
		double pop2 = center2.getEndemic();
		trafficShort = (pop1 + pop2) / weight;
		trafficFinal = trafficLongCoefficient * trafficLong + trafficShortCoefficient * trafficShort;
		
		double costCarryingFactor = 1 - 
				Math.pow((costMin/costEngineered), 2);
		double costDecrease = costCarryingFactor * trafficFinal * costDecRate;

		
		double costIncrease = 0;
		double costResFactor = 0;
		double costUtilFraction = 0;
		double trafficViaImprovement = 0;
		
		trafficViaImprovement = trafficFinal - costBase;
		if (trafficViaImprovement < 0) {
			trafficViaImprovement = 0;
		}		
		
		double costEngineeredImprovement = costBase - costEngineered;
		if (trafficViaImprovement < (int) costEngineeredImprovement) {
			costUtilFraction = trafficViaImprovement/costEngineeredImprovement;
		} else {
			costUtilFraction = 1;
		}
		costResFactor = 1 - (costUtilFraction + (1 - costUtilFraction ) * costResil);
		costIncrease = costResFactor * costIncRate;	
		
		
		costEmployable = costEmployable - costDecrease + costIncrease; 
    	if (costEmployable  < costMin) {
    		costEmployable  = costMin;
    	} else if (costEmployable > costMax) {
    		costEmployable = costMax;
    	}
    	if (costEmployable < costEngineered) {
    		costEngineered = costEmployable;
    	} else {
    		if (costEngineered < costBase) {
    			costEngineered += costRegen;
    		} else {
    			costEmployable -= costRegen;
    			costEngineered = costBase;
    		}
    	}
    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
    	if (tick > 10000+droughtMod & tick < 11000+droughtMod) {
    		weight = costEmployable*costDisturbance;
    	} else {
    		weight = costEmployable;
    	}	
	}
	
	private void makeRiver(Parameters params) {
		terrain = true;
		this.weight = weight / 15;
		costEmployable = weight/15;
		costEngineered = weight/15;
		costBase = weight/15;
		costMin = costBase * (Double)params.getValue("costMinFactor");
		costMax = costBase;
	}
	
	public void makeUplands() {
		terrain = true;
		weight = weight * 10;
		costEmployable =  weight;
		costEngineered =  weight;
		costBase =  weight;
		costMin =  weight/5;
		costMax =  weight;
		costDecRate = costDecRate / 2;
	}
	
	public void makeMountain(Parameters params, double weight) {
		terrain = true;
		this.weight = weight * 1000;
		costEmployable =  weight * 1000;
		costEngineered =  weight * 1000;
		costBase =  weight * 1000;
		costMin =  weight * 1000;
		costMax =  weight * 1000;
	}
	
	public double getTrafficLong() {
		return trafficLong;
	}
	
	public double getTrafficShort() {
		return trafficShort;
	}
	
	public double getTrafficScaled() {
		return trafficFinal;
	}

	public void setTrafficFinal() {
		

	}

//	public boolean isTerraIncognita() {
//		return terraIncognita;
//	}
//
//	public void setTerraIncognita(boolean terraIncognita) {
//		this.terraIncognita = terraIncognita;
//	}
//	
	public double getWeight() {
		return weight;
	}
	
	protected void setDirected(boolean directed) {
		this.directed = directed;
	}

	public T getSource() {
		return source;
	}
	
	public T getTarget() {
		return target;
	}

	public boolean isDirected() {
		return directed;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public double getCostIncrease() {
		return costIncrease;
	}

	public double getCostDecrease() {
		return costDecrease;
	}

	public double getCostEngineered() {
		return costEngineered;
	}
	
	public double getCostEmployable() {
		return costEmployable;
	}
	
	public double getCostBase() {
		return costBase;
	}
	
	public double getCostMin() {
		return costMin;
	}
	
	public double getCostMax() {
		return costMax;
	}

	public double get76cep(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costEmployable;
		} else return 0;
	}
	
	public double get76mx(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costMax;
		} else return 0;
	}
	
	public double get76mn(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costMin;
		} else return 0;
	}
	
	public double get76base(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costBase;
		} else return 0;
	}
	
	public double get76ceg(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costEngineered;
		} else return 0;
	}
	
	public double get76trafl(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficLong;
		} else return 0;
	}
	
	public double get76trafs(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficShort;
		} else return 0;
	}
	
	public double get76traff(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficFinal;
		} else return 0;
	}
	
	public void setTrafficLong(double tl){
		trafficLong = tl;
	}

	public Center getSourceCenter() {
		return sourceCenter;
	}

	public Center getTargetCenter() {
		return targetCenter;
	}
	
	public boolean getTerrain() {
		return terrain;
	}
}

//public T getNeighbor(T center) {
//	if (center.equals(this.target)) {
//		return this.source;
//	} else {
//		return this.target;
//	}
//}

//if (traffic > 0) {
//
//if (traffic >= trafficTop) {
//	trafficTop = traffic;
//	trafficUtilFraction = 1;
//	
//} else {
//	trafficUtilFraction = traffic / trafficTop;
//}
//} else {
//trafficUtilFraction = 1;
//} 
//if (weight <= 0) {
//trafficTop = 0;
//}
//
//costDamageTotal += costTrafficBloat;

//double popDamage = 0;
//if (costPopBloatRate > 0) {
//	double sourcePop = ((Center)source).getStaples();
//	double targetPop = ((Center)target).getStaples();
//	popDamage = sourcePop + targetPop;
//	if (popDamage > popDamageTop) {
//		popDamageTop = popDamage;
//	} else {
//		popDamageTop -= costRegen;
//	}
//}
//double costPopBloat = popDamageTop * costPopBloatRate;
//
//double weightAnthBloat = 0;
//if (weightAnthBloatRate > 0) {
//	double sourceBloat = ((Center)source).getFecundityEmployable();
//	double targetBloat = ((Center)target).getFecundityEmployable();
//	weightAnthBloat = (sourceBloat + targetBloat) * weightAnthBloatRate;
//}
//if (weightAnthBloat > costDamageAnth) {
//	costDamageAnth = weightAnthBloat;
//} else {
//	
//if (costDamageTotal > 0) {
//	costDamageFraction = trafficDamageTotal/costDamageTotal;
//} else {
//	costDamageFraction = .5;
//}
//	if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
//		System.out.println();
//		System.out.print("Cep is ");
//		System.out.println(costEmployable);
//		System.out.print("ccf is ");
//		System.out.println(costCarryingFactor);
//		System.out.print("id 1 is ");
//		System.out.println(((Center)source).getID());
//		System.out.print("id2 is ");
//		System.out.println(((Center)target).getID());
//		System.out.print("costBase is ");
//		System.out.println(costBase);
//		System.out.print("costEmployable is ");
//		System.out.println(costEmployable);
//		System.out.print("trafficShort is ");
//		System.out.println(trafficShort);
//		System.out.print("trafficLong is ");
//		System.out.println(trafficLong);
//		System.out.print("trafficFinalis ");
//		System.out.println(trafficFinal);
//		System.out.print("costTrafficScaleFactor is ");
//		System.out.println(costTrafficScaleFactor);
//		System.out.print("costUtilFraction is ");
//		System.out.println(costUtilFraction);
//		System.out.print("trafficViaImprovement is ");
//		System.out.println(trafficViaImprovement);
//		System.out.print("costResFactor is ");
//		System.out.println(costResFactor);
//		System.out.print("decrease is ");
//		System.out.println(costDecrease);
//		System.out.print("increase is");
//		System.out.println(costIncrease);
//		System.out.print("costEmployable is ");
//		System.out.println(costEmployable);
//	}