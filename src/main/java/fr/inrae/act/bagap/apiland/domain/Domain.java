package fr.inrae.act.bagap.apiland.domain;

public interface Domain<D, E> {

	int size();
	
	boolean accept(E e);
	
	Domain<D, E> inverse();
	
	D minimum();
	
	D maximum();

}
