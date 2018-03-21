package mayagrations;

import java.util.ArrayList;
import java.util.LinkedList;

import repast.simphony.context.Context;

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

}
