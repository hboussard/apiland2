package fr.inrae.act.bagap.apiland.core.space.impl.raster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BoundaryIterator implements Iterator<Pixel>{

	private Iterator<Pixel> i;
	
	public BoundaryIterator(Raster raster){
		Set<Pixel> bounds = new HashSet<Pixel>();
		Pixel p;
		Iterator<Pixel> ite;
		Iterator<Pixel> iterator = raster.iterator();
		while(iterator.hasNext()){
			p = iterator.next();
			ite = p.getMargins();
			while(ite.hasNext()){
				if(!raster.containsPixel(ite.next())){
					bounds.add(p);
					break;
				}
			}
		}
		i = bounds.iterator();
	}
	
	public boolean hasNext(){
		return i.hasNext();
	}
	
	public Pixel next(){
		return i.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
