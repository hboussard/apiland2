package fr.inrae.act.bagap.apiland.core.composition;

import java.io.Serializable;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObserver;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Time;
import fr.inrae.act.bagap.apiland.core.time.TimeException;
import fr.inrae.act.bagap.apiland.core.time.delay.Delay;

public class StaticAttribute<O extends Serializable> extends Attribute<O>{

	private static final long serialVersionUID = 1L;
	
	private O value;
	
	public StaticAttribute(AttributeType type){
		super(type);
	}
	
	private O getValue(){
		return value;
	}
	
	private void setValue(O o){
		this.value = o;
	}
	
	@Override
	public O getValue(Instant t) {
		return value;
	}
	
	@Override
	public void setValue(Time t, Serializable o) {
		this.value = (O) o;
	}
	
	@Override
	public void replaceValue(Time t, Serializable o){
		this.value = (O) o;
	}
	
	@Override
	public void display() {
		System.out.println(value);
	}
	
	@Override
	public Time getTime() {
		return null;
	}

	@Override
	public boolean isActive(Instant t) {
		return true;
	}

	@Override
	public void kill(Instant t) throws TimeException {
		// do nothing
	}

	@Override
	public void setTime(Time t) {
		// do nothing
	}
	
	@Override
	public void addObserver(ChangeableObserver o) {
		// do nothing
	}

	@Override
	public void removeObserver(ChangeableObserver o) {
		// do nothing
	}

	@Override
	public void notifyObservers(Instant t, Changeable c, Object o) {
		// do nothing
	}
	
	@Override
	public void setValue(TemporalValue<?> tv) {
		this.value = (O) tv.getValue();
	}

	@Override
	public void addTemporal(TemporalValue<O> t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TemporalValue<O> get(int index) {
		return null;
	}

	@Override
	public int size() {
		if(value != null){
			return 1;
		}
		return 0;
	}

	@Override
	public TemporalValue<O> getActive(Instant t) {
		return null;
	}
	
	@Override
	public int getIndex(Instant t) {
		return 0;
	}
	
	@Override
	public TemporalValue<O> getFirst(){
		return null;
	}
	
	@Override
	public TemporalValue<O> getLast(){
		return null;
	}

	@Override
	public Instant getLastChange() {
		return null;
	}
	
	@Override
	public Time getTimeForValue(Serializable o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasValue(Serializable value, Time t) {
		return this.value.equals(value);
	}

	@Override
	public void clear() {
		// do nothing
	}

	@Override
	public void removeTemporal(TemporalValue<O> t) {
		// do nothing
	}

	@Override
	public Instant getLastOccurence(Serializable o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public O[] split(Delay d) {
		return null;
	}

}
