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
	private int labor; 
	private int groupsEndemic;

	private double pull;					//migration
	private double pullFraction;
	private Hashtable<Double, Center> destinations;
	private ArrayList<Double> pullFractions;
	
	private int staples;					//staples
	private double fecundityEmployable;
	private double fecundityEngineered;
	private LinkedList<Double> fecundityDamageQueue; 
	private double fecundityIncrease;
	private double fecundityDecrease;
	
	private double imports;					//imports
	private double distToExporter;
	
	private List<Route<Object>> path;		//excluded
	
	
//	STATIC ENTITY VARIABLES	
	private int id;							//excluded
	private Context<Object> context;
	
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
	private double fecundityDisturbance; //Fd
	
	
	
	
//	UNKNOWN STATUS?
	private double deathFraction;
	private double push;
	private int droughtMod;
	
//	METRICS
	private int moveDeaths;
	private int moveLifes;
	private int stayed;
	private int staplesDeaths;
	private int importsDeaths;
	private int born;
	private int settled;
	
	
	
	public Center(int labor, int id, Context<Object> context) {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
//		DYNAMIC ENTITY VARIABLES
		residents = new ArrayList<Group>();	//people
		this.labor = labor;
		groupsEndemic = 0;
		
		staples = 3;						//staples
		fecundityEngineered = 3;
		fecundityEmployable = 3;
		fecundityDamageQueue = new LinkedList<Double>();
		fecundityIncrease = 0;
		fecundityDecrease = 0;

		
		imports = 0.0;						//imports
		distToExporter = 0;
		pull = 0;
		pullFraction = 0;
		
		destinations = null;
		pullFractions = null;
		
//		STATIC ENTITY VARIABLES		
		
		this.id = id;						//excluded
		this.context = context;
		
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
		fecundityDisturbance = (Double)params.getValue("fecundityDisturbance");
		
		for (int i = 0; i < fecundityReck; i++) {
			fecundityDamageQueue.add(fecundityBase);
		}
		
//		UNKNOWN STATUS?
//		deathFraction = (Double)params.getValue("deathFraction");
//		push = (Double)params.getValue("push");
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
//    	reproduce();
    	
    	/*the values of Faq are equal to Fg - Fb, which is calculated later in the method. 
    	 * Frk determines the length of Faq, and therefore the delay between the calculation 
    	 * of Fa and its utilization by the following statement. */
    	
		double fecundityDamage = fecundityDamageQueue.removeFirst(); //Fa
		
    	double fecundityCarryingFactor = 1 - Math.pow(fecundityDamage/fecundityMax, 2);
    	fecundityIncrease = fecundityCarryingFactor * labor * fecundityIncRate;
    	
    	double fecundityOvershoot = fecundityEmployable - fecundityMax;
    	if (fecundityOvershoot > 0) {
    		int fecundityDamageDiff = (int)fecundityOvershoot;
    		for (int i = 0; i < fecundityDamageDiff; i++) {
    			fecundityDamageQueue.removeFirst();
    			fecundityDamageQueue.addLast(fecundityEmployable);
    		}
    	}

    	
    //	The purpose of the Fuf 
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
    	

//    	fecundityTotalGrowth = fecundityEngineered - fecundityBase;     
    	fecundityEmployable = newfecundity;
    	fecundityDamageQueue.addLast(fecundityEngineered);
    	if (fecundityEmployable < fecundityMin) {
    		fecundityEmployable = fecundityMin;
    	}
    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
    	if (tick > 10000+droughtMod & tick < 11000+droughtMod) {
    		staples = Math.round((float)(fecundityEmployable*fecundityDisturbance));
    	} else {
    		staples = Math.round((float)fecundityEmployable);
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
    

    
//    @ScheduledMethod(start = 10000)
//    public void Death() {
//
//    	List<Group> dead = new ArrayList<Group>();
//    	for (Group maya : residents) {
//    		double fate = RandomHelper.nextDoubleFromTo(0,1);
//    		if (fate < deathFraction) {
//    			dead.add(maya);
//    		}
//    	}
//    	for (Group m : dead) {
//    		killGroup(m);
//    	}
//
//    }
    
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
//		if (labor == 0 & flux < 0) {
//			System.out.println(RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
//		}
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

	public double getStaplesStuck() {
		int window = fecundityDamageQueue.size();
		if (window > 7) {
			window = 7;
		} 
		double greatestDamage = 0;
		for (int i = 0; i < window; i++) {
			double fecundityDamage = fecundityDamageQueue.get(i);
			if(fecundityDamage > greatestDamage) {
				greatestDamage = fecundityDamage;
			}
		}
		double staplesStuck = greatestDamage - fecundityBase;
		if (staplesStuck < 0) {
			return(0);
		} else {
			return(staplesStuck);
		}
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
	
	public void modImports(double imports) {
		this.imports += imports;
	}
	
	public void calculateImports() {
		this.imports = (labor * 2 + 1) * Math.pow(distToExporter, -.3) + .78;
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
		return moveLifes/7;
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

	

//  private void build(double tick) {
//	double buildScore = RandomHelper.nextDoubleFromTo(0,Math.log(labor*staplesPerCap));
//	Parameters params = RunEnvironment.getInstance().getParameters();
//	double stelaeculty = (Double)params.getValue("stelaeculty");
//	if (buildScore > stelaeculty) {
//		termD = (int)(tick/45.0 + 600);
//		stela++;
//	}
//	
//}

//@ScheduledMethod(start = 5000, interval = 999999)
//public void warfare (){
//	Parameters params = RunEnvironment.getInstance().getParameters();
//	double warfare = (Double)params.getValue("killGroup");
//	List<Group> dead = new ArrayList<Group>();
//	
//	for (Group maya : residents) {
//		double deathChance = RandomHelper.nextDoubleFromTo(0, 1);
//		if (deathChance > warfare) {
//			dead.add(maya);
//		}
//	}
//	for (Group m : dead) {
//		killGroup(m);
//	}
//}

	
//	public double getMarket() {
//		return this.market;
//	}
//	
//	public void setMarket(double market) {
//		this.market = market;
//	}
//	

//	double fecundityImprovement = labor * fecundityIncreaseRate;
//	if (terraIncognita == false) {
//		System.out.println();
//		System.out.println("ID              is " +  id       );
//		System.out.println("labor           is " +  labor       );
//		System.out.println("fecundityEmployable     is " +  fecundityEmployable    );
//		System.out.println("fecundityEngineered         is " +  fecundityEngineered        );
//		System.out.println("fecundityTotalGrowth is " +  fecundityTotalGrowth);
//		
//		
//
//		double fecundityUtilFraction;
//		double laborViafecundityImprovement = labor - fecundityBase;
//		if (laborViafecundityImprovement < 0) {
//			laborViafecundityImprovement = 0;
//		}
//		System.out.print("fecundityTotalGrowth is ");
//		System.out.println(fecundityTotalGrowth);
//		System.out.print("laborViafecundityImprovement is ");
//		System.out.println(laborViafecundityImprovement);
//		if (laborViafecundityImprovement < (int)fecundityTotalGrowth) {
//			fecundityUtilFraction = laborViafecundityImprovement/fecundityTotalGrowth;
//		} else {
//			fecundityUtilFraction = 1;
//		}
//		System.out.print("fecundityUtilFraction is ");
//		System.out.println(fecundityUtilFraction);
//    	double fecundityResFactor = 1 - (fecundityUtilFraction + (1 - fecundityUtilFraction ) * fecundityResil);
//		System.out.print("fecundityResFactor is ");
//		System.out.println(fecundityResFactor);
//    	double fecundityDecay = fecundityResFactor * fecundityDecreaseRate;
//    	System.out.print("fecundityDecay is ");
//    	System.out.println(fecundityDecay);
//    	System.out.print("fecundityImprovement is ");
//    	System.out.println(fecundityImprovement);
//    	fecundityImprovement -= fecundityDecay;
//    	System.out.print("decayed fecundityImprovement is ");
//    	System.out.println(fecundityImprovement);
//    	
//    	System.out.print("currentfecundity is ");
//    	System.out.println(fecundityEmployable);
//    	
//    	double newfecundity = fecundityEmployable + fecundityImprovement;
//    	System.out.print("basic newfecundity is ");
//    	System.out.println(newfecundity);
//    	
//    	if (newfecundity > fecundityMax) {
//    		System.out.println("**hit maximum fecundity limit**");
//    		newfecundity = fecundityMax;
//    		System.out.print("newfecundity is ");
//        	System.out.println(newfecundity);
//    	}
//    	if (newfecundity > fecundityEngineered) {
//    		System.out.println("**new fecundityEngineered**");
//    		fecundityEngineered = newfecundity;
//    		System.out.println("fecundityEngineered         is " +  fecundityEngineered        );
//    	} else {
//    		System.out.println("**attempting regeneration**");
//    		System.out.println("fecundityEngineered         is " +  fecundityEngineered        );
//    		if (fecundityEngineered > fecundityBase) {
//    			System.out.println("**fecundityEngineered > fecundityBase**");
//    			fecundityEngineered = fecundityEngineered - fecundityRegen;
//    			System.out.println("new fecundityEngineered         is " +  fecundityEngineered        );
//    		} else {
//    			newfecundity += fecundityRegen;
//    			fecundityEngineered = fecundityBase;
//    		}
//    	} 
//    	System.out.println("**calculating fecundityTotalGrowth**");
//    	fecundityTotalGrowth = fecundityEngineered - fecundityBase;     
//    	System.out.println("fecundityTotalGrowth is " +  fecundityTotalGrowth);
//    	System.out.println("**recalculating fecundityEmployable");
//    	fecundityEmployable = newfecundity;
//    	System.out.println("fecundityEmployable     is " +  fecundityEmployable    );
//    	System.out.println("**checking not minimum**");
//    	if (fecundityEmployable < fecundityMin) {
//    		fecundityEmployable = fecundityMin;
//    	}
//    	System.out.println("new fecundityEmployable is ");
//    	System.out.println(fecundityEmployable);
//    	
//    	System.out.println("**filling with staples**");
//    	double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
//    	if (tick > 10000 & tick < 11000) {
//    		staples = Math.round((float)(fecundityEmployable*fecundityDisturbance));
//    	} else {
//    		staples = Math.round((float)fecundityEmployable);
//    	}
//    	System.out.print("staples is ");
//    	System.out.println(staples);	
//		
//		
//		
//		
//	} else {
//		staples = (int)fecundityBase;
//	}
//}

}
