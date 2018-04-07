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

	
//	STATIC ENTITY VARIABLES
	private Center sourceCenter;
	private Center targetCenter;
	protected boolean directed;
	private double costBase;

	private String type;
	private int obSourceID;
	private int obTargetID;
	
//	STATIC GLOBAL VARIABLES
	private double costPromotiveLevel;
	private double costPromotiveRes;
	private double costPromotiveIncRate;
	private double costPromotiveDecRate;
	private double costPromotiveMax;
	
	private double costDemotiveLevel;
	private double costDemotiveRes;
	private double costDemotiveIncRate;
	private double costDemotiveDecRate;
	private double costDemotiveMax;
	private double disturbance;
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

//		STATIC ENTITY VARIABLES
		this.source = source;
		this.target = target;
		sourceCenter = (Center) source;
		targetCenter = (Center) target;
		obSourceID = (Integer)params.getValue("obSourceID");
		obTargetID = (Integer)params.getValue("obTargetID");
	
		this.directed = directed;

		
		type = "none";
		
//		STATIC GLOBAL VARIABLES
		costBase = (Double)params.getValue("costBase");
		costPromotiveLevel   = 0;
		costPromotiveRes     = (Double)params.getValue("costPromotiveRes");
		costPromotiveIncRate = (Double)params.getValue("costPromotiveIncRate")* weight;
		costPromotiveDecRate = (Double)params.getValue("costPromotiveDecRate") * weight;
		costPromotiveMax     = (Double)params.getValue("costPromotiveMax") * weight;
		
		costDemotiveLevel    = 0;
		costDemotiveRes      = (Double)params.getValue("costDemotiveRes");
		costDemotiveIncRate  = (Double)params.getValue("costDemotiveIncRate") * weight;
		costDemotiveDecRate  = (Double)params.getValue("costDemotiveDecRate") * weight;
		costDemotiveMax      = (Double)params.getValue("costDemotiveMax") * weight;

		disturbance = (Double)params.getValue("disturbance");
		trafficShortCoefficient = (Double)params.getValue("trafficShortCoefficient");
		trafficLongCoefficient = (Double)params.getValue("trafficLongCoefficient");
		droughtMod = (Integer)params.getValue("disturbanceDelay");
//		
//		int sourceID = sourceCenter.getID();
//		int targetID = targetCenter.getID();
//		

//		for (int i = 0; i < 17; i++) {
//			if (sourceID == i | targetID == i) {
//				makeMountain(params, weight);
//			}
//		}
//		if ((sourceID-16)%17==0 & (targetID-16)%17==0) {
//			this.weight = weight;
//			makeRiver(params);
//		}
//		if ((sourceID-0)%17==0 & (targetID-0)%17==0) {
//			this.weight = weight;
//			makeRiver(params);
//		}
//		for (int i = 0; i < 16; i++) {
//			if (sourceID == i & targetID == i + 1) {
//				this.weight = weight;
//				makeRiver(params);
//			}
//		}
//		for (int i = 102; i < 106; i++) {
//			if (sourceID == i & targetID == i + 1) {
//				this.weight = weight;
//				makeRiver(params);
//			}
//		}
//		for (int i = 215; i < 220; i++) {
//			if (sourceID == i & targetID == i + 1) {
//				this.weight = weight;
//				makeRiver(params);
//			}
//		}
		if (type == "none") {
			makeBajo(weight);
		}

	}
	
	@ScheduledMethod(start = 2, interval = 5)
	public void calcWeight() {
		
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		double pop1 = center1.getEndemic();
		double pop2 = center2.getEndemic();
		trafficShort = trafficShortCoefficient * ((pop1 + pop2) / weight);
		trafficLong = trafficLongCoefficient * trafficLong;
		trafficFinal = trafficLong + trafficShort;
		

		double costPromotiveCarryingFactor = 1 - Math.pow(costPromotiveLevel/costPromotiveMax, 2);
    	costPromotiveLevel += costPromotiveCarryingFactor * trafficFinal * costPromotiveIncRate;
		
    	double costPromotiveUtilFraction;
		if (trafficFinal < (int)costPromotiveLevel) {
			costPromotiveUtilFraction = trafficFinal/costPromotiveLevel;
		} else {
			costPromotiveUtilFraction = 1;
		}
    	double costPromotiveResFactor = 1 - (costPromotiveUtilFraction + (1 - costPromotiveUtilFraction ) * costPromotiveRes);

    	costPromotiveLevel -= costPromotiveResFactor * costPromotiveDecRate;
    	
    	if (costPromotiveLevel < 0) {
    		costPromotiveLevel = 0;
    	}
    	
		double costDemotiveCarryingFactor = 1 - Math.pow(costDemotiveLevel/costDemotiveMax, 2);
    	costDemotiveLevel += costDemotiveCarryingFactor * trafficFinal * costDemotiveIncRate;
		
		double costDemotiveUtilFraction;
		if (trafficFinal < (int)costDemotiveLevel) {
			costDemotiveUtilFraction = trafficFinal/costDemotiveLevel;
		} else {
			costDemotiveUtilFraction = 1;
		}
    	double costDemotiveResFactor = 1 - (costDemotiveUtilFraction + (1 - costDemotiveUtilFraction ) * costDemotiveRes);
    	costDemotiveLevel -= costDemotiveResFactor * costDemotiveDecRate;
    	if (costDemotiveLevel < 0) {
    		costDemotiveLevel = 0;
    	}
		
		weight = (int)(costBase-costPromotiveLevel+costDemotiveLevel);
    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
    	if (tick > 1000+droughtMod & tick < 1100+droughtMod) {
    		weight = Math.round((float)(weight/disturbance));
    	} 
    	
    	

//		double costCarryingFactor = 1 - 
//				Math.pow((costMin/costEngineered), 2);
//		double costDecrease = costCarryingFactor * trafficFinal * costDecRate;
//
//		
//		double costIncrease = 0;
//		double costResFactor = 0;
//		double costUtilFraction = 0;
//		double trafficViaImprovement = 0;
//		
//		double costEngineeredImprovement = costBase - costEngineered;
//		if (trafficFinal < costEngineeredImprovement) {
//			costUtilFraction = trafficViaImprovement/costEngineeredImprovement;
//		} else {
//			costUtilFraction = 1;
//		}
//		costResFactor = 1 - (costUtilFraction + (1 - costUtilFraction ) * costResil);
//		costIncrease = costResFactor * costIncRate;	
//		
//		costEmployable = costEmployable - costDecrease + costIncrease; 
//		
////		if (!terrain) {
////			System.out.print("trafficViaImprovement is ");
////			System.out.println(trafficViaImprovement);
////			System.out.print("costEngineeredImprovement is ");
////			System.out.println(costEngineeredImprovement);
////			System.out.print("costUtilFraction is ");
////			System.out.println(costUtilFraction);
////			System.out.print("costResFactor is ");
////			System.out.println(costResFactor);
////			System.out.print("increase is");
////			System.out.println(costIncrease);
////			System.out.print("Cp is ");
////			System.out.println(costEmployable);
////		}
//
//		
//    	if (costEmployable  < costMin) {
//    		costEmployable  = costMin;
//    	} else if (costEmployable > costMax) {
//    		costEmployable = costMax;
//    	}
//    	if (costEmployable < costEngineered) {
//    		costEngineered = costEmployable;
//    	} else {
//    		if (costEngineered < costBase) {
//    			costEngineered += costRegen;
//    		} else {
//    			costEmployable -= costRegen;
//    			costEngineered = costBase;
//    		}
//    	}
//    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
//    	if (tick > 1000+droughtMod & tick < 1100+droughtMod) {
//    		weight = costEmployable*(disturbance*costDisturbance);
//    	} else {
//    		weight = costEmployable;
//    	}	
//    	
    	
    	
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
	
	
	public void makeUpland() {
		if (type != "river" & type != "mountain") {
			type = "upland";
		}
	}

	public void makeBajo(double weight) {
		if (type == "none") {
			type = "bajo";
		}
	}
	
	public void initUpland() {
		weight = weight*2;
		costDemotiveMax =  weight/4;
		costPromotiveMax =  weight;
		disturbance = 1;
		costPromotiveRes = costPromotiveRes * 1.5;
		if (costPromotiveRes > 1) {
			costPromotiveRes = 1;
		}
		initBase();
	}
	public void initBajo() {
		weight = weight/5;
		costPromotiveMax =  weight/2;
		costDemotiveMax =  weight*50;
		initBase();
	}
	public void initBase() {
		costBase =  weight;
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
	public double getCostPromotiveLevelBajo() {
		if (type=="bajo") {
			return costPromotiveLevel;
		} else {
			return 0;
		}
	}
	public double getCostDemotiveLevelBajo() {
		if (type=="bajo") {
			return costDemotiveLevel;
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
	
	public double getCostDemotiveLevel() {
		return costDemotiveLevel;
	}
	
	public double getCostPromotiveLevel() {
		return costPromotiveLevel;
	}
	
	public double getCostBase() {
		return costBase;
	}
	
	public double getCostPromotiveMax() {
		return costPromotiveMax;
	}
	
	public double getCostDemotiveMax() {
		return costDemotiveMax;
	}
	
	public double get76cpl(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costPromotiveLevel;
		} else return 0;
	}
	
	public double get76dmx(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costDemotiveMax;
		} else return 0;
	}
	
	public double get76pmx(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costPromotiveMax;
		} else return 0;
	}
	
	public double get76base(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costBase;
		} else return 0;
	}
	
	public double get76cdl(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costDemotiveLevel;
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
}