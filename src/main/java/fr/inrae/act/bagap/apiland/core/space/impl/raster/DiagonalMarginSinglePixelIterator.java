package fr.inrae.act.bagap.apiland.core.space.impl.raster;

import java.util.Iterator;

public class DiagonalMarginSinglePixelIterator implements Iterator<Pixel>{
	
	private Pixel pixel;
	
	private int pos = 0;
	
	public DiagonalMarginSinglePixelIterator(Pixel pixel){
		this.pixel = pixel;
	}

	public boolean hasNext() {
		if(pos < 4){
			return true;
		}
		return false;
	}

	public Pixel next() {
		if(hasNext()){
			switch(pos){
			case 0 : pos = 1;
				return new Pixel(pixel.x()+1,pixel.y()-1);
				//return PixelManager.get(pixel.x()+1,pixel.y()-1);
			case 1 : pos = 2;
				return new Pixel(pixel.x()+1,pixel.y()+1);
				//return PixelManager.get(pixel.x()+1,pixel.y()+1);
			case 2 : pos = 3;
				return new Pixel(pixel.x()-1,pixel.y()+1);
				//return PixelManager.get(pixel.x()-1,pixel.y()+1);
			case 3 : pos = 4;
				return new Pixel(pixel.x()-1,pixel.y()-1);
				//return PixelManager.get(pixel.x()-1,pixel.y()-1);
			}
		}
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
