package fr.inrae.act.bagap.apiland.core.structure;

import fr.inrae.act.bagap.apiland.core.composition.TemporalValue;
import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.spacetime.SpatioTemporal;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.Time;

/**
 * modeling class of a spatio_temporal entity
 * @author H. BOUSSARD
 */
public class TemporalEntity<G extends Geometry> extends TemporalValue<G> implements SpatioTemporal {

	private static final long serialVersionUID = 1L;
	
	public TemporalEntity(Time t, G g){
		super(t,g);
	}
	
	// spatio-temporal
	public boolean equals(TemporalEntity<G> other) {
		return getValue().equals(other.getGeometry()) 
		&& getTime().equals(other.getTime());
	}
	
	@Override
	public boolean isActive(Instant i, Point p) {
		return isActive(i) && isActive(p);
	}
	
	// spatial
	@Override
	public G getGeometry() {
		return getValue();
	}
	
	@Override
	public void setGeometry(Geometry g) {
		setValue((G)g);
	}
	// end spatial

	@Override
	public TemporalEntity<G> clone(){
		TemporalEntity<G> clone = (TemporalEntity<G>) super.clone();
		return clone;
	}

	public TemporalEntity getEntity() {
		return this;
	}
	
	public void setEntity(TemporalEntity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getArea() {
		return getValue().getArea();
	}

	@Override
	public boolean isActive(Point g) {
		return getValue().isActive(g);
	}

	@Override
	public double getLength() {
		return getValue().getLength();
	}

	/*
	@Override
	public double getArea(Instant t) {
		if(isActive(t)){
			return getArea();
		}
		return 0;
	}

	@Override
	public double getLength(Instant t) {
		if(isActive(t)){
			return getValue().getLength();
		}
		return 0;
	}
	*/
	
	
}
