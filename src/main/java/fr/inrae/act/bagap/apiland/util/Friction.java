package fr.inrae.act.bagap.apiland.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jumpmind.symmetric.csv.CsvReader;

public class Friction {

	private Map<Float, Float> r;
	
	private float min;
	
	public Friction(String f){
		r = new HashMap<Float, Float>();
		min = Float.MAX_VALUE;
		read(f);
	}
	
	private void read(String f){
		try{
			CsvReader cr = new CsvReader(f,';');
			cr.readHeaders();
			while(cr.readRecord()){
				r.put(Float.parseFloat(cr.get("code")), Float.parseFloat(cr.get("friction")));
				min = Math.min(min, Float.parseFloat(cr.get("friction")));
			}
			cr.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public float min(){
		return min;
	}
	
	public double get(double v){
		//System.out.println(v);
		return r.get(v);
	}
	
	public Map<Float, Float> getMap(){
		return r;
	}
	
	public void display(){
		for(Entry<Float, Float> e : r.entrySet()){
			System.out.println(e.getKey()+" --> "+e.getValue());
		}
	}
	
}
