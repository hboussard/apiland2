package fr.inrae.act.bagap.apiland.raster;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import fr.inrae.act.bagap.apiland.core.composition.Attribute;
import fr.inrae.act.bagap.apiland.core.element.DynamicFeature;
import fr.inrae.act.bagap.apiland.core.element.DynamicLayer;
import fr.inrae.act.bagap.apiland.core.time.Instant;

public class Raster {

	private static int noDataValue = -1;
	
	public static void setNoDataValue(int ndtv){
		noDataValue = ndtv;
	}
	
	public static int getNoDataValue(){
		return noDataValue;
	}
	
	public static float[] getFloatData(DynamicLayer<?> layer, String attribute, Instant t, EnteteRaster entete) {
		float[] data = new float[entete.width()*entete.height()];
		Arrays.fill(data, entete.noDataValue());
		
		int indrp;
		int xdelta, ydelta, xrp, yrp;
		DynamicFeature f;
		Iterator<DynamicFeature> ite = layer.deepIterator();
		while(ite.hasNext()){
			f = ite.next();
			
			RasterPolygon rp = (RasterPolygon) f.getRepresentation("raster").getGeometry(t);
			
			/*
			double value = 0;
			Attribute<Double> att = (Attribute<Double>) f.getAttribute(attribute);
			if(att.isActive(t)) {
				value = att.getValue(t);
			}
			*/
			String value = "0";
			Attribute<?> att = f.getAttribute(attribute);
			if(att.isActive(t)) {
				value = att.getValue(t).toString();
			}
			
			float fValue = Float.parseFloat(value);
					
			indrp = 0;
			xdelta = rp.getDeltaI();
			ydelta = rp.getDeltaJ();
			for(double v : rp.getDatas()){
				if(v == 1){
					xrp = indrp % rp.getWidth();
					yrp = indrp / rp.getWidth();
					if(xdelta+xrp >= 0 && xdelta+xrp < entete.width() && ydelta+yrp >= 0 && ydelta+yrp < entete.height()){
						data[(ydelta+yrp)*entete.width() + (xdelta+xrp)] = fValue;
					}
				}
				indrp++;
			}
		}
		
		return data;
	}
	
	public static float[] getFloatData(DynamicLayer<?> layer, String name, Instant t, Map<String, Float> map, EnteteRaster entete){
		
		float[] datas = new float[entete.width()*entete.height()];
		Arrays.fill(datas,  -1);
		
		int indrp;
		int xdelta, ydelta, xrp, yrp;
		DynamicFeature f;
		Iterator<DynamicFeature> ite = layer.deepIterator();
		while(ite.hasNext()){
			f = ite.next();
			RasterPolygon rp = (RasterPolygon) f.getRepresentation("raster").getGeometry(t);
			
			String attValue = f.getAttribute(name).getValue(t).toString();
			
			if(map.containsKey(attValue)) {
			
				float value = map.get(attValue);
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
		}
		
		return datas;
	}
	
}
