package mayagrations;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.RepastEdge;

public class Route<T> extends RepastEdge<T> {

//	DYNAMIC ENTITY VARIABLES
	private double weight;
	private double trafficFinal;
	private double trafficLong;
	private double trafficShort;
	private double costEmployable;
	private double costEngineered;
	private double costIncrease;
	private double costDecrease;
	
//	STATIC ENTITY VARIABLES
	private Center sourceCenter;
	private Center targetCenter;
	protected boolean directed;
	private double costBase;
	private double costMax;
	private double costMin;
	private String type;
	
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
		
//		DYNAMIC ENTITY VARIABLES
		
		this.weight = weight;
		trafficFinal = 0;
		trafficLong = 0;
		trafficShort = 0;
		costEmployable = weight;
		costEngineered = weight;
		costIncrease = 0;
		costDecrease = 0;
		
//		STATIC ENTITY VARIABLES
		this.source = source;
		this.target = target;
		sourceCenter = (Center) source;
		targetCenter = (Center) target;
	
		this.directed = directed;
		costMax = (Double)params.getValue("costMaxFactor");
		costMin = (Double)params.getValue("costMinFactor");
		
		type = "none";
		
//		STATIC GLOBAL VARIABLES
		costIncRate = (Double)params.getValue("costIncRate");
		costDecRate = (Double)params.getValue("costDecRate");
		costResil = (Double)params.getValue("costResil");
		costRegen = (Double)params.getValue("costRegen");
		costDisturbance = (Double)params.getValue("costDisturbance");
		trafficShortCoefficient = (Double)params.getValue("trafficShortCoefficient");
		trafficLongCoefficient = (Double)params.getValue("trafficLongCoefficient");
		droughtMod = (Integer)params.getValue("disturbanceDelay");
		
		int sourceID = sourceCenter.getID();
		int targetID = targetCenter.getID();
		

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
		if (type == "none") {
			makeBajo(weight);
		}

	}
	
	@ScheduledMethod(start = 2, interval = 5)
	public void calcWeight() {
//		if (!terrain) {
//			System.out.println();
//			System.out.print("Cp is ");
//			System.out.println(costEmployable);
//			System.out.print("Cg is ");
//			System.out.println(costEngineered);
//		}

		
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
		if (trafficViaImprovement < costEngineeredImprovement) {
			costUtilFraction = trafficViaImprovement/costEngineeredImprovement;
		} else {
			costUtilFraction = 1;
		}
		costResFactor = 1 - (costUtilFraction + (1 - costUtilFraction ) * costResil);
		costIncrease = costResFactor * costIncRate;	
		
		costEmployable = costEmployable - costDecrease + costIncrease; 
		
//		if (!terrain) {
//			System.out.print("trafficViaImprovement is ");
//			System.out.println(trafficViaImprovement);
//			System.out.print("costEngineeredImprovement is ");
//			System.out.println(costEngineeredImprovement);
//			System.out.print("costUtilFraction is ");
//			System.out.println(costUtilFraction);
//			System.out.print("costResFactor is ");
//			System.out.println(costResFactor);
//			System.out.print("increase is");
//			System.out.println(costIncrease);
//			System.out.print("Cp is ");
//			System.out.println(costEmployable);
//		}

		
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
    	
    	
    	
//		if (true) {
//			System.out.print("Cg is ");
//			System.out.println(costEngineered);
//			System.out.print("ccf is ");
//			System.out.println(costCarryingFactor);
//			System.out.print("id 1 is ");
//			System.out.println(((Center)source).getID());
//			System.out.print("id2 is ");
//			System.out.println(((Center)target).getID());
//			System.out.print("costBase is ");
//			System.out.println(costBase);
//			System.out.print("costEmployable is ");
//			System.out.println(costEmployable);
//			System.out.print("trafficShort is ");
//			System.out.println(trafficShort);
//			System.out.print("trafficLong is ");
//			System.out.println(trafficLong);
//			System.out.print("trafficFinal is ");
//			System.out.println(trafficFinal);
//			System.out.print("costUtilFraction is ");
//			System.out.println(costUtilFraction);
//			System.out.print("trafficViaImprovement is ");
//			System.out.println(trafficViaImprovement);
//			System.out.print("costResFactor is ");
//			System.out.println(costResFactor);
//			System.out.print("decrease is ");
//			System.out.println(costDecrease);
//			System.out.print("costEmployable is ");
//			System.out.println(costEmployable);
//			System.out.print("costBase is ");
//			System.out.println(costBase);
//			System.out.print("costMin is ");
//			System.out.println(costMin);
//			System.out.print("costMax is ");
//			System.out.println(costMax);
//		}

	}
	
	private void makeRiver(Parameters params) {
		type = "river";
	}
	
	public void makeUpland() {
		if (type != "river" & type != "mountain") {
			type = "upland";
		}
	}
	
	public void makeMountain(Parameters params, double weight) {
		type = "mountain";
	}
	
	public void makeBajo(double weight) {
		if (type == "none") {
			type = "bajo";
		}
	}
	
	public void initRiver() {
		weight = weight / 10;
		initStatic();
		initBase();
	}
	public void initMountain() {
		weight = weight * 1000;
		initStatic();
		initBase();
	}
	public void initUpland() {
		weight = weight*2;
		costMin =  weight/4;
		costMax =  weight;
		costDisturbance = 1;
		costResil = costResil * 1.5;
		initBase();
	}
	public void initBajo() {
		weight = weight/5;
		costMin =  weight/2;
		costMax =  weight*50;
		costResil = costResil * .7;
		initBase();
	}
	public void initBase() {
		costEmployable = weight;
		costEngineered = weight;
		costBase =  weight;
		costRegen = costRegen * weight;
		costDecRate = costDecRate * weight;
		costIncRate = costIncRate * weight;
	}
	
	public void initStatic() {
		costMin =  weight;
		costMax =  weight;
		costDisturbance = 1;
	}
	
	public String getType() {
		return type;
	}
	
	public double getWeightBajo() {
		if (type=="bajo") {
			return weight;
		} else {
			return 0;
		}
	}
	public double getCostEmployableBajo() {
		if (type=="bajo") {
			return costEmployable;
		} else {
			return 0;
		}
	}
	public double getCostEngineeredBajo() {
		if (type=="bajo") {
			return costEngineered;
		} else {
			return 0;
		}
	}
	public double getCostBaseBajo() {
		if (type=="bajo") {
			return costBase;
		} else {
			return 0;
		}
	}
	public double getTrafficShortBajo() {
		if (type=="bajo") {
			return trafficShort/20;
		} else {
			return 0;
		}
	}
	public double getTrafficLongBajo() {
		if (type=="bajo") {
			return trafficLong/20;
		} else {
			return 0;
		}
	}
	public double getTrafficFinalBajo() {
		if (type=="bajo") {
			return trafficFinal/20;
		} else {
			return 0;
		}
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

	public void setTrafficLong(double tl){
		trafficLong = tl;
	}

	public Center getSourceCenter() {
		return sourceCenter;
	}

	public Center getTargetCenter() {
		return targetCenter;
	}
}