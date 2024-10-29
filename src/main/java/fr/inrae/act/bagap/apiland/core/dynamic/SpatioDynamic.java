package fr.inrae.act.bagap.apiland.core.dynamic;

import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Point;
import fr.inrae.act.bagap.apiland.core.spacetime.SpatioTemporal;
import fr.inrae.act.bagap.apiland.core.time.Instant;

public class SpatioDynamic<ST extends SpatioTemporal> extends Dynamic<ST> implements SpatioDynamical<ST> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public double getArea(Instant t) {
		for(SpatioTemporal st : temporals){
			if(st.isActive(t)){
				return st.getArea();
			}
		}
		return 0;
	}

	@Override
	public Geometry getGeometry(Instant t) {
		for(SpatioTemporal st : temporals){
			if(st.isActive(t)){
				return st.getGeometry();
			}
		}
		return null;
	}

	@Override
	public double getLength(Instant t) {
		for(SpatioTemporal st : temporals){
			if(st.isActive(t)){
				return st.getLength();
			}
		}
		return 0;
	}

	@Override
	public boolean isActive(Instant t, Point g) {
		for(SpatioTemporal st : temporals){
			if(st.isActive(t,g)){
				return true;
			}
		}
		return false;
	}
	

}
