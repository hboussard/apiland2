package fr.inrae.act.bagap.apiland.core.space;

import fr.inrae.act.bagap.apiland.core.space.impl.GeometryImpl;
import fr.inrae.act.bagap.apiland.core.space.impl.Vectorial;
import fr.inrae.act.bagap.apiland.core.space.impl.raster.Raster;
import fr.inrae.act.bagap.apiland.core.space.impl.raster.RasterComposite;
import fr.inrae.act.bagap.apiland.core.space.Geometry;

public class GeometryFactory {
	
	public static final org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
	
	public static Geometry create(org.locationtech.jts.geom.Geometry g){
		if(g instanceof org.locationtech.jts.geom.Point){
			return create((org.locationtech.jts.geom.Point)g);
		}else if(g instanceof org.locationtech.jts.geom.LineString){
			return create((org.locationtech.jts.geom.LineString)g);
		}else if(g instanceof org.locationtech.jts.geom.Polygon){
			return create((org.locationtech.jts.geom.Polygon)g);
		}else if(g instanceof org.locationtech.jts.geom.MultiPoint){
			return create((org.locationtech.jts.geom.MultiPoint)g);
		}else if(g instanceof org.locationtech.jts.geom.MultiLineString){
			return create((org.locationtech.jts.geom.MultiLineString)g);
		}else if(g instanceof org.locationtech.jts.geom.MultiPolygon){
			return create((org.locationtech.jts.geom.MultiPolygon)g);
		}else if(g instanceof org.locationtech.jts.geom.GeometryCollection){
			return create((org.locationtech.jts.geom.GeometryCollection)g);
		}
		throw new IllegalArgumentException();
	}
	
	public static Surface create(org.locationtech.jts.geom.Polygon g){
		return new Surface(new Vectorial(g));
	}
	
	public static MultiCurve create(org.locationtech.jts.geom.LineString g){
		return new MultiCurve(new Vectorial(g));
	}
	
	public static Point create(org.locationtech.jts.geom.Point g){
		return new Point(new Vectorial(g));
	}
	
	public static MultiSurface create(org.locationtech.jts.geom.MultiPolygon g){
		return new MultiSurface(new Vectorial(g));
	}
	
	public static MultiCurve create(org.locationtech.jts.geom.MultiLineString g){
		return new MultiCurve(new Vectorial(g));
	}
	
	public static MultiPoint create(org.locationtech.jts.geom.MultiPoint g){
		return new MultiPoint(new Vectorial(g));
	}
	
	public static ComplexGeometry<Geometry> create(org.locationtech.jts.geom.GeometryCollection g){
		return new ComplexGeometry<Geometry>(new Vectorial(g));
	}
	
	public static Geometry create(Raster r){
		return new Surface(r);
	}
	
	public static Geometry create(RasterComposite r){
		return new MultiSurface(r);
	}
	
	public static Geometry create(GeometryImpl impl){
		if(impl instanceof RasterComposite){
			return create((RasterComposite)impl);
		}else if(impl instanceof Raster){
			return create((Raster)impl);
		}
		throw new IllegalArgumentException();
	}

}
