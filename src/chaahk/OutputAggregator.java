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
		for (RepastEdge<Object> vagueRoute : net.getEdges()) {
			System.out.println(observedRouteType);
			Route<Object> route = (Route<Object>) vagueRoute;
			if (observedRouteType.equals("All")) {
				observedRoutes.add(route);
			} else if (route.getType().equals(observedRouteType)) {
				observedRoutes.add(route);
			} else if (observedRouteType.equals("ID")) {
				int observedRouteSourceID = (Integer)params.getValue("observedRouteSourceID");
				int observedRouteTargetID = (Integer)params.getValue("observedRouteTargetID");
				if (observedRouteSourceID == observedRouteTargetID){
					JOptionPane.showMessageDialog(null, "Route target and source ID's cannot be the same.");
					RepastEssentials.EndSimulationRun();
					return;
				}
				boolean obSame = (route.getSourceCenter().getID()==observedRouteSourceID & route.getTargetCenter().getID()==observedRouteTargetID);
				boolean obReverse = (route.getSourceCenter().getID()==observedRouteTargetID & route.getTargetCenter().getID()==observedRouteSourceID);
				if ( obSame | obReverse ) {
					observedRoutes.add(route);	
				} else {
					JOptionPane.showMessageDialog(null, "Invalid Route ID's specified");
					RepastEssentials.EndSimulationRun();
					return;
				}
			}
		}
		observedRouteCount = observedRoutes.size();
	
		double sumCb = 0;
		for (Route route : observedRoutes) {
			sumCb += route.getCb();
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

	public double chartCdl() {
		double sum = 0;
		for (Route route : observedRoutes) {
			sum += route.getCdl();
		}
		return sum / observedRouteCount + meanCb;
	}

	public double chartCb() {
		return meanCb;
	}
	 

}
