package fr.inrae.act.bagap.apiland.core.space;

import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImpl;

public class ComplexGeometry<G extends Geometry> extends Geometry{

	/**
	 * version number
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor
	 * @param impl geometry implementation
	 */
	public ComplexGeometry(GeometryImpl impl) {
		super(impl);
	}
	
	public ComplexGeometry(){}
	
	@Override
	public ComplexGeometry<G> clone(){
		ComplexGeometry<G> clone = (ComplexGeometry<G>)super.clone();
		return clone;
	}
	
	@Override
	public Geometry smooth() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	protected void add(G g){
		//TODO 
	}
	
	public Geometry addGeometry(Geometry g) {
		return g.addComplexGeometry(this);
	}

	@Override
	public Geometry addPoint(Point g) {
		
		return null;
	}
	
	@Override
	public Geometry addCurve(Curve g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry addSurface(Surface g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <GG extends Geometry> Geometry addComplexGeometry(ComplexGeometry<GG> g) {
		return null;
	}



}