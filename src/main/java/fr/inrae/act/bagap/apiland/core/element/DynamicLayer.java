package fr.inrae.act.bagap.apiland.core.element;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import fr.inrae.act.bagap.apiland.core.element.type.DynamicLayerType;
import fr.inrae.act.bagap.apiland.core.time.Instant;

/**
 * modeling interface of a dynamic layer
 * @author H. BOUSSARD
 *
 * @param <T> the temporal object
 * @param <G> the spatial object
 */
public interface DynamicLayer<E extends DynamicElement> extends DynamicElement, Collection<E>{
	
	@Override
	DynamicLayer<E> clone();
	
	Iterator<E> completeIterator();
	
	Iterator<E> activeIterator(Instant t);
	
	<D extends DynamicFeature> Iterator<D> deepIterator();
	
	<D extends DynamicFeature> Iterator<D> activeDeepIterator(Instant t);
	
	E get(String id);
	
	DynamicFeature getDeep(String id);
	
	boolean contains(String id);
	
	@Override
	DynamicLayerType getType();
	
	void setName(String name);
	
	String getName();
	
	<F extends DynamicElement> Set<F> set(Class<F> theClass); 
	
	
}
