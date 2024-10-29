package fr.inrae.act.bagap.apiland.core.spacetime;

import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.space.Spatial;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Temporal;

/**
 * modeling interface of a spatio-temporal object
 * @author H. BOUSSARD
 */
public interface SpatioTemporal extends Temporal, Spatial {

	/**
	 * test if the spatio-temporal object is
	 * active at the given point and the 
	 * given instant
	 * @param p the given point
	 * @param i the given instant
	 * @return true if the spatio-temporal object is
	 * active at the given point and the 
	 * given instant
	 */
	boolean isActive(Instant t, Point g);
	
	/*
	double getArea(Instant t);
	
	double getLength(Instant t);
	*/
	
	/**
	 * to get the entity 
	 * @return the entity 
	 */
	//TemporalEntity<Geometry> getEntity();
	
	/**
	 * to set an entity
	 * @param e the entity
	 */
	//void setEntity(TemporalEntity<Geometry> e);
	
}
