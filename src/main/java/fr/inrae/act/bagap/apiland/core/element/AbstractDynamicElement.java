package fr.inrae.act.bagap.apiland.core.element;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObject;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObserver;
import fr.inrae.act.bagap.apiland.core.composition.Attribute;
import fr.inrae.act.bagap.apiland.core.composition.Composition;
import fr.inrae.act.bagap.apiland.core.element.type.DynamicElementType;
import fr.inrae.act.bagap.apiland.core.structure.Representation;
import fr.inrae.act.bagap.apiland.core.structure.Structure;
import fr.inrae.act.bagap.apiland.core.time.Instant;
import fr.inrae.act.bagap.apiland.core.time.TimeException;

public abstract class AbstractDynamicElement implements DynamicElement{

	/**
	 * version number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the identifying
	 */
	private String id;

	/**
	 * composition
	 */
	private Composition composition;
	
	/**
	 * structure
	 */
	private Structure structure;
	
	/**
	 * the type
	 */
	private DynamicElementType type;
	
	/**
	 * the aggregate layer if exists
	 */
	private DynamicLayer<?> layer;
	
	/**
	 * gestion des changements
	 */
	private ChangeableObject changeable;
	
	/**
	 * constructor
	 * @param type : the element type
	 */
	public AbstractDynamicElement(DynamicElementType type){
		setType(type);
		changeable = new ChangeableObject();
		id = IdManager.get().getId();
	}
	
	/*
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof DynamicElement){
			return ((DynamicElement) other).getId().equalsIgnoreCase(id);
		}
		return false;
	}
	*/
	
	@Override
	public int compareTo(DynamicElement e){
		return this.id.compareTo(e.getId());
	}
	
	@Override
	public AbstractDynamicElement clone(){
		try{
			AbstractDynamicElement clone = (AbstractDynamicElement)super.clone();
			if(this.composition != null){
				clone.composition = this.composition.clone();
			}
			if(this.structure != null){
				clone.structure = this.structure.clone();
			}
			clone.type = this.type.clone();
			return clone;
		}catch(CloneNotSupportedException ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getInheritedId(String idName){
		if(type.getIdName().equalsIgnoreCase(idName)){
			return id;
		}else if(getLayer() != null){
			return getLayer().getInheritedId(idName);
		}
		return "";
		//throw new IllegalArgumentException("id '"+idName+"' does not exist");
	}
	
	public Attribute<?> getInheritedAttribute(String attName){
		if(getType().hasAttributeType(attName)){
			return getAttribute(attName);
		}else if(getLayer() != null){
			return getLayer().getInheritedAttribute(attName);
		}
		throw new IllegalArgumentException("attribute '"+attName+"' does not exist");
	}

	@Override
	public void setLayer(DynamicLayer<?> layer) {
		this.layer = layer;
	}
	
	@Override
	public DynamicLayer<?> getLayer() {
		return layer;
	}

	public void setType(DynamicElementType type){
		this.type = type;
	}
	
	@Override
	public DynamicElementType getType() {
		return type;
	}

	@Override
	public void update(Instant t, Changeable c, Object o) {
		notifyObservers(t,this,o);
	}
	
	@Override
	public void addObserver(ChangeableObserver o) {
		changeable.addObserver(o);
		composition.addObserver(o);
		structure.addObserver(o);
	}

	@Override
	public void removeObserver(ChangeableObserver o) {
		changeable.removeObserver(o);
		composition.removeObserver(o);
		structure.removeObserver(o);
	}

	@Override
	public void notifyObservers(Instant t, Changeable c, Object o) {
		changeable.notifyObservers(t, c, o);
	}

	@Override
	public void kill(Instant t) throws TimeException{
		composition.kill(t);
		structure.kill(t);
	}

	@Override
	public boolean hasAttribute(String name) {
		return composition.hasAttribute(name);
	}
	
	@Override
	public Attribute<?> getAttribute(String name) {
		if(composition.hasAttribute(name)){
			return composition.getAttribute(name);
		}if(layer != null){
			return layer.getAttribute(name);
		}
		throw new IllegalArgumentException("attribute '"+name+"' does not exist");
	}

	@Override
	public Composition getComposition() {
		return composition;
	}
	
	@Override
	public Structure getStructure(){
		return structure;
	}

	@Override
	public boolean hasRepresentation(String name) {
		return structure.hasRepresentation(name);
	}

	@Override
	public void setComposition(Composition composition) {
		this.composition = composition;
		this.composition.addObserver(this);
	}
	
	@Override
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	
	@Override
	public Representation<?> getDefaultRepresentation() {
		return structure.getDefaultRepresentation();
	}

	@Override
	public Representation<?> getRepresentation(String name) {
		return structure.getRepresentation(name);
	}
	
	@Override
	public int count(Instant t, DynamicElementType... types) {
		for(DynamicElementType type : types){
			if(getType().equals(type)){
				if(isActive(t)){
					return 1;
				}else{
					return 0;
				}
			}
		}
		return 0;
	}
	
	@Override
	public Instant getLastChange(){
		if(getComposition() != null){
			return getComposition().getLastChange();
		}
		return null;
	}
	
	@Override
	public double maxX() {
		return structure.maxX();
	}

	@Override
	public double maxY() {
		return structure.maxY();
	}

	@Override
	public double minX() {
		return structure.minX();
	}

	@Override
	public double minY() {
		return structure.minY();
	}
	
	@Override
	public void delete(){
		id = null;
		if(composition != null){
			composition.delete();
			composition = null;
		}
		if(structure != null){
			structure.delete();
			structure = null;
		}
		layer = null;
		changeable.delete();
		changeable = null;
		type = null;
	}
	
}