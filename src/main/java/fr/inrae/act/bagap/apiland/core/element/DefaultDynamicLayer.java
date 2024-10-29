package fr.inrae.act.bagap.apiland.core.element;

import java.util.Set;
import java.util.TreeSet;

import fr.inrae.act.bagap.apiland.core.element.type.DynamicLayerType;
import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Time;
import fr.inrae.act.bagap.apiland.core.time.TimeException;

public class DefaultDynamicLayer<E extends DynamicElement> extends AbstractDynamicLayer<E> {

	private static final long serialVersionUID = 1L;
	
	public DefaultDynamicLayer(DynamicLayerType type){
		super(type);
	}
	
	@Override
	public DefaultDynamicLayer<E> clone(){
		DefaultDynamicLayer<E> clone = (DefaultDynamicLayer<E>)super.clone();
		return clone;
	}
	
	@Override
	public Time getTime() {
		Time t = null;
		for(DynamicElement e : this){
			t = Time.add(t,e.getTime());
		}
		return t;
	}
	
	@Override
	public void setTime(Time t) {
		// do noting
	}

	@Override
	public boolean isActive(Instant t) {
		for(DynamicElement e : this){
			if(e.isActive(t)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void kill(Instant t) throws TimeException {
		for(DynamicElement e : this){
			e.kill(t);
		}
		super.kill(t);
	}

	@Override
	public double getArea(Instant t) {
		double area = 0.0;
		for(E e : this){
			if(e.isActive(t)){
				area += e.getArea(t);
			}
		}
		return area;
	}
	
	@Override
	public double getLength(Instant t) {
		double length = 0.0;
		for(E e : this){
			if(e.isActive(t)){
				length += e.getLength(t);
			}
		}
		return length;
	}

	@Override
	public Geometry getGeometry(Instant t) {
		Geometry g = null;
		for(E e : this){
			g = Geometry.add(g,e.getGeometry(t));
		}
		return g;
	}

	@Override
	public boolean isActive(Instant t, Point g) {
		for(E e : this){
			if(e.isActive(t, g)){
				return true;
			}
		}
		return false;
	}

	@Override
	public <F extends DynamicElement> Set<F> set(Class<F> theClass) {
		Set<F> set = new TreeSet<F>();
		for(E e : this){
			if(e.getClass().equals(theClass)){
				set.add((F) e);
			}
		}
		return set;
	}

}
