package fr.inrae.act.bagap.apiland.core.element;

import fr.inrae.act.bagap.apiland.core.element.type.DynamicFeatureType;
import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Time;
import fr.inrae.act.bagap.apiland.core.time.TimeException;
import fr.inrae.act.bagap.apiland.core.time.TimeNulle;

/**
 * modeling class of the default dynamic feature implementation
 * @author H. BOUSSARD
 */
public class DefaultDynamicFeature extends AbstractDynamicElement implements DynamicFeature{

	/**
	 * version number
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * time
	 */
	private Time time;
	
	/**
	 * constructor
	 */
	public DefaultDynamicFeature(DynamicFeatureType type){
		super(type);
	}
	
	@Override
	public DynamicFeatureType getType(){
		return (DynamicFeatureType) super.getType();
	}
	
	@Override
	public DefaultDynamicFeature clone(){
		DefaultDynamicFeature clone = (DefaultDynamicFeature)super.clone();
		return clone;
	}
	
	@Override
	public Time getTime() {
		return time;
	}
	
	@Override
	public void setTime(Time t) {
		this.time = t;
		//getStructure().getDefaultRepresentation().setTime(t);
	}
	
	@Override
	public void kill(Instant t) throws TimeException{
		time = Time.kill(time,t);
		super.kill(t);
	}
	
	@Override
	public int count(Instant t) {
		if(isActive(t)){
			return 1;
		}
		return 0;
	}

	@Override
	public void display() {
		System.out.println("default dynamic feature : "+getId());
	}

	@Override
	public double getArea(Instant t) {
		return getStructure().getDefaultRepresentation().getArea(t);
	}

	@Override
	public Geometry getGeometry(Instant t) {
		return getStructure().getDefaultRepresentation().getGeometry(t);
	}

	@Override
	public double getLength(Instant t) {
		return getStructure().getDefaultRepresentation().getLength(t);
	}

	@Override
	public boolean isActive(Instant t, Point g) {
		return getStructure().getDefaultRepresentation().isActive(t);
	}

	@Override
	public boolean isActive(Instant t) {
		return time.isActive(t);
	}
	
	@Override
	public Instant getLastChange(){
		Instant t = super.getLastChange();
		Instant n = getStructure().getLastChange(); 
		if(t == null || t instanceof TimeNulle || (n != null && n.isAfter(t))){
			t = n;
		}
		return t;
	}

	@Override
	public void delete(){
		super.delete();
		time = null;
	}
	
}
