package fr.inrae.act.bagap.apiland.util;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.jumpmind.symmetric.csv.CsvReader;

import fr.inrae.act.bagap.apiland.raster.EnteteRaster;
import fr.inrae.act.bagap.apiland.raster.Pixel;
import fr.inrae.act.bagap.apiland.raster.PixelManager;
import fr.inrae.act.bagap.apiland.raster.RefPoint;
import fr.inrae.act.bagap.apiland.raster.RefPointWithID;

public class CoordinateManager {
	
	public static double getProjectedX(EnteteRaster entete, int x){
		return x * entete.cellsize() + entete.minx() + entete.cellsize()/2.0;
	}
	
	public static double getProjectedX(double minX, double cellSize, int x){
		return x * cellSize + minX + cellSize/2.0;
	}
	
	public static double getProjectedY(EnteteRaster entete, int y){
		return entete.cellsize() * (entete.height() - y) + entete.miny() - entete.cellsize()/2.0;
	}
	
	public static double getProjectedY(double minY, double cellSize, int height, int y){
		return cellSize * (height - y) + minY - cellSize/2.0;
	}
	
	public static int getLocalX(EnteteRaster entete, double x){
		return (int) ((x - entete.minx()) / entete.cellsize());
	}
	
	
	public static int getLocalY(EnteteRaster entete, double y){
		
		//int value = (int) ((entete.height()-1) - ((y - entete.miny()) / entete.cellsize()));
		//return (int) ((entete.height()-1) - ((y - entete.miny()) / entete.cellsize()));
		
		int ly = (int) ((entete.maxy()-y) / entete.cellsize());
		/*
		if(ly == entete.height()) {
			System.out.println(entete+" "+y+" "+ly);
			//ly--;
		}*/
		return ly;
	}
	
	/**
	 * initialize a set of pixels using a text file of points
	 * according to a given matrix
	 * @param m a matrix
	 * @param f a text file of points
	 * @return a set of pixels
	 */
	public static Set<Pixel> initWithPoints(String f, EnteteRaster entete) {
		Set<Pixel> pixels = new TreeSet<Pixel>();
		try{
			CsvReader cr = new CsvReader(f);
			cr.setDelimiter(';');
			cr.readHeaders();
			int xIndex = -1, yIndex = -1, idIndex = -1;
			boolean hasId = false;
			String header;
			for(int i=0; i<cr.getHeaderCount(); i++) {
				header = cr.getHeader(i); 
				if(header.equalsIgnoreCase("x")) {
					xIndex = i;
				}
				if(header.equalsIgnoreCase("y")) {
					yIndex = i;
				}
				if(header.equalsIgnoreCase("id")) {
					idIndex = i;
					hasId = true;
				}
			}
			if(!hasId) {
				for(int i=0; i<cr.getHeaderCount(); i++) {
					header = cr.getHeader(i); 
					if(header.toLowerCase().contains("id")) {
						idIndex = i;
						hasId = true;
						break;
					}
				}
			}
			
			if(xIndex == -1 || yIndex == -1) {
				System.err.println("'X' or 'Y' column missing from "+f);
			}
			
			double X, Y;
			int x, y;
			String id;
			int ind = 1;
			while(cr.readRecord()){
				
				X = Double.parseDouble(cr.get(xIndex));
				x = getLocalX(entete, X);
				
				Y = Double.parseDouble(cr.get(yIndex));
				y = getLocalY(entete, Y);
				
				if(hasId) {
					id = cr.get(idIndex);
					pixels.add(PixelManager.get(x, y, id, X, Y));
				}else {
					pixels.add(PixelManager.get(x, y, (ind++)+"", X, Y));
				}
			}
			
			//System.out.println(points);
			
			cr.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return pixels;	
	}
	
	public static Set<RefPoint> initWithPoints(String f) {
		Set<RefPoint> points = new TreeSet<RefPoint>();
		try{
			CsvReader cr = new CsvReader(f);
			cr.setDelimiter(';');
			cr.readHeaders();
			
			double X, Y;
			String id;
			while(cr.readRecord()){
				
				if(cr.get("X") != ""){
					X = Double.parseDouble(cr.get("X"));
				}else{
					X = Double.parseDouble(cr.get("x"));
				}
				
				if(cr.get("Y") != ""){
					Y = Double.parseDouble(cr.get("Y"));
				}else{
					Y = Double.parseDouble(cr.get("y"));
				}
				
				
				if(!(id = cr.get("id")).equals("")){
					points.add(new RefPointWithID(id, X, Y));
				}else if(!(id = cr.get("ID")).equals("")){
					points.add(new RefPointWithID(id, X, Y));
				}else if(!(id = cr.get("Id")).equals("")){
					points.add(new RefPointWithID(id, X, Y));
				}else if(!(id = cr.get("iD")).equals("")){
					points.add(new RefPointWithID(id, X, Y));
				}else{
					points.add(new RefPoint(X, Y));
				}
			}
			
			//System.out.println(points);
			
			cr.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return points;	
	}
	
	public static Set<Pixel> initWithPixels(String f) {
		Set<Pixel> pixels = new TreeSet<Pixel>();
		try{
			CsvReader cr = new CsvReader(f);
			cr.setDelimiter(';');
			cr.readHeaders();
			
			int x;
			int y;
			
			while(cr.readRecord()){
				x = Integer.parseInt(cr.get("X"));
				y = Integer.parseInt(cr.get("Y"));
//				System.out.println(x+" "+y);
				pixels.add(PixelManager.get(x, y));
			}
			
			//System.out.println(points);
			
			cr.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return pixels;	
	}
	
}
