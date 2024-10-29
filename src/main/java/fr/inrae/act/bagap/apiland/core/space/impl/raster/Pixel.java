package fr.inrae.act.bagap.apiland.core.space.impl.raster;

import java.util.Iterator;

import org.locationtech.jts.geom.Coordinate;

import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImpl;

public class Pixel extends Raster implements Comparable<Pixel>{
	
	/**
	 * version number
	 */
	private static final long serialVersionUID = 1L;
	
	private int x;
	
	private int y;
	
	public Pixel(int x, int y){
		this.x = x;
		this.y = y;
		smooth = true;
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	@Override
	public Iterator<Pixel> iterator() {
		return new SinglePixelIterator(this);
	}

	public Coordinate getCentroid(){
		return new Coordinate(x*size+size/2,y*size+size/2);
	}
	
	@Override
	public Iterator<Pixel> getBoundaries(){
		return new BoundaryIterator(this);
	}
	
	@Override
	public Iterator<Pixel> getMargins(){
		return new MarginSinglePixelIterator(this);
	}
	
	@Override
	public boolean equals(Object p){
		if(p instanceof Pixel){
			//System.out.println(((x == ((Pixel)p).x) && (y == ((Pixel)p).y))+" "+y+" "+(((Pixel)p).y));
			return (x == ((Pixel)p).x) && (y == ((Pixel)p).y);
		}
		//System.out.println("pas egal");
		return false;
	}
	
	@Override
	public int hashCode(){
		//System.out.println("ici "+x+" "+y+" "+(x * 152 + y * 7 + 1));
		return x * 152 + y * 7 + 1;
	}
	
	public boolean equals(int x, int y){
		return (this.x == x) && (this.y == y);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int compareTo(Pixel p) {
		if(this.y < p.y){
			return -1;
		}else if(this.y > p.y){
			return 1;
		}else{
			if(this.x < p.x){
				return -1;
			}else if(this.x > p.x){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	@Override
	public Pixel clone(){
		return this;
	}
	
	@Override
	public int count(){
		return 1;
	}
	
	@Override
	public int size(){
		return 1;
	}
	
	@Override
	public double getArea() {
		return Math.pow(size,2);
	}
	
	@Override
	public double getLength(){
		return 0;
	}
	
	@Override
	public boolean isSmooth() {
		return true;
	}

	@Override
	public Raster smooth() {
		return this;
	}
	
	@Override
	public boolean equals(GeometryImpl impl){
		try{
			return ((Raster)impl).equalsPixel(this);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override	
	protected boolean equalsPixel(Pixel impl) {
		return (x == impl.x) && (y == impl.y);
	}

	@Override
	protected boolean equalsPixelComposite(PixelComposite impl) {
		return impl.equalsPixel(this);
	}
	
	@Override
	protected boolean equalsRasterComposite(RasterComposite impl) {
		return impl.equalsPixel(this);
	}
	
	@Override
	public boolean contains(GeometryImpl impl){
		try{
			return ((Raster)impl).withinPixel(this);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	protected boolean containsPixel(Pixel impl) {
		return equalsPixel(impl);
	}

	@Override
	protected boolean containsPixelComposite(PixelComposite impl) {
		return impl.withinPixel(this);
	}
	
	@Override
	protected boolean containsRasterComposite(RasterComposite impl) {
		return impl.withinPixel(this);
	}
	
	@Override
	public boolean crosses(GeometryImpl impl) {
		try{
			return ((Raster)impl).crossesPixel(this);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected boolean crossesPixel(Pixel impl) {
		return false;
	}

	@Override
	protected boolean crossesPixelComposite(PixelComposite impl) {
		return impl.crossesPixel(this);
	}
	
	@Override
	protected boolean crossesRasterComposite(RasterComposite impl) {
		return impl.crossesPixel(this);
	}
	
	@Override
	public boolean touches(GeometryImpl impl){
		try{
			return ((Raster)impl).touchesPixel(this);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	protected boolean touchesPixel(Pixel impl) {
		return x <= impl.x+1
			&& x >= impl.x-1
			&& y <= impl.y+1
			&& y >= impl.y-1
			&& !equalsPixel(impl);
	}
	
	public boolean rootTouches(Pixel impl) {
		return x == impl.x && y == impl.y-1
			|| x == impl.x && y == impl.y+1
			|| y == impl.y && x == impl.x-1
			|| y == impl.y && x == impl.x+1;
	}

	@Override
	protected boolean touchesPixelWithoutContainsTest(Pixel impl) {
		return x <= impl.x+1
		&& x >= impl.x-1
		&& y <= impl.y+1
		&& y >= impl.y-1;
	}
	
	@Override
	protected boolean touchesPixelComposite(PixelComposite impl) {
		return impl.touchesPixel(this);
	}
	
	@Override
	protected boolean touchesRasterComposite(RasterComposite impl) {
		return impl.touchesPixel(this);
	}
	
	@Override
	public boolean within(GeometryImpl impl){
		try{
			return ((Raster)impl).containsPixel(this);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected boolean withinPixel(Pixel impl) {
		return equalsPixel(impl);
	}

	@Override
	protected boolean withinPixelComposite(PixelComposite impl) {
		return impl.containsPixel(this);
	}

	@Override
	protected boolean withinRasterComposite(RasterComposite impl) {
		return impl.containsPixel(this);
	}
	
	@Override
	public boolean intersects(GeometryImpl impl){
		try{
			return ((Raster)impl).intersectsPixel(this);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	protected boolean intersectsPixel(Pixel impl) {
		return equalsPixel(impl);
	}

	@Override
	protected boolean intersectsPixelComposite(PixelComposite impl) {
		return impl.intersectsPixel(this);
	}
	
	@Override
	protected boolean intersectsRasterComposite(RasterComposite impl) {
		return impl.intersectsPixel(this);
	}
	
	@Override
	public boolean disjoint(GeometryImpl impl){
		try{
			return ((Raster)impl).disjointPixel(this);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected boolean disjointPixel(Pixel impl) {
		return !equalsPixel(impl);
	}

	@Override
	protected boolean disjointPixelComposite(PixelComposite impl) {
		return impl.disjointPixel(this);
	}

	@Override
	protected boolean disjointRasterComposite(RasterComposite impl) {
		return impl.disjointPixel(this);
	}
	
	@Override
	public boolean overlaps(GeometryImpl impl){
		try{
			return ((Raster)impl).overlapsPixel(this);
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	protected boolean overlapsPixel(Pixel impl) {
		return equalsPixel(impl);
	}

	@Override
	protected boolean overlapsPixelComposite(PixelComposite impl) {
		return impl.overlapsPixel(this);
	}
	
	@Override
	protected boolean overlapsRasterComposite(RasterComposite impl) {
		return impl.overlapsPixel(this);
	}

	@Override
	public Raster addGeometryImpl(GeometryImpl impl){
		try{
			return ((Raster)impl).addPixel(this);
		}catch(Exception ex){
			return this;
		}
	}

	@Override
	protected Raster addPixel(Pixel impl) {
		if(equalsPixel(impl)){
			return this;
		}
		if(touchesPixel(impl)){
			PixelComposite pc = new PixelComposite();
			pc.add(this);
			pc.add(impl);
			pc.setSmooth(true);
			return pc;
		}
		RasterComposite rc = new RasterComposite();
		rc.add(this);
		rc.add(impl);
		rc.setSmooth(true);
		return rc;
	}

	@Override
	protected Raster addPixelComposite(PixelComposite impl) {
		return impl.addPixel(this);
	}

	@Override
	protected Raster addRasterComposite(RasterComposite impl) {
		return impl.addPixel(this);
	}

	@Override
	public double minX() {
		return x;
	}

	@Override
	public double maxX() {
		return x;
	}

	@Override
	public double minY() {
		return y;
	}

	@Override
	public double maxY() {
		return y;
	}

	@Override
	public Pixel getOne() {
		return this;
	}
	
	public static double distance(Pixel p1, Pixel p2){
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

	


}
