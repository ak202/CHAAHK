package mayagrations;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import cern.jet.random.Binomial;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import java.lang.Math;

public class Center {
	
//	DYNAMIC ENTITY VARIABLES
	private List<Group> residents; 			//people
	private int labor; //L
	private int groupsEndemic; //B
	private double pull;				
	private double pullFraction;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	
	private int staples;					//staples
	private double fecundityEmployable; //Fp
	private double fecundityEngineered; //Fg
	private LinkedList<Double> fecundityDamageQueue; //Faq 
	private double fecundityIncrease; //Fi
	private double fecundityDecrease; //Fd
	
	private double imports; //I				//imports
	private double importsLast;
	private double distToExporter; //D
	
	private List<Route<Object>> path;		//excluded
	
//	STATIC ENTITY VARIABLES	
	private int id;							//excluded
	private Context<Object> context;
	private boolean upland;
	private boolean water;
	
//	STATIC GLOBAL VARIABLES
	private double infertility;
	private double fecundityBase; //Fb

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
	
	private double disturbance;
	private int droughtMod;

	
	
	
//
//	private double fecundityReck; //Frk
//	private double fecundityReckFraction;
//

	
//	METRICS
	private int moveDeaths;
	private int moveLifes;
	private int stayed;
	private int staplesDeaths;
	private int importsDeaths;
	private int born;
	private int settled;
	
	public Center(int id, Context<Object> context) {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
//		DYNAMIC ENTITY VARIABLES
		residents = new ArrayList<Group>();	//people
		labor = 0;
		groupsEndemic = 0;
		destinations = null;
		pullFractions = null;
		
		staples = 3;						//staples
		
		imports = 0.0;						//imports
		importsLast = 0.0;
		distToExporter = 0;
		pull = 0;
		pullFraction = 0;
		
//		STATIC ENTITY VARIABLES		
		
		this.id = id;						//excluded
		this.context = context;
		upland = false;
		water = true;
		
//		STATIC GLOBAL VARIABLES
		infertility = (Integer)params.getValue("infert");
		fecundityBase = 3;
		
		fecundityPromotiveLevel   = 0;
		fecundityPromotiveRes     = (Double)params.getValue("fecundityPromotiveRes");
		fecundityPromotiveIncRate = (Double)params.getValue("fecundityPromotiveIncRate");
		fecundityPromotiveDecRate = (Double)params.getValue("fecundityPromotiveDecRate");
		fecundityPromotiveMax     = (Double)params.getValue("fecundityPromotiveMax");
		
		fecundityDemotiveLevel    = 0;
		fecundityDemotiveRes      = (Double)params.getValue("fecundityDemotiveRes");
		fecundityDemotiveIncRate  = (Double)params.getValue("fecundityDemotiveIncRate");
		fecundityDemotiveDecRate  = (Double)params.getValue("fecundityDemotiveDecRate");
		fecundityDemotiveMax      = (Double)params.getValue("fecundityDemotiveMax");
		
		disturbance = (Double)params.getValue("disturbance");
		
//		for (int i = 0; i < (int)Math.ceil(fecundityReck); i++) {
//			fecundityDamageQueue.add(fecundityBase);
//	

		droughtMod = (Integer)params.getValue("disturbanceDelay");
		
//		METRICS
		moveLifes = 0;
		moveDeaths = 0;
		staplesDeaths = 0;
		importsDeaths = 0;
		born = 0;
		settled = 0;		
		
	}
	
    public void calculateStaples() {
    	    	
    	if (!water) {
 
    		double fecundityPromotiveCarryingFactor = 1 - Math.pow(fecundityPromotiveLevel/fecundityPromotiveMax, 2);
	    	fecundityPromotiveLevel += fecundityPromotiveCarryingFactor * labor * fecundityPromotiveIncRate;
			
	    	double fecundityPromotiveUtilFraction;
			if (labor < (int)fecundityPromotiveLevel) {
				fecundityPromotiveUtilFraction = labor/fecundityPromotiveLevel;
			} else {
				fecundityPromotiveUtilFraction = 1;
			}
	    	double fecundityPromotiveResFactor = 1 - (fecundityPromotiveUtilFraction + (1 - fecundityPromotiveUtilFraction ) * fecundityPromotiveRes);

	    	fecundityPromotiveLevel -= fecundityPromotiveResFactor * fecundityPromotiveDecRate;
	    	
	    	if (fecundityPromotiveLevel < 0) {
	    		fecundityPromotiveLevel = 0;
	    	}
	    	
    		double fecundityDemotiveCarryingFactor = 1 - Math.pow(fecundityDemotiveLevel/fecundityDemotiveMax, 2);
	    	fecundityDemotiveLevel += fecundityDemotiveCarryingFactor * labor * fecundityDemotiveIncRate;
    		
			double fecundityDemotiveUtilFraction;
			if (labor < (int)fecundityDemotiveLevel) {
				fecundityDemotiveUtilFraction = labor/fecundityDemotiveLevel;
			} else {
				fecundityDemotiveUtilFraction = 1;
			}
	    	double fecundityDemotiveResFactor = 1 - (fecundityDemotiveUtilFraction + (1 - fecundityDemotiveUtilFraction ) * fecundityDemotiveRes);
	    	fecundityDemotiveLevel -= fecundityDemotiveResFactor * fecundityDemotiveDecRate;
	    	if (fecundityDemotiveLevel < 0) {
	    		fecundityDemotiveLevel = 0;
	    	}
    		
    		staples = (int)(fecundityBase+fecundityPromotiveLevel-fecundityDemotiveLevel);
        	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	    	if (tick > 1000+droughtMod & tick < 1100+droughtMod) {
	    		staples = Math.round((float)(staples*disturbance));
	    	} 

    	}
    }

    public void reproduce() {

		if (labor > 0 & staples > 0) {

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
		    	Group newGroup = new Group(this, false, "Center");
		    	newGroup.setDestinations(destinations);
		    	newGroup.setPullFractions(pullFractions);
		    	addGroup(newGroup);
		    	born ++;
		    }
		}
    }
    

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
	public int getSpawnLabor() {
		if (id ==8) {
			return labor;
		} else {
			return 0;
		}
	}
	
	public int getResSize() {
		return residents.size();
	}
	
	public int getPop() {
		return labor;
	}
	public void settle() {
		settled ++;
		incEndemic();
	}
	
	public void emmigrate(Group maya) {
		residents.remove(maya);
		labor -= 1;
		if (maya.getMigrant() == false) {
			decEndemic();
		}
	}
	
	public void immigrate(Group maya) {
		residents.add(maya);
		labor += 1;
		if (maya.getMigrant() == false) {
			incEndemic();
		}
	}
	
	public void killGroup(Group maya) {
		residents.remove(maya);
		labor -= 1;
		context.remove(maya);
		if (maya.getMigrant() == false) {
			decEndemic();
		}
	}

	public void addGroup(Group maya) {
		residents.add(maya);
		labor += 1;
		context.add(maya);
		if (maya.getMigrant() == false) {
			incEndemic();
		}
	}
	
	public List<Group> getGroup() {
		return residents;
	}
	public List<Group> getResidents() {
		return residents;
	}
    
	public int getStaples(){
		return this.staples;
	}
	
	public void modStaples(int flux) {
		staples += flux;
		if (staples < 0) {
			staples = 0;
		}
	}

	public double getStaplesPerCap(){
		return ((double)staples + 1)/((double)residents.size() + 1);
	}
	public void setStaples2(int staples) {
		this.staples = staples;
	}
	
	public double getFecundityBase() {
		return fecundityBase;
	}

	public void setFecundityBase(double fecundityBase) {
		this.fecundityBase = fecundityBase;
	}

	public double getFecundityEmployable() {
		return fecundityEmployable;
	}

	public void setFecundityEmployable(double fecundityEmployable) {
		this.fecundityEmployable = fecundityEmployable;
	}
	
	public double getFecundityEngineered() {
		return fecundityEngineered;
	}

	
	public double getFecundityIncrease() {
		return fecundityIncrease;
	}

	public double getFecundityDecrease() {
		return fecundityDecrease;
	}

	public double getImportsPerCap() {
		return ((double)imports + 1)/((double)residents.size() + 1);
	}
	
	public void setImports(double imports) {
		this.imports = imports;
	}
	
	public double getImports() {
		return(imports);
	}
	
	public double getImportsPopulated() {
		if (labor > 0 ) {
			return(imports);
		} else {
			return 0;
		}
	}
	
	public void modImports(double imports) {
		this.imports += imports;
	}
	
	public void calculateImports() {
		imports = importsLast;
//		System.out.println();
//		print("labor", labor);
//		print("dist", distToExporter);
		importsLast = (labor * 2 + 1) * Math.pow(distToExporter, -.25) + .7;
//		print("importds",imports);
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
	public void setMineDistance(double distanceToExporter) {
		distToExporter = distanceToExporter;
	}

	public void setPath(List<Route<Object>> path) {
		this.path = path;
	}
	public List<Route<Object>> getPath() {
		return path;
	}
	public void consume() {
		imports --;
	}
	
	public double getDistToExporter() {
		return distToExporter;
	}
	
	public void makeUpland() {
		upland = true;
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
    
	
	public boolean getUpland() {
		return upland;
	}
	
	public int getLabor60() {
		return this.labor * 60;
	}	
	public int getStayed() {
		return stayed;
	}
	@ScheduledMethod(start = 30, interval = 999999999)
	public void resetDeaths() {
		moveLifes = 0;
		moveDeaths = 0;
		staplesDeaths = 0;
		importsDeaths = 0;
	}
	public double getMoveLifes() {
		return moveLifes;
	}
	public int getMoveDeaths() {
		return moveDeaths;
	}
	
	public int getStaplesDeaths() {
		return staplesDeaths;
	}
	
	public int getImportsDeaths() {
		return importsDeaths;
	}
	
	public void incStaplesDeaths() {
		staplesDeaths++;
	}
	
	public void incImportsDeaths() {
		importsDeaths++;
	}
	
	public void incMoveLifes() {
		moveLifes ++;
	}
	
	public void incMoveDeaths() {
		moveDeaths++;
	}

	public int getBorn() {
		return born;
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
	
	public double getFPL() {
		return fecundityPromotiveLevel;
	}
	
	public double getFDL() {
		return fecundityDemotiveLevel;
	}
	
	public double getFPLshow() {
		return fecundityBase + fecundityPromotiveLevel;
	}
	
	public double getFDLshow() {
		return fecundityBase -fecundityDemotiveLevel;
	}
	
}
