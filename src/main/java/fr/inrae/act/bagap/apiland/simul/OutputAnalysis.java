package fr.inrae.act.bagap.apiland.simul;

import fr.inrae.act.bagap.apiland.core.time.Instant;

public abstract class OutputAnalysis {
	
	public void init(Simulator simulator){
		// do nothing
	}
	
	public void init(Scenario scenario){
		// do nothing
	}
	
	public void init(Simulation simulation){
		// do nothing
	}
	
	public void calculate(Simulator simulator){
		// do nothing
	}
	
	public void calculate(Scenario scenario){
		// do nothing
	}
	
	public void calculate(Simulation simulation){
		// do nothing
	}
	
	public void calculate(Simulation simulation, Instant t){
		// do nothing
	}
	
	public void close(Simulator simulator){
		// do nothing
	}
	
	public void close(Scenario scenario){
		// do nothing
	}
	
	public void close(Simulation simulation){
		// do nothing
	}
	
}
