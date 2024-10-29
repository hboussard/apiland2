package fr.inrae.act.bagap.apiland.core.element.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.inrae.act.bagap.apiland.core.change.Changeable;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObject;
import fr.inrae.act.bagap.apiland.core.change.ChangeableObserver;
import fr.inrae.act.bagap.apiland.core.composition.AttributeType;
import fr.inrae.act.bagap.apiland.core.element.DynamicElement;
import fr.inrae.act.bagap.apiland.core.structure.RepresentationType;
import fr.inrae.act.bagap.apiland.core.time.Instant;

public abstract class DynamicElementType implements Cloneable, Changeable, Serializable{

	private static final long serialVersionUID = 1L;

	private Class<? extends DynamicElement> binding;
	
	protected List<AttributeType> attributeTypes;
	
	protected List<RepresentationType> representationTypes;
	
	protected DynamicLayerType layerType;
	
	protected String idName;
	
	protected Set<Object> conditions;
	
	private ChangeableObject changeable;
	
	/*
	public DynamicElementType(String idName){
		this.idName = idName;
		attributeTypes = new ArrayList<AttributeType>();
		representationTypes = new ArrayList<RepresentationType>();
		conditions = new HashSet<Object>();
	}
	*/
	
	public DynamicElementType(String idName, Class<? extends DynamicElement> binding){
		changeable = new ChangeableObject();
		this.idName = idName;
		this.binding = binding;
		attributeTypes = new ArrayList<AttributeType>();
		representationTypes = new ArrayList<RepresentationType>();
		conditions = new HashSet<Object>();
	}
	
	public DynamicElementType(String idName,
			List<AttributeType> attributeTypes, 
			List<RepresentationType> representationTypes){
		changeable = new ChangeableObject();
		this.idName = idName;
		this.attributeTypes = attributeTypes;
		this.representationTypes = representationTypes;
		conditions = new HashSet<Object>();
	}
	
	public DynamicElementType(String idName,
			Class<? extends DynamicElement> binding,
			List<AttributeType> attributeTypes, 
			List<RepresentationType> representationTypes){
		changeable = new ChangeableObject();
		this.idName = idName;
		this.binding = binding;
		this.attributeTypes = attributeTypes;
		this.representationTypes = representationTypes;
		conditions = new HashSet<Object>();
	}
	
	@Override
	public DynamicElementType clone(){
		try {
			DynamicElementType clone = (DynamicElementType)super.clone();
			clone.attributeTypes = new ArrayList<AttributeType>();
			for(AttributeType t : attributeTypes){
				clone.attributeTypes.add(t);
			}
			clone.representationTypes = new ArrayList<RepresentationType>();
			for(RepresentationType t : representationTypes){
				clone.representationTypes.add(t);
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString(){
		int level = getLevelTopDown();
		StringBuffer sb = new StringBuffer();
		for(int l=0; l<level; l++){
			sb.append("	");
		}
		sb.append(binding+"\n");
		for(int l=0; l<level; l++){
			sb.append("	");
		}
		sb.append("id : "+idName+" \n");
		for(AttributeType type : getAttributeTypes()){
			for(int l=0; l<level; l++){
				sb.append("	");
			}
			sb.append(type.toString());
		}
		for(RepresentationType type : getRepresentationTypes()){
			for(int l=0; l<level; l++){
				sb.append("	");
			}
			sb.append(type.toString());
		}
		return sb.toString();
	}
	
	public void display(){
		System.out.println(this);
	}
	
	public List<AttributeType> getAttributeTypes() {
		return attributeTypes;
	}
	
	public Set<AttributeType> getInheritedAttributeTypes(DynamicElementType type, Set<AttributeType> set){
		set.addAll(getAttributeTypes());
		if(type.equals(this)){
			return set;
		}else if(layerType != null){
			return layerType.getInheritedAttributeTypes(type, set);
		}else{
			return set;
		}
	}
	
	public List<RepresentationType> getRepresentationTypes() {
		return representationTypes;
	}
	
	public boolean hasRepresentationType(String name){
		for(RepresentationType type : getRepresentationTypes()){
			if(type.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAttributeType(String name){
		for(AttributeType type : getAttributeTypes()){
			if(type.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public RepresentationType getRepresentationType(String name){
		for(RepresentationType type : getRepresentationTypes()){
			if(type.getName().equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}
	
	public AttributeType getAttributeType(String name){
		for(AttributeType type : getAttributeTypes()){
			if(type.getName().equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}

	public int attributesCount(){
		return getAttributeTypes().size();
	}
	
	public int representationsCount(){
		return getRepresentationTypes().size();
	}
	
	public void addAttributeType(AttributeType type){
		if(!hasAttributeType(type.getName())){
			attributeTypes.add(type);	
		}
	}
	
	public void setAttributeTypes(List<AttributeType> attributeTypes){
		this.attributeTypes = attributeTypes;
	}
	
	public void addRepresentationType(RepresentationType type){
		if(!hasRepresentationType(type.getName())){
			representationTypes.add(type);
		}
	}
	
	public void setRepresentationTypes(List<RepresentationType> representationTypes){
		this.representationTypes = representationTypes;
	}
	
	public void removeAttributeType(String name){
		for(AttributeType type : attributeTypes){
			if(type.getName().equalsIgnoreCase(name)){
				attributeTypes.remove(type);
				notifyObservers(null,this,type);
				return;
			}
		}
		//throw new IllegalArgumentException("attribute "+name+" does not exist : can not remove it");
	}
	
	public void removeRepresentationType(String name){
		for(RepresentationType type : representationTypes){
			if(type.getName().equalsIgnoreCase(name)){
				representationTypes.remove(type);
				notifyObservers(null,this,type);
				return;
			}
		}
		throw new IllegalArgumentException("representation "+name+" does not exist : can not remove it");
	}
	
	public Class<? extends DynamicElement> getBinding(){
		return binding;
	}

	public DynamicElementType getLayerType() {
		return layerType;
	}
	
	public void setLayerType(DynamicLayerType layerType) {
		this.layerType = layerType;
	}

	public String getIdName() {
		return idName;
	}
	
	public Set<String> getInheritedIdNames(DynamicElementType type, Set<String> set) {
		set.add(idName);
		if(type.equals(this)){
			return set;
		}else if(layerType != null){
			return layerType.getInheritedIdNames(type, set);
		}else{
			return set;
		}
	}
	
	private void setIdName(String idName) {
		this.idName = idName;
	}
	
	public boolean hasIdName(String name){
		if(idName.equalsIgnoreCase(name)){
			return true;
		}
		return false;
	}
	
	public boolean containsIdName(String name){
		return hasIdName(name);
	}
	
	public boolean containsAttributeByName(String name){
		return hasAttributeType(name);
	}
	
	public abstract List<DynamicFeatureType> getFeatureTypes();
	
	public abstract int getLevelBottomUp();
	
	public int getLevelTopDown() {
		if(layerType == null){
			return 0;
		}
		return layerType.getLevelTopDown() + 1;
	}
	
	public abstract boolean hasElementType(Class<? extends DynamicElement> binding);
	
	public abstract DynamicElementType getDeepElementType();
	
	public abstract DynamicElementType getElementType(Class<? extends DynamicElement> binding);
	
	public boolean isElementType(Class<? extends DynamicElement> binding){
		return getBinding().equals(binding);
	}
	
	public boolean hasDeepCondition(Object o){
		return hasCondition(o);
	}
	
	public boolean hasCondition(Object o){
		return conditions.contains(o);
	}
	
	public void setCondition(Object o){
		conditions.add(o);
	}
	
	@Override
	public void addObserver(ChangeableObserver o) {
		changeable.addObserver(o);
	}

	@Override
	public void removeObserver(ChangeableObserver o) {
		changeable.removeObserver(o);
	}

	@Override
	public void notifyObservers(Instant t, Changeable c, Object o) {
		changeable.notifyObservers(t, c, o);
	}
	
}
