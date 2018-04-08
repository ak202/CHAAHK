package mayagrations;

public class EntryRoute<T> extends Route<T> {
	
	private double weight;
	
	protected EntryRoute(){
	}

	public EntryRoute(T source, T target, boolean directed) {
		this(source, target, directed, 1);
	}

	public EntryRoute(T source, T target, boolean directed, double weight) {
		this.weight = weight;
		this.source = source;
		this.target = target;
	}
	
	public void calculateWeight() {
		return;
	}
	
	public double getWeight() {
		return 10;
	}
	
	public double getWeightShow() {
		return 0;
	}
	
	public double getCostBase() {
		return 0;
	}
	
	public double getCostPromotiveLevel() {
		return 0;
	}
	
	public double getCostDemotiveLevel() {
		return 0;
	}
	
	public double getCostPromotiveLevelShow() {
		return 0;
	}
	
	public double getCostDemotiveLevelShow() {
		return 0;
	}
	
}
