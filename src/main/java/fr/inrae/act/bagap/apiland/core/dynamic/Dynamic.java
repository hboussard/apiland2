package fr.inrae.act.bagap.apiland.core.dynamic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObject;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObserver;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Interval;
import fr.inrae.act.bagap.apiland.core.time.Temporal;
import fr.inrae.act.bagap.apiland.core.time.Time;
import fr.inrae.act.bagap.apiland.core.time.TimeException;
import fr.inrae.act.bagap.apiland.core.time.TimeNulle;

/**
 * modeling class of a dynamic object
 * @author H.BOUSSARD
 * @param <T> temporal type
 */
public class Dynamic<T extends Temporal> implements Dynamical<T>, Iterable<T>{

	private static final long serialVersionUID = 1L;

	/**
	 * the list of times
	 */
	protected List<T> temporals;
	
	/**
	 * list of observers
	 */
	private ChangeableObject changeable;
	
	/**
	 * constructor
	 */
	public Dynamic(){
		temporals = new ArrayList<T>();
		changeable = new ChangeableObject();
	}
	
	public Dynamic<T> cut(Instant start, Instant end){
		Dynamic<T> cut = new Dynamic<T>();
		for(T t : this){
			if(t.getTime().start().isBefore(start) && t.getTime().end().isAfter(start)){
				T temp = (T) t.clone();
				temp.setTime(new Interval(start, t.getTime().end()));
				cut.addTemporal(temp);
			}else if(!t.getTime().start().isBefore(start) && !t.getTime().end().isAfter(end)){
				cut.addTemporal(t);
			}else if(t.getTime().start().isBefore(end) && t.getTime().end().isAfter(end)){
				cut.addTemporal(t);
			}else if(t.getTime().start().equals(end)){
				T temp = (T) t.clone();
				temp.setTime(new Interval(end, t.getTime().end()));
				cut.addTemporal(temp);
			}
		}
		return cut;
	}
	
	public Dynamic<T> clone(){
		try {
			Dynamic<T> clone = (Dynamic<T>)super.clone();
			
			clone.changeable = new ChangeableObject();
			if(temporals != null){
				clone.temporals = new ArrayList<T>();
				for(T t : this){
					clone.temporals.add((T) t.clone());
				}
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString(){
		return temporals.toString();
	}
	
	/**
	 * dynamic construction of time
	 */
	@Override
	public Time getTime() {
		Time time = new TimeNulle();
		for(T tt : this){
			time = time.addTime(tt.getTime());
		}
		return time;
	}

	@Override
	public boolean isActive(Instant t) {
		Iterator<T> ii = iteratorInverse();
		while(ii.hasNext()){
			if(ii.next().isActive(t)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void kill(Instant t) throws TimeException{
		for(T tt : this){
			if(tt.isActive(t)){
				tt.kill(t);
			}
		}
	}

	@Override
	public void setTime(Time t) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<T> iterator() {
		return temporals.iterator();
	}
	
	public Iterator<T> iteratorInverse(){
		return new IteratorInverse<T>(temporals);
	}
	
	@Override
	public void addTemporal(T t) {
		temporals.add(t);
	}
	
	@Override
	public void removeTemporal(T t) {
		temporals.remove(t);
	}

	@Override
	public T getActive(Instant t) {
		Iterator<T> ii = iteratorInverse();
		T time;
		while(ii.hasNext()){
			time = ii.next();
			if(time.isActive(t)){
				return time; 
			}
		}
		return null;
	}

	@Override
	public T get(int index) {
		return temporals.get(index);
	}
	
	@Override
	public int getIndex(Instant t) {
		Iterator<T> ii = iteratorInverse();
		T time;
		int i = size()-1;
		while(ii.hasNext()){
			time = ii.next();
			if(time.isActive(t)){
				return i; 
			}
			i--;
		}
		return i;
	}
	
	@Override
	public void addObserver(ChangeableObserver o) {
		changeable.addObserver(o);
	}

	@Override
	public void removeObserver(ChangeableObserver o) {
		changeable.removeObserver(o);
	}

	@Override
	public void notifyObservers(Instant t, Changeable c, Object o) {
		changeable.notifyObservers(t, c, o);
	}
	
	@Override
	public T getFirst(){
		if(temporals.size() != 0){
			return temporals.get(0);
		}
		return null;
	}
	
	@Override
	public T getLast(){
		if(temporals.size() != 0){
			return temporals.get(temporals.size()-1);
		}
		return null;
	}
	
	public void removeLast(){
		temporals.remove(temporals.size()-1);
	}

	@Override
	public int size() {
		return temporals.size();
	}

	@Override
	public Instant getLastChange() {
		if(temporals.size() != 0){
			return temporals.get(temporals.size()-1).getTime().start();
		}
		return null;
	}
	
	public void delete(){
		changeable.delete();
		changeable = null;
		temporals.clear();
		temporals = null;
	}
	
	public void clear(){
		temporals.clear();
	}
	
}
