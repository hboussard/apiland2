package fr.inrae.act.bagap.apiland.raster;

import java.awt.Rectangle;
import java.util.Arrays;

import javax.media.jai.PlanarImage;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.image.util.ImageUtilities;

// recuperation des donnees depuis le coverage
// attention bug de la récupération des données dans le coverage2D si le Y dépasse une certaine valeur
// bizarement ce bug influence les données en X
// ce bug n'est effectif que sur les coverage issus de fichiers AsciiGrid
// pas de problème sur fichier TIF
public class FileCoverage extends Coverage {

	private GridCoverage2D coverage;
	
	public FileCoverage(GridCoverage2D coverage, EnteteRaster entete){
		super(entete);
		this.coverage = coverage;
	}
	
	public void setCoverage2D(GridCoverage2D gc2d){
		this.coverage = gc2d;
	}
	
	@Override
	public float[] getData(){
		
		float[] datas = new float[getEntete().width() * getEntete().height()];
		datas = coverage.getRenderedImage().getData().getSamples(0, 0, getEntete().width(), getEntete().height(), 0, datas);
		return datas;
	}
	
	@Override
	public float[] getData(Rectangle roi){
		
		//System.out.println(roi+" "+getEntete().width()+" "+getEntete().height());
		
		float[] datas = new float[roi.width * roi.height];
		Arrays.fill(datas, getEntete().noDataValue());
		
		if(roi.intersects(0, 0, coverage.getRenderedImage().getWidth(), coverage.getRenderedImage().getHeight())) {
			
			//datas = coverage.getRenderedImage().getData(roi).getSamples(roi.x, roi.y, roi.width, roi.height, 0, datas);	
			
			Rectangle localRoi = roi.intersection(new Rectangle(0, 0, coverage.getRenderedImage().getWidth(), coverage.getRenderedImage().getHeight()));
			float[] localDatas = new float[localRoi.width * localRoi.height];
			localDatas = coverage.getRenderedImage().getData().getSamples(localRoi.x, localRoi.y, localRoi.width, localRoi.height, 0, localDatas);
		
			int ind = 0;
			for(int j=localRoi.y-roi.y; j<(localRoi.y-roi.y+localRoi.height); j++) {
				for(int i=localRoi.x-roi.x; i<(localRoi.x-roi.x+localRoi.width); i++) {
					
					datas[j*roi.width + i] = localDatas[ind++];
				}	
			}
		}
		
		return datas;
	}
	
	@Override
	public void dispose(){
		if(coverage != null){
			PlanarImage planarImage = (PlanarImage) coverage.getRenderedImage();
			ImageUtilities.disposeImage(planarImage);
			planarImage = null;
			coverage.dispose(true);
			coverage = null;
		}
	}
	
}
