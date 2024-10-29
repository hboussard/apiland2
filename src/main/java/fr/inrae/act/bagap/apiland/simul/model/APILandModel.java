package fr.inrae.act.bagap.apiland.simul.model;

import fr.inrae.act.bagap.apiland.core.element.map.DynamicMap;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.delay.Delay;
import fr.inrae.act.bagap.apiland.simul.Simulation;
import fr.inrae.act.bagap.apiland.simul.Simulator;

public class APILandModel extends CompositeModel {

	private static final long serialVersionUID = 1L;

	private Simulation simulation;
	
	public APILandModel(String name, Instant start, Delay delay, Simulator simulator, DynamicMap map) {
		super(name, start, delay, simulator, null);
		add(new LandscapeModel(simulator, map));
	}

	public DynamicMap map() {
		return landscapeModel().map();
	}
	
	public LandscapeModel landscapeModel(){
		return (LandscapeModel) get("landscape");
	}
	
	@Override
	public void delete(){
		super.delete();
		//model.delete();
	}
	
	@Override
	public void setParent(Model parent) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Simulation simulation(){
		return simulation;
	}
	
	public void setSimulation(Simulation s){
		this.simulation = s;
	}
	
}
