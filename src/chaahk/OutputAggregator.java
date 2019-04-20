package chaahk;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.IllegalParameterException;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.essentials.RepastEssentials;

public class OutputAggregator {

	private List<Route> observedRoutes;
	private List<Center> observedCenters;
	private int observedRouteCount;
	private int observedCenterCount;
	private double meanCb;


	public OutputAggregator(List<Center> centers, Network<Object> net) {

		Parameters params = RunEnvironment.getInstance().getParameters();

		observedRoutes = new ArrayList();
		System.out.println("test");
		String observedRouteType = (String)params.getValue("observedRouteType");
		int observedRouteSourceID = 0;
		int observedRouteTargetID = 0;
		if (observedRouteType.equals("ID")) {
			observedRouteSourceID = (Integer)params.getValue("observedRouteSourceID");
			observedRouteTargetID = (Integer)params.getValue("observedRouteTargetID");		
		}
		for (RepastEdge<Object> vagueRoute : net.getEdges()) {
			Route<Object> route = (Route<Object>) vagueRoute;
			if (observedRouteType.equals("All")) {
				observedRoutes.add(route);
			} else if (route.getType().equals(observedRouteType)) {
				observedRoutes.add(route);
			} else if (observedRouteType.equals("ID")) {
				boolean obSame = (route.getSourceCenter().getID()==observedRouteSourceID & 
						route.getTargetCenter().getID()==observedRouteTargetID);
				boolean obReverse = (route.getSourceCenter().getID()==observedRouteTargetID &
						route.getTargetCenter().getID()==observedRouteSourceID);
				if ( obSame | obReverse ) {
					observedRoutes.add(route);	
				}
			}
		}
		observedRouteCount = observedRoutes.size();
	
		double sumCb = 0;
		for (Route route : observedRoutes) {
			sumCb += route.getCostBase();
		}
		meanCb = sumCb/observedRouteCount;

		observedCenters = new ArrayList();
		boolean observeSpecificCenter = (Boolean)params.getValue("observeSpecificCenter");
		if (observeSpecificCenter) {
			observedCenters = new ArrayList();
			int observedCenterID = (Integer)params.getValue("observeWhatCenter");
			for (Center center : centers) {
				int id = center.getID();
				if (id == observedCenterID) {
					observedCenters.add(center);
				}
			}
		} else {
			observedCenters = centers;
		}
		observedCenterCount = observedCenters.size();

	}

	//----------  General Cost   ------------------
	public double getWeight() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getWeight();
		}
		return sum / observedRouteCount;
	}
	public double getCostBase() {
		return meanCb;
	}
	//---------   Cost Promotive:    ------------
	public double getCostPromotiveLevel() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostPromotiveLevel();
		}
		return sum / observedRouteCount;
	}
	public double getCbMinusCpl() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostPromotiveLevel();
		}
		return meanCb - sum / observedRouteCount;
	}
	public double getCostPromotiveMax() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostPromotiveMax();
		}
		return sum / observedRouteCount;
	}
	public double getCbMinusCpm() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostPromotiveLevel();
		}
		return meanCb - sum / observedRouteCount;
	}
	//---------   Cost Demotive:    ------------
	public double getCostDemotiveLevel() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostDemotiveLevel();
		}
		return sum / observedRouteCount;
	}
	public double getCbPlusCdl() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostDemotiveLevel();
		}
		return meanCb + sum / observedRouteCount;
	}
	public double getCostDemotiveMax() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostDemotiveMax();
		}
		return sum / observedRouteCount;
	}
	public double getCbPlusCdm() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCostDemotiveMax();
		}
		return meanCb + sum / observedRouteCount;
	}
	//---------   General Traffic:    ------------
	public double getTrafficLong() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficLong();
		}
		return sum / observedRouteCount;
	}
	public double getTrafficShort() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficShort();
		}
		return sum / observedRouteCount;
	}
	//---------  Traffic Promotive:    ------------
	public double getTrafficPromotiveLong() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficPromotiveLong();
		}
		return sum / observedRouteCount;
	}
	public double getTrafficPromotiveShort() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficPromotiveShort();
		}
		return sum / observedRouteCount;
	}
	public double getTrafficPromtiveFinal() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficPromtiveFinal();
		}
		return sum / observedRouteCount;
	}
	//---------  Traffic Demotive:    ------------
	
	public double getTrafficDemotiveLong() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficDemotiveLong();
		}
		return sum / observedRouteCount;
	}

	public double getTrafficDemotiveShort() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficDemotiveShort();
		}
		return sum / observedRouteCount;
	}

	public double getTrafficDemotiveFinal() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getTrafficDemotiveFinal();
		}
		return sum / observedRouteCount;
	}
	 
	// Groups-related methods
    
	public double getPull() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getPull();
		}
		return sum / observedCenterCount;
	}
    
	public double getLabor() {
		double sum = 0.0;
		for (Center center : observedCenters) {
			sum += (double)center.getLabor();
		}
		return sum / (double)observedCenterCount;
	}

	public double getResSize() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getResSize();
		}
		return sum / observedCenterCount;
	}
	
	public double getStaples(){
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStaples();
		}
		return sum / observedCenterCount;
	}

	public double getFecundityPromotiveLevel() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getFecundityPromotiveLevel();
		}
		return sum / observedCenterCount;
	} 

	public double getFbPlusFpl() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getFecundityPromotiveLevel();
		}
		return 3 + sum / observedCenterCount;
	}

	public double getFecundityDemotiveLevel() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStaples();
		}
		return sum / observedCenterCount;
	}

	public double getFbMinusFdl() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStaples();
		}
		return 3 - sum / observedCenterCount;
	}

	public double getFecundityBase() {
		return 3;
	}

	public double getStaplesPerCap() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStaplesPerCap();
		}
		return sum / observedCenterCount;
	}
	
	// imports-related methods

	public double getImports() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getImports();
		}
		return sum / observedCenterCount;
	}

	public double getImportsPerCap() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getImportsPerCap();
		}
		return sum / observedCenterCount;
	}
	
	public double getDistToExporter() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getDistToExporter();
		}
		return sum / observedCenterCount;
	}
	
	public double getLabor60() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getLabor60();
		}
		return sum / observedCenterCount;
	}	

	public double getStayed() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStayed();
		}
		return sum / observedCenterCount;
	}

	public double getEmigrations() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getEmigrations();
		}
		return sum / observedCenterCount;
	}
	
	public double getStapleRemovals() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getStapleRemovals();
		}
		return sum / observedCenterCount;
	}
	
	public double getImportRemovals() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getImportRemovals();
		}
		return sum / observedCenterCount;
	}

	public double getCreated() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getCreated();
		}
		return sum / observedCenterCount;
	}
	
	public double getSettled() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getSettled();
		}
		return sum / observedCenterCount;
	}
	
	public double getEndemic() {
		double sum = 0;
		for (Center center : observedCenters) {
			sum += center.getEndemic();
		}
		return sum / observedCenterCount;
	}

}
