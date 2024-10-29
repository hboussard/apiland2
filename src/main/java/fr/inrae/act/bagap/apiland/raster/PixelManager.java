package fr.inrae.act.bagap.apiland.raster;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PixelManager{

	private static Map<Integer,Set<Pixel>> pixels = new TreeMap<Integer,Set<Pixel>>();
	
	private static final int modulo = 500;
	
	private static final int max = 100;
	
	private static int size;
	
	private static void clear(){
		pixels.clear();
		size = 0;
	}
	
	public static Pixel get(int x, int y){
		int n = (x*y)%modulo;
		
		if(size++ >= max){
			clear();
		}else if(pixels.containsKey(n)){
			for(Pixel p : pixels.get(n)){
				if(p.equals(x, y)){
					return p;
				}
			}
			Pixel p = new Pixel(x,y);
			pixels.get(n).add(p);
			return p;
		}
		
		pixels.put(n, new TreeSet<Pixel>());
		Pixel p = new Pixel(x,y);
		pixels.get(n).add(p);
		return p;
	}
	
	public static Pixel get(int x, int y, String id, double X, double Y){
		return new PixelWithID(x, y, id, X, Y);
	}
	
}
