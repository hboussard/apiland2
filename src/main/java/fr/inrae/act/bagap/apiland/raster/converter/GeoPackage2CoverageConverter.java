package fr.inrae.act.bagap.apiland.raster.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.inrae.act.bagap.apiland.raster.CoverageManager;
import fr.inrae.act.bagap.apiland.raster.EnteteRaster;
import fr.inrae.act.bagap.apiland.raster.RasterLineString;
import fr.inrae.act.bagap.apiland.raster.RasterPolygon;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class GeoPackage2CoverageConverter {

	public static String getGeoPackageType(String geoPackage){
		
		String type = "";
		
		try{
			GeoPackage gp = new GeoPackage(new File(geoPackage));
			FeatureEntry fe = gp.features().get(0);
			Geometries g = fe.getGeometryType();
			
			type = g.toString();
			
			gp.close();
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return type;
	}
	
	public static void rasterize(String output, String input, float value, float fillValue, EnteteRaster entete) {
		
		System.out.println("rasterize "+input);
		
		String type = getGeoPackageType(input);
		float[] data;
		if(type.equalsIgnoreCase("LineString")) {
			
			data = getLinearData(input, entete, value, fillValue, 0);
			
		}else if(type.equalsIgnoreCase("MultiPolygon")) {
			
			data = getSurfaceData(input, entete, value, fillValue);
			
		}else {
			throw new IllegalArgumentException("gpkg type "+type+" not supported yet");
		}
		
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, float value, float fillValue, EnteteRaster entete, double buffer) {
		
		System.out.println("rasterize "+input);
		
		String type = getGeoPackageType(input);
		float[] data;
		if(type.equalsIgnoreCase("LineString")) {
			
			data = getLinearData(input, entete, value, fillValue, buffer);
			
		}else if(type.equalsIgnoreCase("MultiPolygon")) {
			
			data = getSurfaceData(input, entete, value, fillValue);
			
		}else {
			throw new IllegalArgumentException("gpkg type "+type+" not supported yet");
		}
		
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, float fillValue, EnteteRaster entete){
		
		System.out.println("rasterize "+input);
		
		float[] data = null; 
		data = getSurfaceData(input, entete, attribute, fillValue);
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, float fillValue, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, crs);
		rasterize(output, input, attribute, fillValue, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, Map<String, Integer> codes, float fillValue, EnteteRaster entete){
		
		System.out.println("rasterize "+input);
		
		String type = getGeoPackageType(input);
		float[] data;
		if(type.equalsIgnoreCase("LineString")) {
			
			data = getLinearData(input, entete, attribute, codes, fillValue, 0);
			
		}else if(type.equalsIgnoreCase("MultiPolygon")) {
			
			data = getSurfaceData(input, entete, attribute, codes, fillValue);
			
		}else {
			throw new IllegalArgumentException("gpkg type "+type+" not supported yet");
		}
		
		
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, Map<String, Integer> codes, float fillValue, EnteteRaster entete, double buffer){
		
		System.out.println("rasterize "+input);
		
		String type = getGeoPackageType(input);
		float[] data;
		if(type.equalsIgnoreCase("LineString")) {
			
			data = getLinearData(input, entete, attribute, codes, fillValue, buffer);
			
		}else if(type.equalsIgnoreCase("MultiPolygon")) {
			
			data = getSurfaceData(input, entete, attribute, codes, fillValue);
			
		}else {
			throw new IllegalArgumentException("gpkg type "+type+" not supported yet");
		}
		
		
		CoverageManager.write(output, data, entete);
	}
	
	private static EnteteRaster getEntete(String zone, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		try{
			
			GeoPackage gp = new GeoPackage(new File(zone));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			double minx = Double.MAX_VALUE;
			double maxx = Double.MIN_VALUE;
			double miny = Double.MAX_VALUE;
			double maxy = Double.MIN_VALUE;
			
			Geometry the_geom;
			while(sfr.hasNext()){
				
				the_geom = (Geometry) sfr.next().getDefaultGeometry();
				
				if(the_geom != null){
					minx = Math.min(minx, the_geom.getEnvelopeInternal().getMinX());
					maxx = Math.max(maxx, the_geom.getEnvelopeInternal().getMaxX());
					miny = Math.min(miny, the_geom.getEnvelopeInternal().getMinY());
					maxy = Math.max(maxy, the_geom.getEnvelopeInternal().getMaxY());
				}
			}
			
			sfr.close();
			gp.close();
			
			return EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	private static float[] getSurfaceData(String input, EnteteRaster entete, float value, float fillValue){
		
		try {
			GeoPackage gp = new GeoPackage(new File(input));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			while(sfr.hasNext()) {
			    SimpleFeature sf = sfr.next();
			    the_geom = (Geometry) sf.getDefaultGeometry();
			    
			    if(the_geom != null){
			    	if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
				    	if(the_geom instanceof Polygon){
							the_poly = (Polygon) the_geom;
							
							rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
							indrp = 0;
							xdelta = rp.getDeltaI();
							ydelta = rp.getDeltaJ();
							for(double v : rp.getDatas()){
								if(v == 1){
									xrp = indrp % rp.getWidth();
									yrp = indrp / rp.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
									}
								}
								indrp++;
							}	
							
						}else if(the_geom instanceof MultiPolygon){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
								
								rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
								indrp = 0;
								xdelta = rp.getDeltaI();
								ydelta = rp.getDeltaJ();
								for(double v : rp.getDatas()){
									if(v == 1){
										xrp = indrp % rp.getWidth();
										yrp = indrp / rp.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
										}
									}
									indrp++;
								}
							}
							
						}else{
							System.out.println(the_geom);
							//throw new IllegalArgumentException("probleme geometrique");
						}
				    }
			    }
			}
			sfr.close();
			gp.close();
			
			return datas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static float[] getSurfaceData(String input, EnteteRaster entete, String attribute, float fillValue){
		
		try {
			GeoPackage gp = new GeoPackage(new File(input));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			while(sfr.hasNext()) {
			    SimpleFeature sf = sfr.next();
			    the_geom = (Geometry) sf.getDefaultGeometry();
			    value = sf.getAttribute(attribute).toString();
			    
			    if(the_geom != null){
			    	if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
				    	if(the_geom instanceof Polygon){
							the_poly = (Polygon) the_geom;
							
							rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
							indrp = 0;
							xdelta = rp.getDeltaI();
							ydelta = rp.getDeltaJ();
							for(double v : rp.getDatas()){
								if(v == 1){
									xrp = indrp % rp.getWidth();
									yrp = indrp / rp.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
									}
								}
								indrp++;
							}	
							
						}else if(the_geom instanceof MultiPolygon){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
								
								rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
								indrp = 0;
								xdelta = rp.getDeltaI();
								ydelta = rp.getDeltaJ();
								for(double v : rp.getDatas()){
									if(v == 1){
										xrp = indrp % rp.getWidth();
										yrp = indrp / rp.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
										}
									}
									indrp++;
								}
							}
							
						}else{
							System.out.println(the_geom);
							//throw new IllegalArgumentException("probleme geometrique");
						}
				    }
			    }
			}
			sfr.close();
			gp.close();
			
			return datas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static float[] getSurfaceData(String input, EnteteRaster entete, String attribute, Map<String, Integer> codes, float fillValue){
		
		try {
			GeoPackage gp = new GeoPackage(new File(input));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			float code;
			while(sfr.hasNext()) {
			    SimpleFeature sf = sfr.next();
			    the_geom = (Geometry) sf.getDefaultGeometry();
			    value = sf.getAttribute(attribute).toString();
			    if(codes.containsKey(value)){
					code = codes.get(value);	
				}else{
					code = fillValue;
				}
			    
			    if(the_geom != null){
			    	if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
				    	if(the_geom instanceof Polygon){
							the_poly = (Polygon) the_geom;
							
							rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
							indrp = 0;
							xdelta = rp.getDeltaI();
							ydelta = rp.getDeltaJ();
							for(double v : rp.getDatas()){
								if(v == 1){
									xrp = indrp % rp.getWidth();
									yrp = indrp / rp.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
									}
								}
								indrp++;
							}	
							
						}else if(the_geom instanceof MultiPolygon){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
								
								rp = RasterPolygon.getRasterPolygon(the_poly, entete.minx(), entete.maxy(), entete.cellsize());
								indrp = 0;
								xdelta = rp.getDeltaI();
								ydelta = rp.getDeltaJ();
								for(double v : rp.getDatas()){
									if(v == 1){
										xrp = indrp % rp.getWidth();
										yrp = indrp / rp.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
										}
									}
									indrp++;
								}
							}
							
						}else{
							System.out.println(the_geom);
							//throw new IllegalArgumentException("probleme geometrique");
						}
				    }
			    }
			}
			sfr.close();
			gp.close();
			
			return datas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static float[] getLinearData(String input, EnteteRaster entete, float value, float fillValue, double buffer){
		
		try {
			GeoPackage gp = new GeoPackage(new File(input));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			while(sfr.hasNext()) {
			    SimpleFeature sf = sfr.next();
			    the_geom = (Geometry) sf.getDefaultGeometry();
			    
			    if(the_geom != null){
			    	if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
				    	if(the_geom instanceof LineString){
				    		the_line = (LineString) the_geom;
							
				    		rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							
				    		if(rls != null) {
				    			indrp = 0;
								xdelta = rls.getDeltaI();
								ydelta = rls.getDeltaJ();
								for(double v : rls.getDatas()){
									if(v == 1){
										xrp = indrp % rls.getWidth();
										yrp = indrp / rls.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
										}
									}
									indrp++;
								}	
				    		}
				    		
							
						}else if(the_geom instanceof MultiLineString){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_line = (LineString) ((MultiLineString) the_geom).getGeometryN(i);
								
								rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
								
								if(rls != null) {
									indrp = 0;
									xdelta = rls.getDeltaI();
									ydelta = rls.getDeltaJ();
									for(double v : rls.getDatas()){
										if(v == 1){
											xrp = indrp % rls.getWidth();
											yrp = indrp / rls.getWidth();
											if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
												datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
											}
										}
										indrp++;
									}
								}
								
							}
							
						}else{
							System.out.println(the_geom);
							//throw new IllegalArgumentException("probleme geometrique");
						}
				    }
			    }
			}
			sfr.close();
			gp.close();
			
			return datas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static float[] getLinearData(String input, EnteteRaster entete, String attribute, Map<String, Integer> codes, float fillValue, double buffer){
		
		try {
			GeoPackage gp = new GeoPackage(new File(input));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			float code;
			while(sfr.hasNext()) {
			    SimpleFeature sf = sfr.next();
			    the_geom = (Geometry) sf.getDefaultGeometry();
			    value = sf.getAttribute(attribute).toString();
			    if(codes.containsKey(value)){
					code = codes.get(value);	
				}else{
					code = fillValue;
				}
			    
			    if(the_geom != null){
			    	if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
				    	if(the_geom instanceof LineString){
							the_line = (LineString) the_geom;
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							
							if(rls != null) {
								indrp = 0;
								xdelta = rls.getDeltaI();
								ydelta = rls.getDeltaJ();
								for(double v : rls.getDatas()){
									if(v == 1){
										xrp = indrp % rls.getWidth();
										yrp = indrp / rls.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
										}
									}
									indrp++;
								}	
							}
							
						}else if(the_geom instanceof MultiLineString){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_line = (LineString) ((MultiLineString) the_geom).getGeometryN(i);
								
								rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
								
								if(rls != null) {
									indrp = 0;
									xdelta = rls.getDeltaI();
									ydelta = rls.getDeltaJ();
									for(double v : rls.getDatas()){
										if(v == 1){
											xrp = indrp % rls.getWidth();
											yrp = indrp / rls.getWidth();
											if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
												datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
											}
										}
										indrp++;
									}
								}
							}
							
						}else{
							System.out.println(the_geom);
							//throw new IllegalArgumentException("probleme geometrique");
						}
				    }
			    }
			}
			sfr.close();
			gp.close();
			
			return datas;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
