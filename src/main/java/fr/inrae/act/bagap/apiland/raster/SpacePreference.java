package fr.inrae.act.bagap.apiland.raster;

import java.util.prefs.Preferences;
import java.util.HashMap;
import java.util.Map;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class SpacePreference {

	private static Preferences prefs;
	
	private static final String local = "local";
	
	private static final String defaultLocal = "france";
	
	private static final Map<String, CoordinateReferenceSystem> crs;
	
	static {
		
		prefs = Preferences.userRoot();
		
		crs = new HashMap<String, CoordinateReferenceSystem>();
		try {
			crs.put("france", CRS.decode("EPSG:2154"));
			crs.put("ireland", CRS.decode("EPSG:29902"));
			crs.put("great_britain", CRS.decode("EPSG:27700"));
			crs.put("new_zealand", CRS.decode("EPSG:2193"));
			crs.put("chile", CRS.decode("EPSG:32719"));
		} catch (FactoryException e) {
			e.printStackTrace();
		}
	}
	
	public static String getLocal() {
		
		return prefs.get(local, defaultLocal);
	}
	
	public static CoordinateReferenceSystem getCRS() {
				
		return crs.get(prefs.get(local, defaultLocal));
	}
	
	public static String getEPSG() {
		
		return CRS.toSRS(getCRS()).replace(':', '_').toLowerCase()+".prj";
	}
	
	public static void setLocal(String userLocal) {
		
		if(userLocal.equalsIgnoreCase("default")) {
			
			prefs.put(local, defaultLocal);
			
		}else if(!crs.containsKey(userLocal)) {
			
			throw new IllegalArgumentException("local "+userLocal+" undefined yet, please contact chloe@inrae.fr to integrate");
		}else {
			
			prefs.put(local, userLocal);
		}
	}
	
	public static void setCRS(String epsg) {
		
		if(!(epsg.startsWith("EPSG:") || epsg.startsWith("epsg:"))) {
			epsg = "EPSG:"+epsg;
		}
		
		try {
			crs.put("local", CRS.decode(epsg));
			setLocal("local");
		} catch (FactoryException e) {
			e.printStackTrace();
		}
	}
}
