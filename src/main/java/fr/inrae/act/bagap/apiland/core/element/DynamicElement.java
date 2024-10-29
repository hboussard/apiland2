package fr.inrae.act.bagap.apiland.core.element;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObserver;
import fr.inrae.act.bagap.apiland.core.composition.Attribute;
import fr.inrae.act.bagap.apiland.core.composition.Composable;
import fr.inrae.act.bagap.apiland.core.element.type.DynamicElementType;
import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.structure.Structurable;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Temporal;

/**
 * modeling interface of a dynamic element
 * @author H. BOUSSARD
 */
public interface DynamicElement	extends Cloneable, Comparable<DynamicElement>, Temporal, Changeable, ChangeableObserver, Structurable, Composable {
	
	/**
	 * to get the identifying
	 * @return the identifying
	 */
	String getId();
	
	String getInheritedId(String idName);
	
	/**
	 * to set the identifying
	 * @param id the identifying
	 */
	void setId(String id);

	/**
	 * to get the area of the dynamic element
	 * at the given instant
	 * @param t the given instant
	 * @return the area
	 */
	double getArea(Instant t);
	
	/**
	 * to get the length of the dynamic element
	 * at the given instant
	 * @param t the given instant
	 * @return the length
	 */
	double getLength(Instant t);
	
	/**
	 * to get the geometry of the dynamic element
	 * at the given instant
	 * @param t the given instant
	 * @return the geometry
	 */
	Geometry getGeometry(Instant t);
	
	/**
	 * to know if the dynamic element is active
	 * at the given instant and the given point
	 * @param t the given instant
	 * @param g the given point
	 * @return true if active
	 */
	boolean isActive(Instant t, Point g);
	
	/**
	 * to get the number of active element 
	 * at the given instant  
	 * @param t the given instant
	 * @return the number
	 */
	int count(Instant t);
	
	int count(Instant t, DynamicElementType... types);
	
	void setType(DynamicElementType type);
	
	/**
	 * to get the element type
	 * @return the element type
	 */
	DynamicElementType getType();
	
	/**
	 * to get the aggregate layer
	 * if exists 
	 * @return the aggregate layer
	 */
	DynamicLayer<?> getLayer();

	/**
	 * to set the aggregate layer
	 * @param layer
	 */
	void setLayer(DynamicLayer<?> layer);
	
	/**
	 * to clone the dyanmic element
	 * @return
	 */
	@Override
	DynamicElement clone();
	
	/**
	 * to display the dynamic element
	 */
	void display();
	
	/**
	 * to get a inherited attribute
	 * @param attName the attribute name
	 * @return the attribute
	 */
	Attribute<?> getInheritedAttribute(String attName);
	
	/**
	 * to delete properly the element
	 */
	void delete();

	
}
