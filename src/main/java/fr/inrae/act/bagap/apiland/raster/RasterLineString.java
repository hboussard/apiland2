package fr.inrae.act.bagap.apiland.raster;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;

import fr.inrae.act.bagap.apiland.core.space.ComplexGeometry;
import fr.inrae.act.bagap.apiland.core.space.Curve;
import fr.inrae.act.bagap.apiland.core.space.Geometry;
import fr.inrae.act.bagap.apiland.core.space.Surface;

public class RasterLineString extends Geometry {
	
	private static final long serialVersionUID = 1L;

	private int deltaI, deltaJ;
	
	private float[] datas;
	
	private int width, height;
	
	public RasterLineString(int deltaI, int deltaJ, int width, int height, float[] datas){
		this.deltaI = deltaI;
		this.deltaJ = deltaJ;
		this.width = width;
		this.height = height;
		this.datas = datas;
	}
	
	/*
	public static RasterLineString getRasterLineString(LineString line, double minx, double maxy, double cellsize){
		return getRasterLineString(line, minx, maxy, cellsize, 0);
	}
	
	public static RasterLineString getRasterLineString(Lineal line, double minx, double maxy, double cellsize, double buffer){

		Envelope internal = null;
		if(line instanceof LineString){
			internal = ((LineString) line).getEnvelopeInternal();
		}else if(line instanceof MultiLineString){
			internal = ((MultiLineString) line).getEnvelopeInternal();
		}
		
		double iminx = internal.getMinX();
		double imaxx = internal.getMaxX();
		double iminy = internal.getMinY();
		double imaxy = internal.getMaxY();
		
		//System.out.println(iminx+" "+imaxx+" "+iminy+" "+imaxy);
		
		int deltaI = new Double((iminx - minx)/cellsize).intValue();
		int deltaJ = new Double((maxy-imaxy)/cellsize).intValue();
		
		double eminx = minx + cellsize*deltaI;
		double emaxy = maxy - cellsize*deltaJ;
		
		int width = new Double((imaxx - eminx)/cellsize).intValue() + 1;
		int height = new Double((emaxy - iminy)/cellsize).intValue() + 1;
		
		//System.out.println(deltaI+" "+deltaJ+" "+width+" "+height);
		
		float[] datas = new float[width * height];
		
		//PreparedLineString pp = new PreparedLineString(line);
		GeometryFactory gf = new GeometryFactory();
		Point p;
		double x, y;
		for(int j=0; j<height; j++){
			y = emaxy - (cellsize / 2.0) - j * cellsize;
			for(int i=0; i<width; i++){
				x = eminx + (cellsize / 2.0) + i * cellsize;
				p = gf.createPoint(new Coordinate(x, y));
				
				if(((LineString)line).distance(p)<=(((cellsize+buffer)/2)*Math.sqrt(2))){
					//System.out.println(line.distance(p)+" "+((cellsize+buffer)/2)*Math.sqrt(2));
					datas[j*width+i] = 1;
				}
			}	
		}
		
		return new RasterLineString(deltaI, deltaJ, width, height, datas);
	}
	*/
	
	public static RasterLineString getRasterLineString(LineString line, double minx, double maxx, double miny, double maxy, double cellsize){
		return getRasterLineString(line, minx, maxx, miny, maxy, cellsize, 0);
	}
	
	public static RasterLineString getRasterLineString(Lineal line, double minx, double maxx, double miny, double maxy, double cellsize, double buffer){

		Envelope internal = null;
		if(line instanceof LineString){
			internal = ((LineString) line).getEnvelopeInternal();
		}else if(line instanceof MultiLineString){
			internal = ((MultiLineString) line).getEnvelopeInternal();
		}
		
		double iminx = Math.max(minx, internal.getMinX());
		double imaxx = Math.min(maxx, internal.getMaxX());
		double iminy = Math.max(miny, internal.getMinY());
		double imaxy = Math.min(maxy, internal.getMaxY());
		
		//System.out.println(iminx+" "+imaxx+" "+iminy+" "+imaxy);
		
		int deltaI = new Double((iminx - minx)/cellsize).intValue();
		int deltaJ = new Double((maxy-imaxy)/cellsize).intValue();
		
		double eminx = minx + cellsize*deltaI;
		double emaxy = maxy - cellsize*deltaJ;
		
		int width = new Double((imaxx - eminx)/cellsize).intValue() + 1;
		int height = new Double((emaxy - iminy)/cellsize).intValue() + 1;
		
		//System.out.println(deltaI+" "+deltaJ+" "+width+" "+height);
		
		float[] datas = new float[width * height];
		
		//PreparedLineString pp = new PreparedLineString(line);
		GeometryFactory gf = new GeometryFactory();
		Point p;
		double x, y;
		boolean ok = false;
		for(int j=0; j<height; j++){
			y = emaxy - (cellsize / 2.0) - j * cellsize;
			for(int i=0; i<width; i++){
				x = eminx + (cellsize / 2.0) + i * cellsize;
				p = gf.createPoint(new Coordinate(x, y));
				
				if(((LineString)line).distance(p)<=(((cellsize+buffer)/2)*Math.sqrt(2))){
					//System.out.println(line.distance(p)+" "+((cellsize+buffer)/2)*Math.sqrt(2));
					datas[j*width+i] = 1;
					ok = true;
				}
			}	
		}
		
		if(ok) {
			return new RasterLineString(deltaI, deltaJ, width, height, datas);	
		}else {
			return null;
		}
		
	}

	public int getDeltaI() {
		return deltaI;
	}

	public int getDeltaJ() {
		return deltaJ;
	}

	public float[] getDatas() {
		return datas;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public Geometry smooth() {
		return this;
	}

	@Override
	public Geometry addGeometry(Geometry g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Geometry addCurve(Curve g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Geometry addSurface(Surface g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <G extends Geometry> Geometry addComplexGeometry(ComplexGeometry<G> g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Geometry addPoint(fr.inrae.act.bagap.apiland.core.space.Point g) {
		throw new UnsupportedOperationException();
	}
	
}
