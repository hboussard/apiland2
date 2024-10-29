package fr.inrae.act.bagap.apiland.core.space;

import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImpl;


public class Curve extends Geometry implements Linear {

	/**
	 * version number 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Curve(GeometryImpl impl){
		super(impl);
	}
	
	public String toString(){
		return "curve length = "+getLength();
	}
	
	public Curve clone(){
		Curve clone = (Curve)super.clone();
		return clone;
	}
	
	@Override
	public Curve smooth() {
		if(get().isSmooth()){
			return this;
		}
		return new Curve(get().smooth());
	}

	@Override
	public Geometry addGeometry(Geometry g) {
		return g.addCurve(this);
	}

	@Override
	public Geometry addPoint(Point g) {
		return new Curve(GeometryManager.add(get(), g.get()));
	}

	@Override
	public Geometry addCurve(Curve g) {
		if(intersects(g)){
			if(contains(g)){
				return clone();
			}
			if(within(g)){
				return g.clone();
			}
			return new Curve(GeometryManager.add(get(), g.get()));
		}
		if(touches(g)){
			return new Curve(GeometryManager.add(get(), g.get()));
		}
		MultiCurve mc = new MultiCurve();
		mc.add(g);
		mc.add(g);
		return mc.smooth();
	}
	
	@Override
	public Geometry addSurface(Surface g) {
		return g.addCurve(this);
	}

	@Override
	public <G extends Geometry> Geometry addComplexGeometry(ComplexGeometry<G> g) {
		return g.addCurve(this);
	}


}
