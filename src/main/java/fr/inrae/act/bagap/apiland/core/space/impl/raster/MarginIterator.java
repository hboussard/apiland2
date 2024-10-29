package fr.inrae.act.bagap.apiland.core.space.impl.raster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MarginIterator implements Iterator<Pixel>{

	private Iterator<Pixel> i;
	
	public MarginIterator(Raster raster){
		Set<Pixel> bounds = new HashSet<Pixel>();
		Pixel p;
		Pixel p2;
		Iterator<Pixel> ite;
		Iterator<Pixel> iterator = raster.getBoundaries();
		while(iterator.hasNext()){
			p = iterator.next();
			ite = p.getMargins();
			while(ite.hasNext()){
				p2 = ite.next();
				if(!raster.containsPixel(p2)){
					bounds.add(p2);
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
