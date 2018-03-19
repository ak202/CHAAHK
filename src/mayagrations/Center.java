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
	private boolean terrain;
	
//	STATIC GLOBAL VARIABLES
	private double infertility;
	private double fecundityBase; //Fb
	private double fecundityMax; //Fmx
	private double fecundityMin; //Fmn
	private double fecundityIncRate; //Fir
	private double fecundityDecRate; //Fdr
	private double fecundityRegen; //Frg
	private double fecundityResil; //Frs
	private int fecundityReck; //Frk
	private int fecundityReckFraction;
	private double fecundityDisturbance;
	private double disturbance; //Fd
	private int droughtMod;
	
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
		this.labor = labor;
		groupsEndemic = 0;
		destinations = null;
		pullFractions = null;
		
		staples = 3;						//staples
		fecundityEngineered = 3;
		fecundityEmployable = 3;
		fecundityDamageQueue = new LinkedList<Double>();
		fecundityIncrease = 0;
		fecundityDecrease = 0;
		
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
		terrain = false;
		
//		STATIC GLOBAL VARIABLES
		infertility = (Integer)params.getValue("infert");
		fecundityBase = 3;
		fecundityMax = (Double)params.getValue("fecundityMax");
		fecundityMin = (Double)params.getValue("fecundityMin");
		fecundityIncRate = (Double)params.getValue("fecundityIncRate");
		fecundityDecRate = (Double)params.getValue("fecundityDecRate");
		fecundityRegen = (Double)params.getValue("fecundityRegen");
		fecundityResil = (Double)params.getValue("fecundityResil");
		fecundityReck = (Integer)params.getValue("fecundityReck");
		fecundityReckFraction = fecundityReck%1;
		fecundityDisturbance = (Double)params.getValue("fecundityDisturbance");
		disturbance = (Double)params.getValue("disturbance");
		
		for (int i = 0; i < (int)Math.ceil(fecundityReck); i++) {
			fecundityDamageQueue.add(fecundityBase);
		}

		droughtMod = (Integer)params.getValue("disturbanceDelay");
		
//		METRICS
		moveLifes = 0;
		moveDeaths = 0;
		staplesDeaths = 0;
		importsDeaths = 0;
		born = 0;
		settled = 0;		
		
		if ((id-16)%17==0 | (id)%17==0) {
			setWater(true);
			terrain = true;
		}
		for (int i=0;i<17;i++){
			if(i == id) {
				setWater(true);
				terrain = true;
			}	
		}
	}
	
    public void calculateStaples() {

    	/*the values of Faq are equal to Fg - Fb, which is calculated later in the method. 
    	 * Frk determines the length of Faq, and therefore the delay between the calculation 
    	 * of Fa and its utilization by the following statement. */
    	
    	if (!water) {
			double fecundityDamage = fecundityDamageQueue.removeFirst(); //Fa
			
	    	double fecundityCarryingFactor = 1 - Math.pow(fecundityDamage/fecundityMax, 2);
	    	fecundityIncrease = fecundityCarryingFactor * labor * fecundityIncRate;
	    	
	    	double fecundityOvershoot = fecundityEmployable - fecundityMax;
	    	if (fecundityOvershoot > 0) {
	    		int fecundityDamageDiff = (int)fecundityOvershoot;
	    		for (int i = 0; i < fecundityDamageDiff; i++) {
	    			fecundityDamageQueue.addLast(fecundityEmployable);
	    			fecundityDamageQueue.removeFirst();
	    		}
	    	}
	
			double fecundityUtilFraction;
			double laborViaFecundityGrowth = labor - fecundityBase;
			if (laborViaFecundityGrowth < 0) {
				laborViaFecundityGrowth = 0;
			}
			double fecundityEngineeredGrowth = fecundityEngineered - fecundityBase;
			if (laborViaFecundityGrowth < (int)fecundityEngineeredGrowth) {
				fecundityUtilFraction = laborViaFecundityGrowth/fecundityEngineeredGrowth;
			} else {
				fecundityUtilFraction = 1;
			}
	    	double fecundityResFactor = 1 - (fecundityUtilFraction + (1 - fecundityUtilFraction ) * fecundityResil);
	    	fecundityDecrease = fecundityResFactor * fecundityDecRate;
	
	    	double newfecundity = fecundityEmployable + fecundityIncrease - fecundityDecrease;
	
	    	if (newfecundity > fecundityEngineered) {
	    		fecundityEngineered = newfecundity; 
	    	} else {
	    		if (fecundityEngineered > fecundityBase) {
	    			fecundityEngineered -= fecundityRegen;
	    		} else {
	    			newfecundity += fecundityRegen;
	    			fecundityEngineered = fecundityBase;
	    		}
	    	}
	    	    
	    	fecundityEmployable = newfecundity;
	    	
	    	
	    	if (fecundityReckFraction == 0) {
	    		fecundityDamageQueue.addLast(fecundityEngineered);
	    	} else {
	    		fecundityDamageQueue.addLast(
	    				(1-fecundityReckFraction)*fecundityDamageQueue.peekLast() +
	    				fecundityReckFraction*fecundityEngineered);
	    	}
	    	
	    	if (fecundityEmployable < fecundityMin) {
	    		fecundityEmployable = fecundityMin;
	    	}
	    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	    	if (tick > 1000+droughtMod & tick < 1100+droughtMod) {
	    		staples = Math.round((float)(fecundityEmployable/(disturbance*fecundityDisturbance)));
	    	} else {
	    		staples = Math.round((float)fecundityEmployable);
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
    
    public void setWater(boolean water) {
    	if (!terrain) {
        	this.water = water;
        	if (water) {
            	fecundityBase = 0;
            	fecundityMax = 0;
            	fecundityMin = 0;
            	staples = 0;
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

	public void setLabor(int flux) {
		this.labor += flux;
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
		System.out.println(distToExporter);
		imports = importsLast;
		importsLast = (labor * 2 + 1) * Math.pow(distToExporter, -.2) + .51;
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
		if (!terrain) {
			upland = true;
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
	
}
