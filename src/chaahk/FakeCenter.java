package chaahk;

import repast.simphony.context.Context;

// this is a center connected to the 4 gateways at the simulations 4 corners that only serves as a target 
// for calculating the shortest path from each center to the closest gateway, which is called in Route.calculateTrafficLong().
// otherwise it sits in extradimensional space doing nothing.
public class FakeCenter extends Center {

	public FakeCenter(int id, Context<Object> context) {
		super(id, context);	
	}
	
	public void calculateStaples() {
		
	}
	
	public void calculateImports() {
		
	}
	
	public void reproduce() {
		System.out.println(this.getLabor());
	}
	
	public int getID() {
		return 1337;
	}

}
