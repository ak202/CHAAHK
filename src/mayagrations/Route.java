package mayagrations;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.RepastEdge;

public class Route<T> extends RepastEdge<T> {
	
//	MISC VARIABLES
	private int obSourceID;
	private Center sourceCenter;
	private int obTargetID;
	private Center targetCenter;
	protected boolean directed;
	private String type;
	
//	WEIGHT-RELATED
	private double weight;
	private double costBase;
	
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

	private double trafficShort;
	private double trafficShortCoefficient;
	private double trafficLong;
	private double trafficLongCoefficient;
	private double trafficFinal;

	protected Route(){
	}

	public Route(T source, T target, boolean directed) {
		this(source, target, directed, 1);
	}

	public Route(T source, T target, boolean directed, double weight) {

		Parameters params = RunEnvironment.getInstance().getParameters();
		
//		MISC VARIABLES
		this.source = source;
		obSourceID = (Integer)params.getValue("obSourceID");
		sourceCenter = (Center) source;
		this.target = target;
		obTargetID = (Integer)params.getValue("obTargetID");
		targetCenter = (Center) target;
		this.directed = directed;
		type = "none";

//		WEIGHT-RELATED
		
		this.weight = weight;
		costBase = weight;
		
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
		
		trafficShort = 0;
		trafficShortCoefficient = (Double)params.getValue("trafficShortCoefficient");
		trafficLong = 0;
		trafficLongCoefficient = (Double)params.getValue("trafficLongCoefficient");
		trafficFinal = 0;

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
		trafficShort = (pop1 + pop2) / weight;
		trafficFinal = trafficLongCoefficient *trafficLong + trafficShortCoefficient * trafficShort;
		
		double costPromotiveCarryingFactor;
		if (costPromotiveLevel < costPromotiveMax){
			costPromotiveCarryingFactor = 1 - costPromotiveLevel/costPromotiveMax;
		} else{
			costPromotiveCarryingFactor = 0;
		} 
    	double cplIncrease = costPromotiveCarryingFactor * trafficFinal * costPromotiveIncRate;
    	costPromotiveLevel += cplIncrease;
    	double costPromotiveUtilFraction;
		if (trafficFinal < costPromotiveLevel) {
			costPromotiveUtilFraction = trafficFinal/costPromotiveLevel;
		} else {
			costPromotiveUtilFraction = 1;
		}
    	double costPromotiveResFactor = 1 - (costPromotiveUtilFraction + (1 - costPromotiveUtilFraction ) * costPromotiveRes);

    	double cplDecrease = costPromotiveResFactor * costPromotiveDecRate;
    	costPromotiveLevel -= cplDecrease;
    	
    	if (costPromotiveLevel < 0) {
    		costPromotiveLevel = 0;
    	} else if (costPromotiveLevel > costPromotiveMax) {
    		costPromotiveLevel = costPromotiveMax;
    	}
    	
    	double costDemotiveCarryingFactor;
		if (costDemotiveLevel < costDemotiveMax){
			costDemotiveCarryingFactor = 1 - costDemotiveLevel/costDemotiveMax;
		} else{
			costDemotiveCarryingFactor = 0;
		} 
    	double cdlIncrease = costDemotiveCarryingFactor * trafficFinal * costDemotiveIncRate;
    	costDemotiveLevel += cdlIncrease;
    	double costDemotiveUtilFraction;
		if (trafficFinal < costDemotiveLevel) {
			costDemotiveUtilFraction = trafficFinal/costDemotiveLevel;
		} else {
			costDemotiveUtilFraction = 1;
		}
    	double costDemotiveResFactor = 1 - (costDemotiveUtilFraction + (1 - costDemotiveUtilFraction ) * costDemotiveRes);

    	double cdlDecrease = costDemotiveResFactor * costDemotiveDecRate;
    	costDemotiveLevel -= cdlDecrease;
    	
    	if (costDemotiveLevel < 0) {
    		costDemotiveLevel = 0;
    	} else if (costDemotiveLevel > costDemotiveMax) {
    		costDemotiveLevel = costDemotiveMax;
    	}

    	double costPromotiveFraction;
    	double costDemotiveFraction;
    	
    	if (costPromotiveMax == 0) {
    		costPromotiveFraction = 0;
    	} else {
    		costPromotiveFraction = costPromotiveLevel/costPromotiveMax;
    	}
    	
    	if (costDemotiveMax == 0) {
    		costDemotiveFraction = 0;
    	} else {
    		costDemotiveFraction = costDemotiveLevel/costDemotiveMax;
    	}
    	
    	if (costPromotiveFraction > costDemotiveFraction) {
    		costPromotiveFraction = costPromotiveFraction - costDemotiveFraction;
    		weight = costBase - costPromotiveFraction * costPromotiveMax;
    	} else {
    		costDemotiveFraction = costDemotiveFraction - costPromotiveFraction;
    		weight = costBase + costDemotiveFraction * costDemotiveMax;
    	} 
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
		costDemotiveMax =  0;
		costPromotiveMax =  .5 * weight;
		costPromotiveRes = .4;
		initBase();
	}
	public void initBajo() {
		
		weight = weight /5;
		Parameters params = RunEnvironment.getInstance().getParameters();
		costPromotiveIncRate = (Double)params.getValue("costPromotiveIncRate")* weight;
		costPromotiveDecRate = (Double)params.getValue("costPromotiveDecRate") * weight;
		costPromotiveMax     = (Double)params.getValue("costPromotiveMax") * weight;
		
		costDemotiveRes      = (Double)params.getValue("costDemotiveRes");
		costDemotiveIncRate  = (Double)params.getValue("costDemotiveIncRate") * weight;
		costDemotiveDecRate  = (Double)params.getValue("costDemotiveDecRate") * weight;
		costDemotiveMax      = (Double)params.getValue("costDemotiveMax") * weight;
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
			return costBase - costPromotiveLevel;
		} else {
			return 0;
		}
	}
	public double getCostDemotiveLevelBajo() {
		if (type=="bajo") {
			return costBase + costDemotiveLevel;
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
			return trafficShort * trafficShortCoefficient;
		} else {
			return 0;
		}
	}
	public double getTrafficLongBajo() {
		if (type=="bajo") {
			return trafficLong * trafficLongCoefficient;
		} else {
			return 0;
		}
	}
	public double getTrafficFinalBajo() {
		if (type=="bajo") {
			return trafficFinal;
		} else {
			return 0;
		}
	}
	
	public double getTrafficLong() {
		return trafficLong;
	}
	
	public double getTrafficLongShow() {
		return trafficLong * trafficLongCoefficient;
	}
	
	public double getTrafficShort() {
		return trafficShort;
	}
	
	public double getTrafficShortShow() {
		return trafficShort * trafficShortCoefficient;
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
	
	public double getCostDemotiveLevelShow() {
		return (costBase + costDemotiveLevel);
	}
	
	public double getCostPromotiveLevelShow() {
		return costBase - costPromotiveLevel;
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
	
	public double getObCpl(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costPromotiveLevel;
		} else return 0;
	}
	
	public double getObDmx(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costDemotiveMax;
		} else return 0;
	}
	
	public double getObPmx(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costPromotiveMax;
		} else return 0;
	}
	
	public double getObBase(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costBase;
		} else return 0;
	}
	
	public double getWeightShow() {
		return weight;
	}
	
	public double getObCdl(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return costDemotiveLevel;
		} else return 0;
	}
	
	public double getObTrafL(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficLong;
		} else return 0;
	}
	
	public double getObTrafLShow(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficLong * trafficLongCoefficient;
		} else return 0;
	}
	
	public double getObTrafS(){
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			return trafficShort;
		} else return 0;
	}
	
	public double getObTrafF(){
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
	
	private void printOb(String phrase, double number) {
		Center center1 = (Center) source;
		Center center2 = (Center) target;

		if (center1.getID()==obSourceID & center2.getID()==obTargetID) {
			System.out.print(phrase);
			System.out.print(" is ");
			System.out.println(number);
		}
	}	
	private void print(String phrase, double number) {
		System.out.print(phrase);
		System.out.print(" is ");
		System.out.println(number);
	}
	
	private void print(String phrase, double number, boolean bool) {
		if (bool) {
			System.out.print(phrase);
			System.out.print(" is ");
			System.out.println(number);
		}
	}
	

}