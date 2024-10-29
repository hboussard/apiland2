package fr.inrae.act.bagap.apiland.simul.model;

import java.io.Serializable;

import fr.inrae.act.bagap.apiland.core.element.DynamicElement;
import fr.inrae.act.bagap.apiland.core.time.Instant;

public interface LandscapeAction extends Serializable {
	
	LandscapeModel getLandscapeModel();
	
	boolean put(Instant t, DynamicElement e);
	
	boolean action(Instant t);
	
	void delete();
	
}
