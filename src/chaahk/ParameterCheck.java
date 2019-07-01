package chaahk;

import javax.swing.JOptionPane;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

// detects invalid parameter entries and displays each error in a pop up window
public class ParameterCheck{

	public static boolean check(Parameters params, Network<Object> net) {
		
		boolean invalidParams = false;
		String paramIssues = "Invalid Parameters. Fix parameter values and try again.";

		int disturbanceDelay = (Integer)params.getValue("disturbanceDelay");
		if (disturbanceDelay < -1000 | disturbanceDelay > 550) {
			paramIssues += " \n - Disturbance Delay must be between -1000 and 550.";
			invalidParams = true;
		}
		double disturbanceRemovalChance = (Double)params.getValue("disturbanceRemovalChance");
		if (disturbanceRemovalChance < 0 | disturbanceRemovalChance > 1) {
			paramIssues += " \n - Disturbance Removal Chance must be between 0 and 1.";
			invalidParams = true;
		}
		double fertility = (Double)params.getValue("fertility");
		if (fertility < 0 | fertility > 5) {
			paramIssues += " \n - Ferility must be between 0 and 5.";
			invalidParams = true;
		}
		double fecundityPromotiveMax = (Double)params.getValue("fecundityPromotiveMax");
		if (fecundityPromotiveMax < 0) {
			paramIssues += " \n - Fecundity Promotive Maximum cannot be below 0.";
			invalidParams = true;
		}
		double fecundityPromotiveRes     = (Double)params.getValue("fecundityPromotiveRes");
		if (fecundityPromotiveRes < 0 | fecundityPromotiveRes > 1) {
			paramIssues += " \n - Fecundity Promotive Resilience must be between 0 and 1.";
			invalidParams = true;
		}
		double fecundityPromotiveIncRate = (Double)params.getValue("fecundityPromotiveIncRate");
		if (fecundityPromotiveIncRate < 0) {
			paramIssues += " \n - Fecundity Promotive Increase Rate cannot be below 0.";
			invalidParams = true;
		}
		double fecundityPromotiveDecRate = (Double)params.getValue("fecundityPromotiveDecRate");
		if (fecundityPromotiveDecRate < 0) {
			paramIssues += " \n - Fecundity Promotive Decrease Rate cannot be below 0.";
			invalidParams = true;
		}
		double fecundityDemotiveMax      = (Double)params.getValue("fecundityDemotiveMax");
		if (fecundityDemotiveMax < 0) {
			paramIssues += " \n - Fecundity Demotive Maximum cannot be below 0.";
			invalidParams = true;
		}
		double fecundityDemotiveRes      = (Double)params.getValue("fecundityDemotiveRes");
		if (fecundityDemotiveRes < 0 | fecundityDemotiveRes > 1) {
			paramIssues += " \n - Fecundity Demotive Resilience must be between 0 and 1.";
			invalidParams = true;
		}
		double fecundityDemotiveIncRate  = (Double)params.getValue("fecundityDemotiveIncRate");
		if (fecundityDemotiveIncRate < 0) {
			paramIssues += " \n - Fecundity Demotive Increase Rate cannot be below 0.";
			invalidParams = true;
		}
		double fecundityDemotiveDecRate  = (Double)params.getValue("fecundityDemotiveDecRate");
		if (fecundityDemotiveDecRate < 0) {
			paramIssues += " \n - Fecundity Demotive Decrease Rate cannot be below 0.";
			invalidParams = true;
		}
		double costPromotiveRes = (Double)params.getValue("costPromotiveRes");
		if (costPromotiveRes < 0 | costPromotiveRes > 1) {
			paramIssues += " \n - Cost Promotive Resilience must be between 0 and 1.";
			invalidParams = true;
		}
		double costPromotiveMax = (Double)params.getValue("costPromotiveMax");
		if (costPromotiveMax < 0) {
			paramIssues += " \n - Cost Promotive Maximum cannot be below 0.";
			invalidParams = true;
		}
		double costPromotiveIncRate = (Double)params.getValue("costPromotiveIncRate");
		if (costPromotiveIncRate < 0) {
			paramIssues += " \n - Cost Promotive Increase Rate cannot be below 0.";
			invalidParams = true;
		}
		double costPromotiveDecRate = (Double)params.getValue("costPromotiveDecRate");
		if (costPromotiveDecRate < 0) {
			paramIssues += " \n - Cost Promotive Decrease Rate cannot be below 0.";
			invalidParams = true;
		}
		double trafficPromotiveShortCoefficient  = (Double)params.getValue("trafficPromotiveShortCoefficient");                                                        
		if (trafficPromotiveShortCoefficient < 0) {
			paramIssues += " \n - Traffic Promotive Short Coefficient cannot be below 0.";
			invalidParams = true;
		}
		double trafficPromotiveLongCoefficient   = (Double)params.getValue("trafficPromotiveLongCoefficient");
		if (trafficPromotiveLongCoefficient < 0) {
			paramIssues += " \n - Traffic Promotive Long Coefficient cannot be below 0.";
			invalidParams = true;
		}
		double costDemotiveRes      = (Double)params.getValue("costDemotiveRes");
		if (costDemotiveRes < 0 | costDemotiveRes > 1) {
			paramIssues += " \n - Cost Demotive Resilience must be between 0 and 1.";
			invalidParams = true;
		}
		double costDemotiveMax      = (Double)params.getValue("costDemotiveMax");
		if (costDemotiveMax < 0) {
			paramIssues += " \n - Cost Demotive Maximum cannot be below 0.";
			invalidParams = true;
		}
		double costDemotiveIncRate  = (Double)params.getValue("costDemotiveIncRate");
		if (costDemotiveIncRate < 0) {
			paramIssues += " \n - Cost Demotive Increase Rate cannot be below 0.";
			invalidParams = true;
		}
		double costDemotiveDecRate  = (Double)params.getValue("costDemotiveDecRate");
		if (costDemotiveDecRate < 0) {
			paramIssues += " \n - Cost Demotive Decrease Rate cannot be below 0.";
			invalidParams = true;
		}
		double trafficDemotiveShortCoefficient  = (Double)params.getValue("trafficDemotiveShortCoefficient");
		if (trafficDemotiveShortCoefficient < 0) {
			paramIssues += " \n - Traffic Demotive Short Coefficient cannot be below 0.";
			invalidParams = true;
		}
		double trafficDemotiveLongCoefficient   = (Double)params.getValue("trafficDemotiveLongCoefficient");
		if (trafficDemotiveLongCoefficient < 0) {
			paramIssues += " \n - Traffic Demotive Long Coefficient cannot be below 0.";
			invalidParams = true;
		}
		double importsCoefficient = (Double)params.getValue("importsCoefficient");
		if (importsCoefficient > 0) {
			paramIssues += " \n - Imports Coefficient must be negative.";
			invalidParams = true;
		}
		double importsYIntercept = (Double)params.getValue("importsYIntercept");
		if (importsYIntercept < 0) {
			paramIssues += " \n - Imports Y Intercept cannot be below 0.";
			invalidParams = true;
		}
		
		int observedRouteSourceID = (Integer)params.getValue("observedRouteSourceID");
		int observedRouteTargetID = (Integer)params.getValue("observedRouteTargetID");
		boolean difIDs = true;
		if (observedRouteSourceID == observedRouteTargetID){
			paramIssues += " \n - Observed Route target and source ID's cannot be the same.";
			invalidParams = true;
			difIDs = false;
		}
		boolean invalidObsRoute = true;
		for (RepastEdge<Object> vagueRoute : net.getEdges()) {
			Route<Object> route = (Route<Object>) vagueRoute;
			boolean obReverse = (route.getSourceCenter().getID()==observedRouteTargetID & route.getTargetCenter().getID()==observedRouteSourceID);
			boolean obSame = (route.getSourceCenter().getID()==observedRouteSourceID & route.getTargetCenter().getID()==observedRouteTargetID);
			if ( obSame | obReverse ) {
				invalidObsRoute = false;
			} 
		}
		if (invalidObsRoute & difIDs) {
			paramIssues +=  " \n - Specified Route does not exist.";
			invalidParams = true;
		}

		// returning true will stop the simulation from initializing.
		if (invalidParams) {
			JOptionPane.showMessageDialog(null, paramIssues);
			return true;
		} else {
			return false;
		}
	}
}
