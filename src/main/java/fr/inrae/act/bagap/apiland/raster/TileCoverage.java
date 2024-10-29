package fr.inrae.act.bagap.apiland.raster;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;
//import fr.inrae.act.bagap.apiland.raster.Raster;

public class TileCoverage extends Coverage {
	
	private int ncols, nrows, tileWidth, tileHeight;
	
	private Coverage[] grid; 
	
	public TileCoverage(Set<Coverage> tiles, EnteteRaster entete) {
		super(entete);
		initGrid(tiles);
	}
	
	public Coverage getCoverage(int i, int j){
		if(i<0 || i>=tileWidth || j<0 || j>= tileHeight){
			return null;
		}
		return grid[j*ncols + i];
	}
	
	public int ncols(){
		return ncols;
	}
	
	public int nrows(){
		return nrows;
	}
	
	public double tileSize(){
		return tileWidth * getEntete().cellsize();
	}
	
	public int tileWidth(){
		return tileWidth;
	}
	
	private void initGrid(Set<Coverage> tiles){
		
		Coverage cov = tiles.iterator().next();
		tileWidth = cov.width();
		tileHeight = cov.height();
		//System.out.println("tile : "+tileWidth+" "+tileHeight);
		
		ncols = new Double(Math.round(getEntete().maxx() - getEntete().minx()) / (tileWidth * (int) getEntete().cellsize())).intValue();
		if(Math.round(getEntete().maxx() - getEntete().minx()) % (tileWidth * (int) getEntete().cellsize()) != 0){
			ncols++;
		}
		nrows = new Double(Math.round(getEntete().maxy() - getEntete().miny()) / (tileHeight * (int) getEntete().cellsize())).intValue();
		if(Math.round(getEntete().maxy() - getEntete().miny()) % (tileHeight * (int) getEntete().cellsize()) != 0){
			nrows++;
		}
		
		//System.out.println("tableau : "+ncols+" "+nrows);
		grid = new Coverage[ncols*nrows];
		int posX, posY;
		for(Coverage tile : tiles){
			posX = new Double(Math.round(tile.getEntete().minx() - getEntete().minx()) / (tileWidth * (int) getEntete().cellsize())).intValue();
			posY = new Double(Math.round(getEntete().maxy() - tile.getEntete().maxy()) / (tileHeight * (int) getEntete().cellsize())).intValue();
			
			//System.out.println(posX+" "+posY);
			grid[posY*ncols + posX] = tile;
		}
		
	}

	@Override
	public float[] getData() {
		float[] datas = new float[width()*height()];
		float[] d;
		Coverage tile;
		for(int j=0; j<nrows; j++){
			for(int i=0; i<ncols; i++){
				tile = grid[j*ncols + i];
				if(tile != null){
					d = tile.getData();
					for(int y=0; y<tileHeight; y++){
						for(int x=0; x<tileWidth; x++){
							datas[((j*tileHeight)+y)*width() + ((i*tileWidth)+x)] = d[y*tileWidth + x];
						}
					}
				}else{
					for(int y=0; y<tileHeight; y++){
						for(int x=0; x<tileWidth; x++){
							datas[((j*tileHeight)+y)*width() + ((i*tileWidth)+x)] = Raster.getNoDataValue();
						}
					}
				}
			}	
		}
		
		return datas;
	}

	@Override
	public float[] getData(Rectangle roi) {
		
		//System.out.println("rectangle de roi : "+roi);
		
		EnteteRaster roiEntete = EnteteRaster.getEntete(getEntete(), roi);
		
		//System.out.println("entete de roi : "+roiEntete);
		
		Envelope roiEnv = roiEntete.getEnvelope();
		
		//System.out.println("enveloppe de roi : "+roiEnv);
		
		float[] data = new float[roiEntete.width()*roiEntete.height()];
		Arrays.fill(data, Raster.getNoDataValue());
		
		Envelope tileEnv, localEnv;
		Coverage tile;
		Rectangle localRoi, tileRoi;
		float[] localData;
		for(int j=0; j<nrows; j++){
			for(int i=0; i<ncols; i++){
				tile = grid[j*ncols + i];
				if(tile != null){
					tileEnv = tile.getEntete().getEnvelope();
					
					if(roiEnv.intersects(tileEnv)){
						
						localEnv = roiEnv.intersection(tileEnv);
						
						//System.out.println("enveloppe de tile : "+tileEnv);
						//System.out.println("enveloppe locale : "+localEnv);
						
						if(!(localEnv.getMinX() == localEnv.getMaxX() || localEnv.getMinY() == localEnv.getMaxY())){
							localRoi = EnteteRaster.getROI(tile.getEntete(), localEnv);
							
							//System.out.println("rectangle local : "+localRoi);
							
							if(localRoi.width != 0 && localRoi.height != 0){
								localData = tile.getData(localRoi);
								tileRoi = EnteteRaster.getROI(roiEntete, localEnv);
								//System.out.println("rectangle de tile : "+tileRoi);
								
								for(int y=0; y<localRoi.height; y++){
									for(int x=0; x<localRoi.width; x++){
								
										//System.out.println((y*localRoi.width + x));
										//System.out.println(localData[y*localRoi.width + x]);
										//System.out.println(((y+localRoi.y)*roiEntete.width() + x+localRoi.x));
										//System.out.println(x+" "+y+" "+localRoi.x+" "+localRoi.y+" "+localRoi.width);
										//System.out.println(data[(y+localRoi.y)*roiEntete.width() + x+localRoi.x]);
										data[(y+tileRoi.y)*roiEntete.width() + x+tileRoi.x] = localData[y*localRoi.width + x];
											
									}
								}
								
								
								//tileRoi = EnteteRaster.getROI(roiEntete, localEnv);
								
								//System.out.println("rectangle de tile : "+tileRoi);
								
								//for(int y=0; y<tileRoi.height; y++){
								//	for(int x=0; x<tileRoi.width; x++){
										
										//System.out.println(getEntete()+" "+roiEntete+" "+localRoi+" "+tileRoi);
										//System.out.println(localData[y*tileRoi.width + x]);
										//System.out.println(data[(y+tileRoi.y)*roiEntete.width() + x+tileRoi.x]);
										
								//		data[(y+tileRoi.y)*roiEntete.width() + x+tileRoi.x] = localData[y*tileRoi.width + x];
										//data[(y+localRoi.y)*roiEntete.width() + x+localRoi.x] = localData[y*localRoi.width + x];
								//	}
								//}
							}
							
						}
					}
					tile.dispose();
				}
			}
		}
		
		return data;
	}

	@Override
	public void dispose() {
		if(grid != null){
			for(Coverage tile : grid){
				if(tile != null){
					tile.dispose();
				}
			}
		}
		grid = null;
	}

}
