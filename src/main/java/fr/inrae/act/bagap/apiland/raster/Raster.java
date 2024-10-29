package fr.inrae.act.bagap.apiland.raster;

public class Raster {

	private static int noDataValue = -1;
	
	public static void setNoDataValue(int ndtv){
		noDataValue = ndtv;
	}
	
	public static int getNoDataValue(){
		return noDataValue;
	}
	
}
