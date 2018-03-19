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
//		System.out.println("getting weight");
		return weight;
	}
}
