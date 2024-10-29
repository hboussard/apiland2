package fr.inrae.act.bagap.apiland.core.composition;

import java.io.Serializable;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.dynamic.Dynamical;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Temporal;
import fr.inrae.act.bagap.apiland.core.time.Time;
import fr.inrae.act.bagap.apiland.core.time.delay.Delay;
import fr.inrae.act.bagap.apiland.core.time.delay.YearDelay;

public abstract class Attribute<O extends Serializable> implements Dynamical<TemporalValue<O>>, Temporal, Changeable{
	
	private static final long serialVersionUID = 1L;

	private AttributeType type;
	
	public Attribute(AttributeType type){
		setType(type);
	}
	
	@Override
	public String toString(){
		return type.getName();
	}
	
	@Override
	public Attribute<O> clone(){
		try{
			Attribute<O> clone = (Attribute<O>)super.clone(); 
			clone.type = this.type;
			return clone;
		}catch(CloneNotSupportedException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public AttributeType getType(){
		return type;
	}
	
	protected void setType(AttributeType type){
		this.type = type;
	}
	
	public String getName(){
		return type.getName();
	}	
	
	public Class<?> getBinding(){
		return type.getBinding();
	}
	
	public abstract boolean hasValue(Serializable value, Time t);
	
	public abstract O getValue(Instant t);
	
	public abstract void setValue(Time t, Serializable o);
	
	public abstract void setValue(TemporalValue<?> tv);
	
	public abstract void replaceValue(Time t, Serializable o);
	
	public abstract Time getTimeForValue(Serializable o);
	
	public abstract Instant getLastOccurence(Serializable o);
	
	public abstract void display();
	
	public void delete(){
		type = null;
	}
	
	public abstract void clear();

	public abstract O[] split(Delay d);
	
}
