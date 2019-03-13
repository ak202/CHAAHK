package chaahk;

import java.awt.Color;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.RepastEdge;

public class Route<T> extends RepastEdge<T> {
	
//	MISC VARIABLES
	
	private boolean observed;
	private Center sourceCenter;
	private Center targetCenter;
	protected boolean directed;
	private String type;
	
//	WEIGHT-RELATED

	private double weight;
	private double costBase;
	private double trafficLong;
	private double trafficShort;
	
	private double costPromotiveLevel;
	private double costPromotiveRes;
	private double costPromotiveIncRate;
	private double costPromotiveDecRate;
	private double costPromotiveMax;
	private double trafficPromotiveShort;
	private double trafficPromotiveShortCoefficient;
	private double trafficPromotiveLong;
	private double trafficPromotiveLongCoefficient;
	private double trafficPromotiveFinal;
	
	private double costDemotiveLevel;
	private double costDemotiveRes;
	private double costDemotiveIncRate;
	private double costDemotiveDecRate;
	private double costDemotiveMax;
	private double trafficDemotiveShort;
	private double trafficDemotiveShortCoefficient;
	private double trafficDemotiveLong;
	private double trafficDemotiveLongCoefficient;
	private double trafficDemotiveFinal;

	protected Route(){
	}

	public Route(T source, T target, boolean directed) {
		this(source, target, directed, 1);
	}

	public Route(T source, T target, boolean directed, double weight) {

		Parameters params = RunEnvironment.getInstance().getParameters();
		
//		MISC VARIABLES
		int obSourceID = (Integer)params.getValue("obSourceID"); 
		int obTargetID = (Integer)params.getValue("obTargetID"); 
		this.source = source;
		sourceCenter = (Center) source;
		this.target = target;
		targetCenter = (Center) target;
		boolean obSame = (sourceCenter.getID()==obSourceID & targetCenter.getID()==obTargetID);
		boolean obReverse = (sourceCenter.getID()==obTargetID & targetCenter.getID()==obSourceID);
		if ( obSame | obReverse ) {
			observed = true;
		} else {
			observed = false;
		}
		this.directed = directed;
		type = "none";

//		WEIGHT-RELATED
		
		this.weight = weight;
		costBase = weight;
		trafficLong = 0;
		trafficShort = 0;
		
		costPromotiveLevel   = 0;
		costPromotiveRes     = (Double)params.getValue("costPromotiveRes");
		trafficPromotiveShort             = 0;                                                            
		trafficPromotiveShortCoefficient  = (Double)params.getValue("trafficPromotiveShortCoefficient") * costPromotiveMax;  
		trafficPromotiveLong              = 0;                                                            
		trafficPromotiveLongCoefficient   = (Double)params.getValue("trafficPromotiveLongCoefficient") * costPromotiveMax;   
		trafficPromotiveFinal             = 0;                                                            
        
		
		costDemotiveLevel    = 0;
		costPromotiveMax     = (Double)params.getValue("costDemotiveMax") * weight;
		costDemotiveRes      = (Double)params.getValue("costDemotiveRes");
		costDemotiveIncRate  = (Double)params.getValue("costDemotiveIncRate") * costDemotiveMax;
		costDemotiveDecRate  = (Double)params.getValue("costDemotiveDecRate") * costDemotiveMax;
		trafficDemotiveShort             = 0;
		trafficDemotiveShortCoefficient  = (Double)params.getValue("trafficDemotiveShortCoefficient") * costDemotiveMax;
		trafficDemotiveLong              = 0;
		trafficDemotiveLongCoefficient   = (Double)params.getValue("trafficDemotiveLongCoefficient") * costDemotiveMax;
		trafficDemotiveFinal             = 0;

		if (type == "none") {
			makeBajo();
		}
	}
	
	@ScheduledMethod(start = 2, interval = 5)
	public void calculateWeight() {

		// part 1 - calulation of trafficShort. very simple gravity model using the labor of 2 adjacent Centers.
		
		Center center1 = (Center) source;
		Center center2 = (Center) target;
		double pop1 = center1.getLabor();
		double pop2 = center2.getLabor();
		trafficShort = (pop1 + pop2) / costBase;
		
		
		//part 2 - costPromotiveLevel is modified 

				// calculate tpf
		trafficPromotiveLong = trafficLong * trafficPromotiveLongCoefficient;
		trafficPromotiveShort = trafficShort * trafficPromotiveShortCoefficient;
		trafficPromotiveFinal = trafficPromotiveLong + trafficPromotiveShort;

				// increase
		double costPromotiveCarryingFactor;
		if (costPromotiveLevel < costPromotiveMax) {
			costPromotiveCarryingFactor = 1 - costPromotiveLevel/costPromotiveMax;
		} else {
			costPromotiveCarryingFactor = 0;
		} 
		double cplIncrease = costPromotiveCarryingFactor * trafficPromotiveFinal * costPromotiveIncRate;
		costPromotiveLevel += cplIncrease;

				// decrease	
		double trafficPromotiveDecFinal = trafficPromotiveFinal*costPromotiveMax;
		double costPromotiveUtilFraction;
		if (trafficPromotiveDecFinal < costPromotiveLevel) {
			costPromotiveUtilFraction = trafficPromotiveDecFinal/costPromotiveLevel;
		} else {
			costPromotiveUtilFraction = 1;
		}
		double costPromotiveResFactor = 1 - (costPromotiveUtilFraction + (1 - costPromotiveUtilFraction ) * costPromotiveRes);
		double cplDecrease = costPromotiveResFactor * costPromotiveDecRate;
		costPromotiveLevel -= cplDecrease;

				// readjust	
		if (costPromotiveLevel < 0) {
			costPromotiveLevel = 0;
		} else if (costPromotiveLevel > costPromotiveMax) {
			costPromotiveLevel = costPromotiveMax;
		}
		
		// part 3 - costDemotiveLevel is modified

				// calculate tdf
		trafficDemotiveLong = trafficLong * trafficDemotiveLongCoefficient;
		trafficDemotiveShort = trafficShort * trafficDemotiveShortCoefficient;
		trafficDemotiveFinal = trafficDemotiveLong + trafficDemotiveShort;
		 
				//increase
		double costDemotiveCarryingFactor;
		if (costDemotiveLevel < costDemotiveMax)
		{
			costDemotiveCarryingFactor = 1 - costDemotiveLevel/costDemotiveMax;
		} else {
			costDemotiveCarryingFactor = 0;
		} 
		double cdlIncrease = costDemotiveCarryingFactor * trafficDemotiveFinal * costDemotiveIncRate;
		costDemotiveLevel += cdlIncrease;

				//decrease	
		double trafficDemotiveDecFinal = trafficDemotiveFinal*costDemotiveMax;
		double costDemotiveUtilFraction;
		if (trafficDemotiveDecFinal < costDemotiveLevel) {
			costDemotiveUtilFraction = trafficDemotiveDecFinal/costDemotiveLevel;
		} else {
			costDemotiveUtilFraction = 1;
		}
		double costDemotiveResFactor = 1 - (costDemotiveUtilFraction + (1 - costDemotiveUtilFraction ) * costDemotiveRes);
		double cdlDecrease = costDemotiveResFactor * costDemotiveDecRate;
		costDemotiveLevel -= cdlDecrease;

				//readjust
		if (costDemotiveLevel < 0) {
			costDemotiveLevel = 0;
		} else if (costDemotiveLevel > costDemotiveMax) {
			costDemotiveLevel = costDemotiveMax;
		}
		
		// part 4 - weight is drawn away from costBase depending on whether cpl or cdl is closer to their max (cpm, cdm)

		double costPromotiveFraction;
		double costDemotiveFraction;
		
				// calculate both relative levels
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
		
				// update weight
		if (costPromotiveFraction > costDemotiveFraction) {
			costPromotiveFraction = costPromotiveFraction - costDemotiveFraction;
			weight = costBase - costPromotiveFraction * costPromotiveMax;
		} else {
			costDemotiveFraction = costDemotiveFraction - costPromotiveFraction;
			weight = costBase + costDemotiveFraction * costDemotiveMax;
		} 
	
	}
	
	// displays a bajo Route lower than its costBase as more red, more green if higher.
	public Color getColorBajo() {
		double  r = 255;
		double  g = 255;
		double b = 0;
		if (weight > costBase) {
			double actualDif = weight - costBase;
			double colorFraction = 1 - (actualDif/(costBase*5));
			if (colorFraction > 1) {
				colorFraction = 1;
			} else if (colorFraction < 0) {
				colorFraction = 0;
			}
			g = g * colorFraction;
		} else if (weight < costBase) {
			double actualDif = costBase - weight;
			double colorFraction = 1-(actualDif/(costBase*.5));
			r = r * colorFraction;
		}
		Color color = new Color((int)r,(int)g,(int)b);
		return color;
	}
    
	// upland routes are black
	public Color getColorUpland() {
		double  r = 0;
		double  g = 0;
		double b = 0;
		Color color = new Color((int)r,(int)g,(int)b);
		return color;
	}
    
	// makes route a nominal upland	
	public void makeUpland() {
		if (type != "river" & type != "mountain") {
			type = "upland";
		}
	}
	// give route upland behavior
	public void initUpland() {
		costBase =  weight;
		Parameters params = RunEnvironment.getInstance().getParameters();
		costPromotiveIncRate = (Double)params.getValue("costPromotiveIncRate") * weight;
		costPromotiveDecRate = (Double)params.getValue("costPromotiveDecRate") * weight;
		costPromotiveMax     = 0;
		costDemotiveIncRate  = 0;
		costDemotiveDecRate  = 0;
		costDemotiveMax      = 0;
	}
	// makes route a nominal bajo
	public void makeBajo() {
		if (type == "none") {
			type = "bajo";
		}
	}
	// give route bajo behavior
	public void initBajo() {
		weight = weight /5;
		costBase =  weight;
		Parameters params = RunEnvironment.getInstance().getParameters();
		costPromotiveMax     = (Double)params.getValue("costPromotiveMax") * weight;
		costPromotiveIncRate = (Double)params.getValue("costPromotiveIncRate") * costPromotiveMax;
		costPromotiveDecRate = (Double)params.getValue("costPromotiveDecRate") * costPromotiveMax;
		trafficPromotiveShortCoefficient  = (Double)params.getValue("trafficPromotiveShortCoefficient");                                                        
		trafficPromotiveLongCoefficient   = (Double)params.getValue("trafficPromotiveLongCoefficient");
		costDemotiveMax      = (Double)params.getValue("costDemotiveMax") * weight;
		costDemotiveIncRate  = (Double)params.getValue("costDemotiveIncRate") * costDemotiveMax;
		costDemotiveDecRate  = (Double)params.getValue("costDemotiveDecRate") * costDemotiveMax;
		trafficDemotiveShortCoefficient  = (Double)params.getValue("trafficDemotiveShortCoefficient");
		trafficDemotiveLongCoefficient   = (Double)params.getValue("trafficDemotiveLongCoefficient");
	}
	
	public String getType() {
		return type;
	}

	public double getTrafficLong() {
		return trafficLong;
	}
	
	public double getWeight() {
		return weight;
	}

	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	//-----------------    Aggregate Data Getter Methods for...    --------------------------
	//'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	//----------------    All Routes:         --------------------------
	//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	//---------   General Cost:    ------------
	public double getAllW() {
		return weight;
	}
	public double getAllCb() {
		return costBase;
	}
	//---------   Cost Promotive:    ------------
	public double getAllCpl() {
		return costPromotiveLevel;
	}
	public double getAllCplShow() {
		return costBase - costPromotiveLevel;
	}
	public double getAllCpm() {
		return costPromotiveMax;
	}
	//---------   Cost Demotive:    ------------
	public double getAllCdl() {
		return costDemotiveLevel;
	}
	public double getAllCdlShow() {
		return (costBase + costDemotiveLevel);
	}
	public double getAllCdm() {
		return costDemotiveMax;
	}
	//---------   General Traffic:    ------------
	public double getAllTl() {
		return trafficLong;
	}
	public double getAllTs() {
		return trafficShort;
	}
	//---------  Traffic Promotive:    ------------
	public double getAllTpl() {
		return trafficLong * trafficPromotiveLong;
	}
	public double getAllTps() {
		return trafficLong * trafficPromotiveLong;
	}
	public double getAllTpf() {
		return trafficPromotiveFinal;
	}
	//---------  Traffic Demotive:    ------------
	public double getAllTdl() {
		return trafficLong * trafficDemotiveLong;
	}
	public double getAllTds() {
		return trafficLong * trafficDemotiveLong;
	}
	public double getAllTdf() {
		return trafficDemotiveFinal;
	}
	
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	//----------------    *Type* Routes:         -----------------------
	//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	private double typeGetter(double variable) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		String observedType = (String)params.getValue("observedType");

		if (type.equals(observedType)) {
			return variable;
		} else {
			return 0;
		}
	}
	
	//---------   General Cost:    ------------
	public double getTypeW() {
		return typeGetter(weight);
	}
	public double getTypeCb() {
		return typeGetter(costBase);
	}
	//---------   Cost Promotive:    ------------
	public double getTypeCpl() {
		return typeGetter(costPromotiveLevel);
	}
	public double getTypeCplShow() {
		return typeGetter(costBase - costPromotiveLevel);
	}
	public double getTypeCpm() {
		return typeGetter(costPromotiveMax);
	}
	//---------   Cost Demotive:    ------------
	public double getTypeCdl() {
		return typeGetter(costDemotiveLevel);
	}
	public double getTypeCdlShow() {
		return typeGetter(costBase + costDemotiveLevel);
	}
	public double getTypeCdm() {
		return typeGetter(costDemotiveMax);
	}
	//---------   General Traffic:    ------------
	public double getTypeTs() {
		return typeGetter(trafficShort);
	}
	public double getTypeTl() {
		return typeGetter(trafficLong);
	}
	//---------  Traffic Promotive:    ------------
	public double getTypeTpl(){
		return typeGetter(trafficPromotiveLong);
	}
	public double getTypeTps(){
		return typeGetter(trafficPromotiveShort);
	}
	public double getTypeTpf(){
		return typeGetter(trafficPromotiveFinal);
	}
	//---------  Traffic Demotive:    ------------
	public double getTypeTdl(){
		return typeGetter(trafficDemotiveLong);
	}
	public double getTypeTds(){
		return typeGetter(trafficDemotiveShort);
	}
	public double getTypeTdf(){
		return typeGetter(trafficDemotiveFinal);
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	//----------------    Observed Route:         ----------------------
	//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	//---------   General Cost:    ------------
	public double getObW(){
		if (observed) {
			return weight;
		} else return 0;
	}
	public double getObCb(){
		if (observed) {
			return costBase;
		} else return 0;
	}
	//---------   Cost Promotive:    ------------
	public double getObCpl(){
		if (observed) {
			return costPromotiveLevel;
		} else return 0;
	}
	public double getObCplShow(){
		if (observed) {
			return costBase - costPromotiveLevel;
		} else return 0;
	}
	public double getObCpm(){
		if (observed) {
			return costPromotiveMax;
		} else return 0;
	}
	//---------   Cost Demotive:    ------------
	public double getObCdl(){
		if (observed) {
			return costDemotiveLevel;
		} else return 0;
	}
	public double getObCdlShow(){
		if (observed) {
			return costBase + costDemotiveLevel;
		} else return 0;
	}
	public double getObCdm(){
		if (observed) {
			return costDemotiveMax;
		} else return 0;
	}
	//---------   General Traffic:    ------------
	public double getObTl(){
		if (observed) {
			return trafficLong;
		} else return 0;
	}
	public double getObTs(){
		if (observed) {
			return trafficShort;
		} else return 0;
	}
	//---------  Traffic Promotive:    ------------
	public double getObTpl(){
		if (observed) {
			return trafficPromotiveLong;
		} else return 0;
	}
	public double getObTps(){
		if (observed) {
			return trafficPromotiveShort;
		} else return 0;
	}
	public double getObTpf(){
		if (observed) {
			return trafficPromotiveFinal;
		} else return 0;
	}
	//---------  Traffic Demotive:    ------------
	public double getObTdl(){
		if (observed) {
			return trafficDemotiveLong;
		} else return 0;
	}
	public double getObTds(){
		if (observed) {
			return trafficDemotiveShort;
		} else return 0;
	}
	public double getObTdf(){
		if (observed) {
			return trafficDemotiveFinal;
		} else return 0;
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
	protected void setDirected(boolean directed) {
		this.directed = directed;
	}

	public void setWeight(double weight) {
		this.weight = weight;
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
