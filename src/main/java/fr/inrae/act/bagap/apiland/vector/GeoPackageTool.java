package fr.inrae.act.bagap.apiland.vector;

import java.io.File;
import java.io.IOException;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

public class GeoPackageTool {

	public static Envelope getEnvelope(String zone){
		return getEnvelope(zone, 0);
	}
	
	public static Envelope getEnvelope(String zone, double buffer) {
		
		//System.out.println("recuperation de l'enveloppe");
		try{
			
			GeoPackage gp = new GeoPackage(new File(zone));
			FeatureEntry fe = gp.features().get(0);
			SimpleFeatureReader sfr = gp.reader(fe, null, null);
			
			double minx = Double.MAX_VALUE;
			double maxx = Double.MIN_VALUE;
			double miny = Double.MAX_VALUE;
			double maxy = Double.MIN_VALUE;
			
			Geometry the_geom;
			SimpleFeature sf;
			while(sfr.hasNext()){
				
				sf = sfr.next();
				
				the_geom = (Geometry) sf.getDefaultGeometry();
				
				if(the_geom != null){
					minx = Math.min(minx, the_geom.getEnvelopeInternal().getMinX());
					maxx = Math.max(maxx, the_geom.getEnvelopeInternal().getMaxX());
					miny = Math.min(miny, the_geom.getEnvelopeInternal().getMinY());
					maxy = Math.max(maxy, the_geom.getEnvelopeInternal().getMaxY());
				}
			}
			
			sfr.close();
			gp.close();
			
			//System.out.println(minx+" "+maxx+" "+miny+" "+maxy);
			
			return new Envelope(minx-buffer, maxx+buffer, miny-buffer, maxy+buffer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
}
