package fr.inrae.act.bagap.apiland.raster.converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.store.ContentFeatureSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.inrae.act.bagap.apiland.raster.Coverage;
import fr.inrae.act.bagap.apiland.raster.CoverageManager;
import fr.inrae.act.bagap.apiland.raster.EnteteRaster;
import fr.inrae.act.bagap.apiland.raster.RasterLineString;
import fr.inrae.act.bagap.apiland.raster.RasterPolygon;
import fr.inrae.act.bagap.apiland.raster.TabCoverage;

public class ShapeFile2CoverageConverter {

	public static CoordinateReferenceSystem getCoordinateReferenceSystem(String shape){
		
		try{
			ShapefileDataStore SHPdataStore = new ShapefileDataStore(Paths.get(shape).toUri().toURL());
			ContentFeatureSource featureSource = SHPdataStore.getFeatureSource();
			SimpleFeatureType schema = featureSource.getSchema();
			CoordinateReferenceSystem crs = schema.getCoordinateReferenceSystem();
			
			SHPdataStore.dispose();
			
			return crs; 
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (ShapefileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	
		return null;
	}
	
	public static ShapeType getShapeType(String shape){
	
		try{
			ShpFiles sf;
			if(shape.endsWith(".shp")){
				sf = new ShpFiles(shape);
			}else{
				sf = new ShpFiles(shape + ".shp");
			}
			
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new org.locationtech.jts.geom.GeometryFactory());
			
			ShapeType st = sfr.getHeader().getShapeType();
			
			sfr.close();
			
			return st; 
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (ShapefileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	
		return null;
	}
	
	public static void rasterize(String output, String input, String attribute, float fillValue, EnteteRaster entete){
		
		float[] data = null; 
		ShapeType sType = getShapeType(input);
		if(sType.isPolygonType()){
			data = getSurfaceData(input, entete, attribute, fillValue);
		}else if(sType.isLineType()){
			data = getLinearData(input, entete, attribute, fillValue, 0);
		}else{
			throw new IllegalArgumentException("shape type "+sType+" not supported yet");
		}
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, noDataValue);
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, fillValue);
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, Map<String, Integer> codes, float cellSize, int noDataValue, int fillValue, CoordinateReferenceSystem crs){
		
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, codes, fillValue);
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, String attribute, Map<String, Integer> codes, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, codes, fillValue);
		CoverageManager.write(output, data, entete);
	}
	
	public static void rasterize(String output, String input, float cellSize, float value, int noDataValue) {
		
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, null);
		float[] data = getSurfaceData(input, entete, value, noDataValue);
		CoverageManager.write(output, data, entete);
	}
	
	private static EnteteRaster getEntete(String zone, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		try{
			
			ShpFiles sf = new ShpFiles(zone);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			
			Geometry the_geom;
			
			double minx = Double.MAX_VALUE;
			double maxx = Double.MIN_VALUE;
			double miny = Double.MAX_VALUE;
			double maxy = Double.MIN_VALUE;
			
			while(sfr.hasNext()){
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					minx = Math.min(minx, the_geom.getEnvelopeInternal().getMinX());
					maxx = Math.max(maxx, the_geom.getEnvelopeInternal().getMaxX());
					miny = Math.min(miny, the_geom.getEnvelopeInternal().getMinY());
					maxy = Math.max(maxy, the_geom.getEnvelopeInternal().getMaxY());
				}
			}
			
			sfr.close();
			sf.dispose();
			
			return EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public static Coverage getSurfaceCoverage(float[] data, EnteteRaster entete, String input, float fillValue){
		
		getSurfaceData(data, entete, input, fillValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(float[] data, EnteteRaster entete, String input, String attribute){
		
		getSurfaceData(data, entete, input, attribute);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(float[] data, EnteteRaster entete, String input, String attribute, Map<String, Integer> codes){
		
		getSurfaceData(data, entete, input, attribute, codes);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, EnteteRaster entete, int fillValue){
		
		float[] data = getSurfaceData(input, entete, attribute, fillValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, noDataValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, fillValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, EnteteRaster entete, float value, float fillValue){
		
		float[] data = getSurfaceData(input, entete, value, fillValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, Map<String, Integer> codes, EnteteRaster entete, float fillValue){
		
		float[] data = getSurfaceData(input, entete, attribute, codes, fillValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, Map<String, Integer> codes, float cellSize, int noDataValue, CoordinateReferenceSystem crs){
		
		EnteteRaster entete = getEntete(input, cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, codes, noDataValue);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getSurfaceCoverage(String input, String attribute, Map<String, Integer> codes, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getSurfaceData(input, entete, attribute, codes, fillValue);

		return new TabCoverage(data, entete);
	}
	
	private static void getSurfaceData(float[] data, EnteteRaster entete, String inputShape, float fillValue){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			
			Envelope envelopeRef = entete.getEnvelope();
			Envelope envelopeGeom;
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			while(sfr.hasNext()){
				 
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					envelopeGeom = the_geom.getEnvelopeInternal();
					if(envelopeGeom.intersects(envelopeRef)){
							
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
										if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
											data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = fillValue;	
										}
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
											if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
												data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = fillValue;
											}
										}
									}
									indrp++;
								}
							}
								
						}else{
							throw new IllegalArgumentException("probleme geometrique");
						}
					}
				}
			}
			
			sfr.close();
			sf.dispose();
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void getSurfaceData(float[] data, EnteteRaster entete, String inputShape, String attribute){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			while(sfr.hasNext()){
				dfr.read();
				value = dfr.readField(pos).toString();
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
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
									data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
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
										data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
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
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void getSurfaceData(float[] data, EnteteRaster entete, String inputShape, String attribute, Map<String, Integer> codes){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Envelope envelopeRef = entete.getEnvelope();
			Envelope envelopeGeom;
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			float code;
			boolean ok;
			while(sfr.hasNext()){
				dfr.read();
				value = (String) dfr.readField(pos);
				ok = true;
				code = -1;
				if(codes.containsKey(value)){
					code = codes.get(value);	
				}else{
					ok = false;
				}
				 
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null && ok) {
					envelopeGeom = the_geom.getEnvelopeInternal();
					if(envelopeGeom.intersects(envelopeRef)){
						
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
										if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
											data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;	
										}
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
											if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
												data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
											}
										}
									}
									indrp++;
								}
							}
							
						}else{
							throw new IllegalArgumentException("probleme geometrique");
						}
					}
				}
			}
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static float[] getSurfaceData(String inputShape, EnteteRaster entete, String attribute, float fillValue){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			//Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			Coordinate[] coords = new Coordinate[5];
			coords[0] = new Coordinate(entete.minx(), entete.maxy());
			coords[1] = new Coordinate(entete.maxx(), entete.maxy());
			coords[2] = new Coordinate(entete.maxx(), entete.miny());
			coords[3] = new Coordinate(entete.minx(), entete.miny());
			coords[4] = new Coordinate(entete.minx(), entete.maxy());
			PreparedPolygon pgE = new PreparedPolygon(new GeometryFactory().createPolygon(coords));
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			while(sfr.hasNext()){
				dfr.read();
				value = dfr.readField(pos).toString();
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					if(pgE.within(the_geom)) {
						
						//System.out.println("inclusion totale dans le departement "+value);
						
						Arrays.fill(datas, Float.parseFloat(value));
						
						break;
						
					}else if(pgE.intersects(the_geom)) {
						
					//if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
						
						//System.out.println("intersection avec le departement "+value);
						
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
			dfr.close();
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	private static float[] getSurfaceData(String inputShape, EnteteRaster entete, float value, float fillValue){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			
			while(sfr.hasNext()){
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					if(entete.getEnvelope().intersects(the_geom.getEnvelopeInternal())){
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
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	private static float[] getSurfaceData(String inputShape, EnteteRaster entete, String attribute, Map<String, Integer> codes, float fillValue){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Envelope envelopeRef = entete.getEnvelope();
			Envelope envelopeGeom;
			
			Geometry the_geom;
			Polygon the_poly;
			RasterPolygon rp;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			float code;
			while(sfr.hasNext()){
				dfr.read();
				value = dfr.readField(pos).toString();
				if(codes.containsKey(value)){
					code = codes.get(value);	
				}else{
					code = fillValue;
				}
				 
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					envelopeGeom = the_geom.getEnvelopeInternal();
					if(envelopeGeom.intersects(envelopeRef)){
						
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
							throw new IllegalArgumentException("probleme geometrique");
						}
					}
				}
			}
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	public static Coverage getLinearCoverage(float[] data, EnteteRaster entete, String input, float value){
		
		getLinearData(data, entete, input, value, 0);
 
		return new TabCoverage(data, entete);
	}
	
	public static Coverage getLinearCoverage(float[] data, EnteteRaster entete, String input, float value, double buffer){
		
		getLinearData(data, entete, input, value, buffer);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getLinearCoverage(float[] data, EnteteRaster entete, String input, String attribute, Map<String, Integer> codes, double buffer){
		
		getLinearData(data, entete, input, attribute, codes, buffer);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getLinearCoverage(String input, EnteteRaster entete, float value, float fillValue){
		
		float[] data = getLinearData(input, entete, value, fillValue, 0);
 
		return new TabCoverage(data, entete);
	}
	
	public static Coverage getLinearCoverage(String input, EnteteRaster entete, float value, float fillValue, double buffer){
		
		float[] data = getLinearData(input, entete, value, fillValue, buffer);

		return new TabCoverage(data, entete);
	}
	
	private static float[] getLinearData(String inputShape, EnteteRaster entete, float value, float fillValue, double buffer){
		
		float[] data = new float[entete.width()*entete.height()];
		Arrays.fill(data, fillValue);
		
		getLinearData(data, entete, inputShape, value, buffer);
		
		return data;
	}
	
	private static void getLinearData(float[] data, EnteteRaster entete, String inputShape, float value, double buffer){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			
			Envelope envelopeRef = entete.getEnvelope();
			Envelope envelopeGeom;
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			while(sfr.hasNext()){
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					envelopeGeom = the_geom.getEnvelopeInternal();
					if(envelopeGeom.intersects(envelopeRef)){
						
						if(the_geom instanceof LineString){
							the_line = (LineString) the_geom;
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							indrp = 0;
							xdelta = rls.getDeltaI();
							ydelta = rls.getDeltaJ();
							for(double v : rls.getDatas()){
								if(v == 1){
									xrp = indrp % rls.getWidth();
									yrp = indrp / rls.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
											data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
										}
									}
								}
								indrp++;
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
												if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
													data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = value;
												}
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
			sf.dispose();
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private static void getLinearData(float[] data, EnteteRaster entete, String inputShape, String attribute, Map<String, Integer> codes, double buffer){
		
		try{
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Envelope envelopeRef = entete.getEnvelope();
			Envelope envelopeGeom;
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			String value;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			boolean ok;
			int code;
			while(sfr.hasNext()){
				
				dfr.read();
				value = (String) dfr.readField(pos);
				ok = true;
				code = -1;
				if(codes.containsKey(value)){
					code = codes.get(value);	
				}else{
					ok = false;
				}
				
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null && ok) {
					envelopeGeom = the_geom.getEnvelopeInternal();
					if(envelopeGeom.intersects(envelopeRef)){
						
						if(the_geom instanceof LineString){
							the_line = (LineString) the_geom;
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							indrp = 0;
							xdelta = rls.getDeltaI();
							ydelta = rls.getDeltaJ();
							for(double v : rls.getDatas()){
								if(v == 1){
									xrp = indrp % rls.getWidth();
									yrp = indrp / rls.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
											data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
										}
									}
								}
								indrp++;
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
												if(data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] != entete.noDataValue()) {
													data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = code;
												}
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
			dfr.close();
			sf.dispose();
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static Coverage getLinearCoverage(String input, String attribute, EnteteRaster entete, float fillValue, double buffer){
		
		float[] data = getLinearData(input, entete, attribute, fillValue, buffer);

		return new TabCoverage(data, entete);
	}
	
	public static Coverage getLinearCoverage(String input, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue, double buffer){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getLinearData(input, entete, attribute, fillValue, buffer);

		return new TabCoverage(data, entete);
	}
	
	private static float[] getLinearData(String inputShape, EnteteRaster entete, String attribute, float fillValue, double buffer){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Envelope globalEnvelope = new Envelope(entete.minx(), entete.maxx(), entete.miny(), entete.maxy());
			
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			while(sfr.hasNext()){
				dfr.read();
				value = dfr.readField(pos).toString();
				//System.out.println(value);
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					if(the_geom.getEnvelopeInternal().intersects(globalEnvelope)){
						if(the_geom instanceof LineString){
							the_line = (LineString) the_geom;
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							indrp = 0;
							xdelta = rls.getDeltaI();
							ydelta = rls.getDeltaJ();
							for(double v : rls.getDatas()){
								if(v == 1){
									xrp = indrp % rls.getWidth();
									yrp = indrp / rls.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
									}
								}
								indrp++;
							}	
							
						}else if(the_geom instanceof MultiLineString){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								the_line = (LineString) ((MultiLineString) the_geom).getGeometryN(i);
								
								rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
								indrp = 0;
								xdelta = rls.getDeltaI();
								ydelta = rls.getDeltaJ();
								for(double v : rls.getDatas()){
									if(v == 1){
										xrp = indrp % rls.getWidth();
										yrp = indrp / rls.getWidth();
										if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
											datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
										}
									}
									indrp++;
								}
							}
							
						}else{
							throw new IllegalArgumentException("probleme geometrique");
						}
					}	
				}
			}
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	public static Coverage getLinearCoverage(String input, Map<String, String> conditionsAttributeAndValue, String attribute, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue, double buffer){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getLinearData(input, entete, conditionsAttributeAndValue, attribute, fillValue, buffer);

		return new TabCoverage(data, entete);
	}
	
	private static float[] getLinearData(String inputShape, EnteteRaster entete, Map<String, String> conditionsAttributeAndValue, String attribute, float fillValue, double buffer){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			
			Map<String, Integer> conditionsPosition = new TreeMap<String, Integer>();
			for(String cond : conditionsAttributeAndValue.keySet()){
				conditionsPosition.put(cond, -1);
			}
			int posAttribute = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					posAttribute = f;
				}
				for(String cond : conditionsAttributeAndValue.keySet()){
					if (dfh.getFieldName(f).equalsIgnoreCase(cond)) {
						conditionsPosition.put(cond, f);
					}
				}
			}
			
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			String value;
			Map<String, String> conditions = new TreeMap<String, String>();
			boolean ok;
			while(sfr.hasNext()){
				dfr.read();
				value = dfr.readField(posAttribute).toString();
				conditions.clear();
				for(String cond : conditionsAttributeAndValue.keySet()){
					conditions.put(cond, dfr.readField(conditionsPosition.get(cond)).toString());
				}
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				ok = true;
				for(String cond : conditionsAttributeAndValue.keySet()){
					if(!conditionsAttributeAndValue.get(cond).equalsIgnoreCase(conditions.get(cond))){
						ok = false;
					}
				}
				
				if(the_geom != null && ok){
					if(the_geom instanceof LineString){
						the_line = (LineString) the_geom;
						
						rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
						indrp = 0;
						xdelta = rls.getDeltaI();
						ydelta = rls.getDeltaJ();
						for(double v : rls.getDatas()){
							if(v == 1){
								xrp = indrp % rls.getWidth();
								yrp = indrp / rls.getWidth();
								if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
									datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
								}
							}
							indrp++;
						}	
						
					}else if(the_geom instanceof MultiLineString){
						
						for(int i=0; i<the_geom.getNumGeometries(); i++){
							the_line = (LineString) ((MultiLineString) the_geom).getGeometryN(i);
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
							indrp = 0;
							xdelta = rls.getDeltaI();
							ydelta = rls.getDeltaJ();
							for(double v : rls.getDatas()){
								if(v == 1){
									xrp = indrp % rls.getWidth();
									yrp = indrp / rls.getWidth();
									if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
										datas[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = Float.parseFloat(value);
									}
								}
								indrp++;
							}
						}
						
					}else{
						throw new IllegalArgumentException("probleme geometrique");
					}
				}
			}
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	public static Coverage getLinearCoverage(String input, Map<String, String> conditionsAttributeAndValue, float value, float cellSize, int noDataValue, CoordinateReferenceSystem crs, double minx, double maxx, double miny, double maxy, float fillValue, double buffer){
		
		EnteteRaster entete = EnteteRaster.getEntete(new Envelope(minx, maxx, miny, maxy), cellSize, noDataValue, crs);
		float[] data = getLinearData(input, entete, conditionsAttributeAndValue, value, fillValue, buffer);

		return new TabCoverage(data, entete);
	}
	
	private static float[] getLinearData(String inputShape, EnteteRaster entete, Map<String, String> conditionsAttributeAndValue, float value, float fillValue, double buffer){
		try{
			
			ShpFiles sf = new ShpFiles(inputShape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			
			Map<String, Integer> conditionsPosition = new TreeMap<String, Integer>();
			for(String cond : conditionsAttributeAndValue.keySet()){
				conditionsPosition.put(cond, -1);
			}
			for (int f=0; f<dfh.getNumFields(); f++) {
				for(String cond : conditionsAttributeAndValue.keySet()){
					if (dfh.getFieldName(f).equalsIgnoreCase(cond)) {
						conditionsPosition.put(cond, f);
					}
				}
			}
			
			float[] datas = new float[entete.width()*entete.height()];
			Arrays.fill(datas, fillValue);
			
			Geometry the_geom;
			LineString the_line;
			RasterLineString rls;
			int indrp;
			int xdelta, ydelta, xrp, yrp;
			Map<String, String> conditions = new TreeMap<String, String>();
			boolean ok;
			while(sfr.hasNext()){
				dfr.read();
				conditions.clear();
				for(String cond : conditionsAttributeAndValue.keySet()){
					conditions.put(cond, dfr.readField(conditionsPosition.get(cond)).toString());
				}
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				ok = true;
				for(String cond : conditionsAttributeAndValue.keySet()){
					if(!conditionsAttributeAndValue.get(cond).equalsIgnoreCase(conditions.get(cond))){
						ok = false;
					}
				}
				
				if(the_geom != null && ok){
					if(the_geom instanceof LineString){
						the_line = (LineString) the_geom;
						
						rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
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
						
					}else if(the_geom instanceof MultiLineString){
						
						for(int i=0; i<the_geom.getNumGeometries(); i++){
							the_line = (LineString) ((MultiLineString) the_geom).getGeometryN(i);
							
							rls = RasterLineString.getRasterLineString(the_line, entete.minx(), entete.maxx(), entete.miny(), entete.maxy(), entete.cellsize(), buffer);
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
						
					}else{
						throw new IllegalArgumentException("probleme geometrique");
					}
				}
			}
			
			sfr.close();
			dfr.close();
			sf.dispose();
			
			return datas;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}

	public static Envelope getEnvelope(String zone){
		return getEnvelope(zone, 0);
	}
	
	public static Envelope getEnvelope(String zone, double buffer) {
		
		//System.out.println("r�cup�ration de l'enveloppe");
		
		double minx = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double miny = Double.MAX_VALUE;
		double maxy = Double.MIN_VALUE;
		
		try{
			ShpFiles sf = new ShpFiles(zone);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			
			Geometry the_geom;
			while(sfr.hasNext()){
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null){
					minx = Math.min(minx, the_geom.getEnvelopeInternal().getMinX());
					maxx = Math.max(maxx, the_geom.getEnvelopeInternal().getMaxX());
					miny = Math.min(miny, the_geom.getEnvelopeInternal().getMinY());
					maxy = Math.max(maxy, the_geom.getEnvelopeInternal().getMaxY());
				}
				
			}
			
			sfr.close();
			
			return new Envelope(minx-buffer, maxx+buffer, miny-buffer, maxy+buffer);
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Envelope getEnvelope(String zone, double buffer, String attribute, String... values) {
		
		//System.out.println("r�cup�ration de l'enveloppe");
		
		double minx = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double miny = Double.MAX_VALUE;
		double maxy = Double.MIN_VALUE;
		
		try{
			ShpFiles sf = new ShpFiles(zone);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			
			DbaseFileHeader dfh = dfr.getHeader();
			
			int position = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					position = f;
					break;
				}
			}
			
			Geometry the_geom;
			String value;
			while(sfr.hasNext()){
				the_geom = (Geometry) sfr.nextRecord().shape();
				value = dfr.readEntry()[position].toString();
				
				if(the_geom != null){
					for(String v : values) {
						if(value.equalsIgnoreCase(v)) {
							minx = Math.min(minx, the_geom.getEnvelopeInternal().getMinX());
							maxx = Math.max(maxx, the_geom.getEnvelopeInternal().getMaxX());
							miny = Math.min(miny, the_geom.getEnvelopeInternal().getMinY());
							maxy = Math.max(maxy, the_geom.getEnvelopeInternal().getMaxY());
							break;
						}
					}
				}
			}
			
			sfr.close();
			dfr.close();
			
			return new Envelope(minx-buffer, maxx+buffer, miny-buffer, maxy+buffer);
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
