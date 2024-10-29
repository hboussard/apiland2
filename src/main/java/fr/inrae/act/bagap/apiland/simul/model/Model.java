package fr.inrae.act.bagap.apiland.simul.model;

import java.io.Serializable;

import fr.inrae.act.bagap.apiland.core.element.DynamicElement;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.simul.Simulation;
import fr.inrae.act.bagap.apiland.simul.SimulationManager;
import fr.inrae.act.bagap.apiland.simul.Simulator;

public abstract class Model implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private Instant current;
	
	private DynamicElement element;
	
	private Model parent;
	
	public Model(String name, Simulator simulator){
		this.name = name;
		setCurrent(simulator.manager().start());
	}
	
	public Model(String name, Instant start, Simulator simulator){
		this.name = name;
		setCurrent(start);
	}
	
	public Model(String name, Simulator simulator, DynamicElement element){
		this.name = name;
		setCurrent(simulator.manager().start());
		this.element = element;
	}
	
	public Model(String name, Instant start, Simulator simulator, DynamicElement element){
		this.name = name;
		setCurrent(start);
		this.element = element;
	}
	
	/*@Override
	public String toString(){
		return name;
	}*/
	
	public void setParent(Model parent) {
		this.parent = parent;
	}
	
	public Simulator simulator(){
		return simulation().simulator();
	}
	
	public Simulation simulation(){
		return parent.simulation();
	}
	
	public SimulationManager manager(){
		return simulator().manager();
	}
	
	protected Instant current(){
		return current;
	}
	
	public void setCurrent(Instant t){
		this.current = t;
	}
	
	public void initCurrent(Instant t){
		setCurrent(t);
	}
	
	public String getName(){
		return name;
	}
	
	public DynamicElement getElement(){
		return element;
	}
	
	public void setDynamicElement(DynamicElement element){
		this.element = element;
	}
	
	public boolean deepContains(String name){
		return this.name.equalsIgnoreCase(name);
	}
	
	public Model deepGet(String name){
		if(deepContains(name))return this;
		throw new IllegalArgumentException();
	}

	public abstract boolean run(Instant t);
	
	public void delete(){
		name = null;
		current = null;
	}

}
