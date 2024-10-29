package fr.inrae.act.bagap.apiland.core.space.impl.raster;

import java.util.Iterator;
import java.util.Set;

import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImpl;
import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImplType;

public abstract class Raster implements GeometryImpl, Iterable<Pixel>{

	private static final long serialVersionUID = 1L;
	
	// pixel size
	protected static double size = 1;
	
	//private static int noDataValue = -1;
	
	private double userData;
	
	private GeometryImplType type;
	
	public Raster(){
		this.type = GeometryImplType.RASTER;
		//userData = noDataValue;
	}
	
	public double getUserData(){
		return userData;
	}
	
	public void setUserData(double userData){
		this.userData = userData;
	}
	
	@Override
	public GeometryImplType getType(){
		return type;
	}
	
	public static void setCellSize(double s){
		size = s;
	}
	
	public static double getCellSize(){
		return size;
	}
	/*
	public static void setNoDataValue(int ndtv){
		noDataValue = ndtv;
	}
	
	public static int getNoDataValue(){
		return noDataValue;
	}*/
	
	protected boolean smooth = false;
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Raster)){
			return false;
		}
		for(Pixel p : this){
			if(!((Raster)o).containsPixel(p)){
				return false;
			}
		}
		for(Pixel p : (Raster)o){
			if(!containsPixel(p)){
				return false;
			}
		}
		return true;
	}

	@Override
	public abstract int count();
	
	public abstract int size();
	
	@Override
	public void display(){
		System.out.println(this);
	}
	
	@Override
	public void init(Geometry g){
		//g.initRaster(this);
	}
	
	@Override
	public Raster clone(){
		Raster clone = null;
		try {
			clone = (Raster)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}
	
	@Override
	public abstract Raster smooth();
	
	protected void setSmooth(boolean smooth){
		this.smooth = smooth;
	}
	
	@Override
	public org.locationtech.jts.geom.Geometry getJTS() {
		throw new UnsupportedOperationException();
	}

	/**
	 * to get an iteration of the boundaries pixels
	 * @return iterator of the boundaries pixels
	 */
	public abstract Iterator<Pixel> getBoundaries();
	
	/**
	 * to get one pixel of the raster
	 * @return a single pixel
	 */
	public abstract Pixel getOne();
	
	/**
	 * to get an iteration of the margins pixels
	 * @return iterator of the margins pixels
	 */
	public abstract Iterator<Pixel> getMargins();
	
	protected abstract boolean equalsPixel(Pixel impl);
	
	protected abstract boolean equalsPixelComposite(PixelComposite impl);
	
	protected abstract boolean equalsRasterComposite(RasterComposite impl);
	
	protected abstract boolean containsPixel(Pixel impl);
	
	protected abstract boolean containsPixelComposite(PixelComposite impl);
	
	protected abstract boolean containsRasterComposite(RasterComposite impl);
	
	protected abstract boolean touchesPixel(Pixel impl);
	
	protected abstract boolean touchesPixelWithoutContainsTest(Pixel impl);
	
	protected abstract boolean touchesPixelComposite(PixelComposite impl);
	
	protected abstract boolean touchesRasterComposite(RasterComposite impl);
	
	protected abstract boolean withinPixel(Pixel impl);
	
	protected abstract boolean withinPixelComposite(PixelComposite impl);
	
	protected abstract boolean withinRasterComposite(RasterComposite impl);
	
	protected abstract boolean crossesPixel(Pixel impl);
	
	protected abstract boolean crossesPixelComposite(PixelComposite impl);
	
	protected abstract boolean crossesRasterComposite(RasterComposite impl);
	
	protected abstract boolean intersectsPixel(Pixel impl);
	
	protected abstract boolean intersectsPixelComposite(PixelComposite impl);
	
	protected abstract boolean intersectsRasterComposite(RasterComposite impl);
	
	protected abstract boolean disjointPixel(Pixel impl);
	
	protected abstract boolean disjointPixelComposite(PixelComposite impl);
	
	protected abstract boolean disjointRasterComposite(RasterComposite impl);
	
	protected abstract boolean overlapsPixel(Pixel impl);
	
	protected abstract boolean overlapsPixelComposite(PixelComposite impl);
	
	protected abstract boolean overlapsRasterComposite(RasterComposite impl);

	@Override
	public abstract Raster addGeometryImpl(GeometryImpl impl);
	
	protected abstract Raster addPixel(Pixel impl);
	
	protected abstract Raster addPixelComposite(PixelComposite impl);
	
	protected abstract Raster addRasterComposite(RasterComposite impl);

	/*
	public static double distance(PixelComposite pc1, PixelComposite pc2){
		double min = Double.MAX_VALUE; 
		for(Pixel p1 : pc1){
			for(Pixel p2 : pc2){
				min = Math.min(min, Math.sqrt(Math.pow(p2.x()-p1.x(), 2) + Math.pow(p2.y()-p1.y(), 2)));
			}
			if(min == 0){
				break;
			}
		}
		if(min == Double.MAX_VALUE){
			min = Raster.getNoDataValue();
		}
		return min;
	}
	
	public static double distance(Set<Pixel> set1, Set<Pixel> set2){
		double min = Double.MAX_VALUE; 
		for(Pixel p1 : set1){
			for(Pixel p2 : set2){
				min = Math.min(min, Math.sqrt(Math.pow(p2.x()-p1.x(), 2) + Math.pow(p2.y()-p1.y(), 2)));
			}
			if(min == 0){
				break;
			}
		}
		if(min == Double.MAX_VALUE){
			min = Raster.getNoDataValue();
		}
		return min;
	}
	*/
}
