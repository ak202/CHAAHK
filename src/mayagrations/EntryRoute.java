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
	
	public void calcWeight() {
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
			return 0;
		}
		//---------   Cost Promotive:    ------------
		public double getAllCpl() {
			return 0;
		}
		public double getAllCplShow() {
			return 0;
		}
		public double getAllCpm() {
			return 0;
		}
		//---------   Cost Demotive:    ------------
		public double getAllCdl() {
			return 0;
		}
		public double getAllCdlShow() {
			return 0;
		}
		public double getAllCdm() {
			return 0;
		}
		//---------   General Traffic:    ------------
		public double getAllTl() {
			return 0;
		}
		public double getAllTs() {
			return 0;
		}
		//---------  Traffic Promotive:    ------------
		public double getAllTpl() {
			return 0;
		}
		public double getAllTps() {
			return 0;
		}
		public double getAllTpf() {
			return 0;
		}
		//---------  Traffic Demotive:    ------------
		public double getAllTdl() {
			return 0;
		}
		public double getAllTds() {
			return 0;
		}
		public double getAllTdf() {
			return 0;
		}
		
		
		//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
		//----------------    *Type* Routes:         -----------------------
		//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
				
		//---------   General Cost:    ------------
		public double getTypeW() {
			return 0;
		}
		public double getTypeCb() {
			return 0;
		}
		//---------   Cost Promotive:    ------------
		public double getTypeCpl() {
			return 0;
		}
		public double getTypeCplShow() {
			return 0;
		}
		public double getTypeCpm() {
			return 0;
		}
		//---------   Cost Demotive:    ------------
		public double getTypeCdl() {
			return 0;
		}
		public double getTypeCdlShow() {
			return 0;
		}
		public double getTypeCdm() {
			return 0;
		}
		//---------   General Traffic:    ------------
		public double getTypeTs() {
			return 0;
		}
		public double getTypeTl() {
			return 0;
		}
		//---------  Traffic Promotive:    ------------
		public double getTypeTpl(){
			return 0;
		}
		public double getTypeTps(){
			return 0;
		}
		public double getTypeTpf(){
			return 0;
		}
		//---------  Traffic Demotive:    ------------
		public double getTypeTdl(){
			return 0;
		}
		public double getTypeTds(){
			return 0;
		}
		public double getTypeTdf(){
			return 0;
		}
		
		//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
		//----------------    Observed Route:         ----------------------
		//''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
		
		//---------   General Cost:    ------------
		public double getObW(){
			return 0;
		}
		public double getObCb(){
			return 0;
		}
		//---------   Cost Promotive:    ------------
		public double getObCpl(){
			return 0;
		}
		public double getObCplShow(){
			return 0;
		}
		public double getObCpm(){
			return 0;
		}
		//---------   Cost Demotive:    ------------
		public double getObCdl(){
			return 0;
		}
		public double getObCdlShow(){
			return 0;
		}
		public double getObCdm(){
			return 0;
		}
		//---------   General Traffic:    ------------
		public double getObTl(){
			return 0;
		}
		public double getObTs(){
			return 0;
		}
		//---------  Traffic Promotive:    ------------
		public double getObTpl(){
			return 0;
		}
		public double getObTps(){
			return 0;
		}
		public double getObTpf(){
			return 0;
		}
		//---------  Traffic Demotive:    ------------
		public double getObTdl(){
			return 0;
		}
		public double getObTds(){
			return 0;
		}
		public double getObTdf(){
			return 0;
		}
		
		public String getType() {
			return "entry";
		}
	
}
