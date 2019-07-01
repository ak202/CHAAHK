package chaahk;

// One of four Routes that are always weight = 10. A wormhole of sorts connecting the four gateways
// to the extradimensional FakeCenter, which is used as a singular target to find the least cost path 
// from each Center to its nearest gateway in Region.calculateTrafficLong().
public class EntryRoute<T> extends Route<T> {
	
	private double weight;
	private Center sourceCenter;
	private Center targetCenter;
	
	protected EntryRoute(){
	}

	public EntryRoute(T source, T target, boolean directed) {
		this(source, target, directed, 1);
	}

	public EntryRoute(T source, T target, boolean directed, double weight) {
		this.weight = weight;
		this.source = source;
		sourceCenter = (Center) source;
		this.target = target;
		targetCenter = (Center) target;
	}
	
	public void calcWeight() {
		return;
	}
	
	public double getWeightShow() {
		return 0;
	}
	
	public double getCostPromotiveLevelShow() {
		return 0;
	}
	
	public double getCostDemotiveLevelShow() {
		return 0;
	}
	
	public Center getSourceCenter() {
		return sourceCenter;
	}

	public Center getTargetCenter() {
		return targetCenter;
	}

	//---------   General Cost:    ------------
	public double getWeight() {
		return 10;
	}
	public double getCostBase() {
		return 0;
	}
	//---------   Cost Promotive:    ------------
	public double getCostPromotiveLevel() {
		return 0;
	}
	public double getCostPromotiveMax() {
		return 0;
	}
	//---------   Cost Demotive:    ------------
	public double getCostDemotiveLevel() {
		return 0;
	}
	public double getCostDemotiveMax() {
		return 0;
	}
	//---------   General Traffic:    ------------
	public double getTrafficLong() {
		return 0;
	}
	public double getTrafficShort() {
		return 0;
	}
	//---------  Traffic Promotive:    ------------
	public double getTrafficPromotiveLong() {
		return 0;
	}
	public double getTrafficPromotiveShort() {
		return 0;
	}
	public double getTrafficPromtiveFinal() {
		return 0;
	}
	//---------  Traffic Demotive:    ------------
	public double getTrafficDemotiveLong() {
		return 0;
	}
	public double getTrafficDemotiveShort() {
		return 0;
	}
	public double getTrafficDemotiveFinal() {
		return 0;
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

	public String getType() {
		return "entry";
	}

}
