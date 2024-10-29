package fr.inrae.act.bagap.apiland.simul.model;

import fr.inrae.act.bagap.apiland.core.element.DynamicElement;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.delay.Delay;
import fr.inrae.act.bagap.apiland.simul.Simulator;

public abstract class AtomicModel extends Model{

	private static final long serialVersionUID = 1L;

	private Delay delay;
	
	public AtomicModel(String name, Simulator simulator, DynamicElement element){
		super(name, simulator, element);
		this.delay = simulator.manager().delay();
	}
	
	public AtomicModel(String name, Instant start, Delay delay, Simulator simulator, DynamicElement element){
		super(name, start, simulator, element);
		this.delay = delay;
	}
	
	@Override
	public boolean run(Instant t) {
		while(current().isBefore(t) || current().equals(t)){
			if(make(current())){
				setCurrent(delay.next(current()));
			}else{
				return false;
			}
		}
		return true;
	}
	
	public Delay getDelay(){
		return delay;
	}
	
	public abstract boolean make(Instant t);
	
	@Override
	public void delete(){
		super.delete();
		delay = null;
	}

}
