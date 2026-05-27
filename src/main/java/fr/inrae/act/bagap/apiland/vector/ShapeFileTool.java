package fr.inrae.act.bagap.apiland.vector;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.HashMap;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileWriter;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileHeader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileWriter;
import org.jumpmind.symmetric.csv.CsvReader;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class ShapeFileTool {

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
		
		//System.out.println("recuperation de l'enveloppe");
		
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
	
	public static Envelope getEnvelope(Set<Polygon> polygons, double buffer) {
		
		//System.out.println("recuperation de l'enveloppe");
		
		double minx = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double miny = Double.MAX_VALUE;
		double maxy = Double.MIN_VALUE;
		
		for(Polygon polygon : polygons) {
			
			if(polygon != null){
				
				minx = Math.min(minx, polygon.getEnvelopeInternal().getMinX());
				maxx = Math.max(maxx, polygon.getEnvelopeInternal().getMaxX());
				miny = Math.min(miny, polygon.getEnvelopeInternal().getMinY());
				maxy = Math.max(maxy, polygon.getEnvelopeInternal().getMaxY());
			}
		}
			
		return new Envelope(minx-buffer, maxx+buffer, miny-buffer, maxy+buffer);
	}
	
	public static Set<Polygon> getSurfaceEntities(String shape, String attribute, String code) {
		
		try{
			ShpFiles sf = new ShpFiles(shape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Set<Polygon> sufaces = new HashSet<Polygon>();
			
			Geometry the_geom;
			Polygon the_poly;
			String value;
			Object[] entry;
			while(sfr.hasNext()){
				
				entry = dfr.readEntry();
				value = entry[pos].toString();
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(value.equalsIgnoreCase(code) && the_geom != null) {
					
					if(the_geom instanceof Polygon){
						
						the_poly = (Polygon) the_geom;
						the_poly.setUserData(entry);
						
						sufaces.add(the_poly);	
						
					}else if(the_geom instanceof MultiPolygon){
						
						for(int i=0; i<the_geom.getNumGeometries(); i++){
							
							the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
							the_poly.setUserData(entry);
							
							sufaces.add(the_poly);	
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
			
			return sufaces;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}
	
	public static void getSurfaceEntities(Map<String, Set<Polygon>> entities, String[] shapes, String attribute) {
		
		for(String shape : shapes) {
			
			System.out.println(shape);
			
			try{
				ShpFiles sf = new ShpFiles(shape);
				ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
				DbaseFileReader dfr = new DbaseFileReader(sf, false, Charset.defaultCharset());
				DbaseFileHeader dfh = dfr.getHeader();
				int pos = -1;
				for (int f=0; f<dfh.getNumFields(); f++) {
					if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
						pos = f;
					}
				}
				
				Geometry the_geom;
				Polygon the_poly;
				String value;
				Object[] entry;
				while(sfr.hasNext()){
					
					entry = dfr.readEntry();
					value = entry[pos].toString();
					the_geom = (Geometry) sfr.nextRecord().shape();
					
					if(the_geom != null) {
						
						if(!entities.containsKey(value)) {
							
							entities.put(value, new HashSet<Polygon>());
						}
						
						if(the_geom instanceof Polygon){
							
							the_poly = (Polygon) the_geom;
							the_poly.setUserData(entry);
								
							entities.get(value).add(the_poly);
							
						}else if(the_geom instanceof MultiPolygon){
							
							for(int i=0; i<the_geom.getNumGeometries(); i++){
								
								the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
								the_poly.setUserData(entry);
								
								entities.get(value).add(the_poly);
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
	}
	
	public static Map<String, Set<Polygon>> getSurfaceEntities(String shape, String attribute) {
		
		try{
			ShpFiles sf = new ShpFiles(shape);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			DbaseFileReader dfr = new DbaseFileReader(sf, false, Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int pos = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(attribute)) {
					pos = f;
				}
			}
			
			Map<String, Set<Polygon>> entities = new TreeMap<String, Set<Polygon>>();
			
			Geometry the_geom;
			Polygon the_poly;
			String value;
			Object[] entry;
			while(sfr.hasNext()){
				
				entry = dfr.readEntry();
				value = entry[pos].toString();
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					if(!entities.containsKey(value)) {
						
						entities.put(value, new HashSet<Polygon>());
					}
					
					if(the_geom instanceof Polygon){
						
						the_poly = (Polygon) the_geom;
						the_poly.setUserData(entry);
							
						entities.get(value).add(the_poly);
						
					}else if(the_geom instanceof MultiPolygon){
						
						for(int i=0; i<the_geom.getNumGeometries(); i++){
							
							the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
							the_poly.setUserData(entry);
							
							entities.get(value).add(the_poly);
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
			
			return entities;
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}

	public static void jointurePolygon(String outputShapeFile, String shapefile, String shapeCode, String csvFile, String csvCode) {
		
		try {
			ShpFiles sf = new ShpFiles(shapefile);
			ShapefileReader sfr = new ShapefileReader(sf, true, false, new GeometryFactory());
			ShapefileHeader sfh = sfr.getHeader();
			
			DbaseFileReader dfr = new DbaseFileReader(sf, true,	Charset.defaultCharset());
			DbaseFileHeader dfh = dfr.getHeader();
			int posShapeCode = -1;
			for (int f=0; f<dfh.getNumFields(); f++) {
				if (dfh.getFieldName(f).equalsIgnoreCase(shapeCode)) {
					posShapeCode = f;
				}
			}
			
			Set<Polygon> entities = new HashSet<Polygon>();
			
			Geometry the_geom;
			Polygon the_poly;
			Object[] entry;
			while(sfr.hasNext()){
				
				entry = dfr.readEntry();
				the_geom = (Geometry) sfr.nextRecord().shape();
				
				if(the_geom != null) {
					
					if(the_geom instanceof Polygon){
						
						the_poly = (Polygon) the_geom;
						the_poly.setUserData(entry);
							
						entities.add(the_poly);
						
					}else if(the_geom instanceof MultiPolygon){
						
						for(int i=0; i<the_geom.getNumGeometries(); i++){
							
							the_poly = (Polygon) ((MultiPolygon) the_geom).getGeometryN(i);
							the_poly.setUserData(entry);
							
							entities.add(the_poly);
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
			
			System.out.println("nombre d'entités : "+entities.size());
			
			CsvReader cr = new CsvReader(csvFile);
			cr.setDelimiter(';');
			cr.readHeaders();
			
			int posCsvCode = -1;
			String[] namesCsv = new String[cr.getHeaderCount()-1];
			int r = 0;
			for (int h=0; h<cr.getHeaderCount(); h++) {
				if (cr.getHeader(h).equalsIgnoreCase(csvCode)) {
					
					posCsvCode = h;
					r = -1;
					
				}else {
					
					namesCsv[h+r] = cr.getHeader(h);
				}
			}
			
			int sizeCsv = cr.getHeaderCount();
			
			Map<String, String[]> infos = new HashMap<String, String[]>();
			
			while(cr.readRecord()) {
			
				String code = cr.get(posCsvCode);
				String[] info = new String[sizeCsv-1];
				r = 0;
				for(int i=0; i<sizeCsv; i++) {
					
					if(i == posCsvCode) {
						
						r = -1;
						
					}else {
						
						info[i+r] = cr.get(i);
					}
				}
				
				infos.put(code, info);
			}
			
			cr.close();
			
			Iterator<Polygon> ite = entities.iterator();
			
			while(ite.hasNext()) {
				
				Polygon entity = ite.next();
				
				Object[] userDatas = ((Object[]) entity.getUserData());
				String localShapeCode = userDatas[posShapeCode].toString();
				String[] datas = new String[dfh.getNumFields()+sizeCsv-1];
				
				if(!infos.containsKey(localShapeCode)) {
					
					System.out.println(localShapeCode+" non traité");
					ite.remove();
				}
				
				else {
					
					for(int h=0; h<dfh.getNumFields(); h++) {
						
						if(userDatas[h] != null) {
						
							datas[h] = userDatas[h].toString();
						
						}else {
							
							datas[h] = "";
						}
					}
					for(int h=0; h<infos.get(localShapeCode).length; h++) {
						
						datas[h+dfh.getNumFields()] = infos.get(localShapeCode)[h];
					}
					
					entity.setUserData(datas);
				}
			}
			
			System.out.println("nombre d'entités après traitement : "+entities.size());
			
			FileOutputStream dbf = new FileOutputStream(outputShapeFile + ".dbf");
			WritableByteChannel out = dbf.getChannel(); 
			DbaseFileHeader header = new DbaseFileHeader();
			header.setNumRecords(entities.size());
			
			for (int f=0; f<dfh.getNumFields(); f++) {
				header.addColumn(dfh.getFieldName(f), 'C', 12, f);
			}
			
			for (int f=0; f<namesCsv.length; f++) {
				
				System.out.println(namesCsv[f]);
				
				header.addColumn(namesCsv[f], 'C', 12, f);
			}
			
			DbaseFileWriter dbfW = new DbaseFileWriter(header, out);
			FileOutputStream shp = new FileOutputStream(outputShapeFile + ".shp");
			FileOutputStream shx = new FileOutputStream(outputShapeFile + ".shx");
			ShapefileWriter shapeW = new ShapefileWriter(shp.getChannel(), shx.getChannel());
				
			shapeW.writeHeaders(new Envelope(sfh.minX(), sfh.maxX(), sfh.minY(), sfh.maxY()), ShapeType.POLYGON, entities.size(), 1000000);
			
			for(Polygon entity : entities){
						
				dbfW.write((String[]) entity.getUserData());
				shapeW.writeGeometry(entity);
				
			}
				
			out.close();
			dbf.close();
			dbfW.close();
			shp.close();
			shx.close();
			shapeW.close();	
			
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
}
