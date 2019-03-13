package chaahk;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import cern.jet.random.Binomial;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import java.lang.Math;

public class Center {
	
//	MISC VARIABLES
	
	private int id;							//excluded
	private Context<Object> context;
	private boolean upland;
	private boolean water;
	private boolean gateway;
	
//	PEOPLE-RELATED
	
	private int labor; //L
	private List<Group> residents; 			
	private int groupsEndemic; //B
	private double pull;				
	private double pullFraction;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	private double infertility;
	
//	STAPLES-RELATED
	
	private int staples;
	private int staplesShow;
	private double fecundityBase; 
	
	private double fecundityPromotiveLevel;
	private double fecundityPromotiveRes;
	private double fecundityPromotiveIncRate;
	private double fecundityPromotiveDecRate;
	private double fecundityPromotiveMax;
	
	private double fecundityDemotiveLevel;
	private double fecundityDemotiveRes;
	private double fecundityDemotiveIncRate;
	private double fecundityDemotiveDecRate;
	private double fecundityDemotiveMax;
	
//	IMPORTS-RELATED
	
	private double imports; //I				
	private double importsCoefficient;
	private double importsYIntercept;
	private double distToExporter; //D
	private List<Route<Object>> path;		//excluded
	
//	ANALYTICAL METRICS
	private int emigrations;
	private int stayed;
	private int stapleRemovals;
	private int importRemovals;
	private int created;
	private int settled;
	
	public Center(int id, Context<Object> context) {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
//		MISC VARIABLES		
		
		this.id = id;						//excluded
		this.context = context;
		upland = false;
		water = true;
		gateway = false;
		
//		PEOPLE-RELATED
		
		labor = 0;
		residents = new ArrayList<Group>();	
		groupsEndemic = 0;
		pull = 0;
		pullFraction = 0;
		destinations = null;
		pullFractions = null;
		infertility = (Integer)params.getValue("infert");

//		STAPLES-RELATED
		fecundityBase = 3;
		
		fecundityPromotiveLevel   = 0;
		fecundityPromotiveMax     = (Double)params.getValue("fecundityPromotiveMax");
		fecundityPromotiveRes     = (Double)params.getValue("fecundityPromotiveRes");
		fecundityPromotiveIncRate = (Double)params.getValue("fecundityPromotiveIncRate") * fecundityPromotiveMax;
		fecundityPromotiveDecRate = (Double)params.getValue("fecundityPromotiveDecRate") * fecundityPromotiveMax;
		
		fecundityDemotiveLevel    = 0;
		fecundityDemotiveMax      = (Double)params.getValue("fecundityDemotiveMax");
		fecundityDemotiveRes      = (Double)params.getValue("fecundityDemotiveRes");
		fecundityDemotiveIncRate  = (Double)params.getValue("fecundityDemotiveIncRate")	* fecundityDemotiveMax;
		fecundityDemotiveDecRate  = (Double)params.getValue("fecundityDemotiveDecRate") * fecundityDemotiveMax;
		
//		IMPORTS-RELATED	
		
		imports = 0.0;						
		importsCoefficient = (Double)params.getValue("importsCoefficient");
		importsYIntercept = (Double)params.getValue("importsYIntercept");
		distToExporter = 0;
		path = null;

//		ANALYTICAL METRICS
		
		emigrations = 0;
		stapleRemovals = 0;
		importRemovals = 0;
		created = 0;
		settled = 0;		
		
	}
	
	public void calculateStaples() {
    	    	
		if (water) return;
		
		// part 1 Fpl modified
 
				// incease
		double fecundityPromotiveCarryingFactor = 1 - Math.pow(fecundityPromotiveLevel/fecundityPromotiveMax, 2);
		fecundityPromotiveLevel += fecundityPromotiveCarryingFactor * labor * fecundityPromotiveIncRate;	
				// decrease
		double fecundityPromotiveUtilFraction;
		if (labor < (int)fecundityPromotiveLevel) {
			fecundityPromotiveUtilFraction = labor/fecundityPromotiveLevel;
		} else {
			fecundityPromotiveUtilFraction = 1;
		}
		double fecundityPromotiveResFactor = 1 - (fecundityPromotiveUtilFraction + (1 - fecundityPromotiveUtilFraction ) * fecundityPromotiveRes);
		fecundityPromotiveLevel -= fecundityPromotiveResFactor * fecundityPromotiveDecRate;
				// readjust	
		if (fecundityPromotiveLevel < 0) {
			fecundityPromotiveLevel = 0;
		}
		
		//part 2 - Fdl modified
		
				//increase
		double fecundityDemotiveCarryingFactor = 1 - Math.pow(fecundityDemotiveLevel/fecundityDemotiveMax, 2);
		fecundityDemotiveLevel += fecundityDemotiveCarryingFactor * labor * fecundityDemotiveIncRate;
				//decrease	
		double fecundityDemotiveUtilFraction;
		if (labor < (int)fecundityDemotiveLevel) {
			fecundityDemotiveUtilFraction = labor/fecundityDemotiveLevel;
		} else {
			fecundityDemotiveUtilFraction = 1;
		}
		double fecundityDemotiveResFactor = 1 - (fecundityDemotiveUtilFraction + (1 - fecundityDemotiveUtilFraction ) * fecundityDemotiveRes);
		fecundityDemotiveLevel -= fecundityDemotiveResFactor * fecundityDemotiveDecRate;
	   			// readjust 
		if (fecundityDemotiveLevel < 0) {
			fecundityDemotiveLevel = 0;
		}

		// part 3 - staples is drawn away from fecundityBase depending on whether fpl or fdl is closer to their max (fpm, fdm)

		double fecundityPromotiveFraction;
		double fecundityDemotiveFraction;
		
				// calculate both relative levels
		if (fecundityPromotiveMax == 0) {
			fecundityPromotiveFraction = 0;
		} else {
			fecundityPromotiveFraction = fecundityPromotiveLevel/fecundityPromotiveMax;
		}
		if (fecundityDemotiveMax == 0) {
			fecundityDemotiveFraction = 0;
		} else {
			fecundityDemotiveFraction = fecundityDemotiveLevel/fecundityDemotiveMax;
		}
					
				// update staples
		if (fecundityPromotiveFraction > fecundityDemotiveFraction) {
			fecundityPromotiveFraction = fecundityPromotiveFraction - fecundityDemotiveFraction;
			staples = (int) (fecundityBase + fecundityPromotiveFraction * fecundityPromotiveMax);
		} else {
			fecundityDemotiveFraction = fecundityDemotiveFraction - fecundityPromotiveFraction;
			staples = (int)(fecundityBase - fecundityDemotiveFraction * fecundityDemotiveMax);
		} 
		staplesShow = staples;
	}
    
	// culmination of the more elaborate Region.calculateTrafficLong() and Route.calculateWeight()
	public void calculateImports() {
		imports = importsCoefficient * distToExporter + importsYIntercept;
		if (imports < 0) {
			imports = 0;
		}
	}

	public void reproduce() {

		if (labor == 0 | staples == 0) {
			return;
		}

		// each existing group has and *odds* chance to generate a new group
		int newGroups = 0;
		double odds = getStaplesPerCap() / infertility;
		if (odds >= 1) {
			odds = .999;
		}
		try {
			Binomial dist = RandomHelper.createBinomial(labor, odds);
			newGroups = dist.nextInt();
		} catch (IllegalArgumentException e) {
			newGroups = 0;
		}
		for (int i = 0; i < newGroups; i++) {
			Group newGroup = new Group(this, false);
			newGroup.setDestinations(destinations);
			newGroup.setPullFractions(pullFractions);
			addGroup(newGroup);
			created ++;
		}
	}
	
	// Groups-related methods
    
	public void setPull(double pull) {
		this.pull = pull;
	}
    
	public double getPull() {
		return pull;
	}
    
	public void setPullFraction(double pullFraction) {
		this.pullFraction = pullFraction;
	}
    
	public double getPullFraction() {
		return pullFraction;
	}
    
   	public int getID() {
		return id;
	}

	public int getLabor() {
		return this.labor;
	}

//	public int getSpawnLabor() {
//		if (id ==8) {
//			return labor;
//		} else {
//			return 0;
//		}
//	}
	
	public int getResSize() {
		return residents.size();
	}
	
	public void settle() {
		settled ++;
		incEndemic();
	}
	
	public void emmigrate(Group group) {
		residents.remove(group);
		labor -= 1;
		if (group.getMigrant() == false) {
			decEndemic();
		}
	}
	
	public void immigrate(Group group) {
		residents.add(group);
		labor += 1;
		if (group.getMigrant() == false) {
			incEndemic();
		}
	}
	
	public void removeGroup(Group group) {
		residents.remove(group);
		labor -= 1;
		context.remove(group);
		if (group.getMigrant() == false) {
			decEndemic();
		}
	}

	public void addGroup(Group group) {
		residents.add(group);
		labor += 1;
		context.add(group);
		if (group.getMigrant() == false) {
			incEndemic();
		}
	}
	
	public List<Group> getResidents() {
		return residents;
	}
    
	// Staple-related methods

	public int getStaples(){
		return this.staples;
	}

	public double getStaplesPerCap(){
		return ((double)staples + 1)/((double)residents.size() + 1);
	}
	
	public void decreaseStaples() {
		staples--;
	}
	
//	public void modStaples(int flux) {
//		staples += flux;
//		if (staples < 0) {
//			staples = 0;
//		}
//	}

//	public void setStaples(int staples) {
//		this.staples = staples;
//	}
	
	public double getFecundityBase() {
		return fecundityBase;
	}

//	public void setFecundityBase(double fecundityBase) {
//		this.fecundityBase = fecundityBase;
//	}
	
	// imports-related methods

	public void decreaseImports() {
		imports --;
	}

	public double getImports() {
		return(imports);
	}

	public double getImportsPerCap() {
		return ((double)imports + 1)/((double)residents.size() + 1);
	}
	
//	public void setImports(double imports) {
//		this.imports = imports;
//	}
	
	public double getImportsPopulated() {
		if (labor > 0 ) {
			return(imports);
		} else {
			return 0;
		}
	}
	
//	public void modImports(double imports) {
//		this.imports += imports;
//	}
	
	public void setMineDistance(double distanceToExporter) {
		distToExporter = distanceToExporter;
	}

	public void setPath(List<Route<Object>> path) {
		this.path = path;
	}

	public List<Route<Object>> getPath() {
		return path;
	}
	
	public double getDistToExporter() {
		return distToExporter;
	}
	
	// terrain-related methods
	
	public void makeUpland() {
		upland = true;
	}
	
	public boolean getUpland() {
		return upland;
	}

	public void setWater(boolean water) {
		this.water = water;
		if (water) {
		    	fecundityBase = 0;
		    	fecundityPromotiveMax = 0;
		    	fecundityDemotiveMax = 0;
		    	staples = 0;
		}
	}
	
	// output-related methods
	
	public int getLabor60() {
		return this.labor * 60;
	}	
	public int getStayed() {
		return stayed;
	}

	@ScheduledMethod(start = 30, interval = 999999999)
	public void resetDeaths() {
		emigrations = 0;
		stapleRemovals = 0;
		importRemovals = 0;
	}

	public double getEmigrations() {
		return emigrations;
	}
	
	public int getStapleRemovals() {
		return stapleRemovals;
	}
	
	public int getImportRemovals() {
		return importRemovals;
	}
	
	public void incStapleRemovals() {
		stapleRemovals ++;
	}
	
	public void incImportRemovals() {
		importRemovals ++;
	}
	
	public void incEmigrations() {
		emigrations ++;
	}

	public int getCreated() {
		return created;
	}
	public int getSettled() {
		return settled;
	}
	
	public void incEndemic(){
		groupsEndemic ++;
	}

	public void decEndemic(){
		groupsEndemic --;
	}
	
	public int getEndemic(){
		return groupsEndemic;
	}
	public void setDestinations(Hashtable<Double, Center> destinations) {
		Object dests = destinations.clone();
		this.destinations = (Hashtable<Double, Center>) dests;
	}

	public void setPullFractions(ArrayList<Double> pullFractions) {
		Object pullfras = pullFractions.clone();
		this.pullFractions = (ArrayList<Double>) pullfras;
	}
	
//	public double getFPL() {
//		return fecundityPromotiveLevel;
//	}
//	
//	public double getFDL() {
//		return fecundityDemotiveLevel;
//	}
	
	public double getFPLshow() {
		return fecundityBase + fecundityPromotiveLevel;
	}
	
	public double getFDLshow() {
		return fecundityBase -fecundityDemotiveLevel;
	}
	
	public int getStaplesShow() {
		return staplesShow;
	}
	
	public boolean getGateway() {
		return gateway;
	}
	
	public void makeGateway() {
		gateway = true;
	}
	
}
