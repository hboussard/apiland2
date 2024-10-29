package fr.inrae.act.bagap.apiland.raster;

public class RefPointWithID extends RefPoint {

	private String id;
	
	public RefPointWithID(String id, double X, double Y) {
		super(X, Y);
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	@Override
	public int compareTo(RefPoint p) {
		int comp = super.compareTo(p);
		if(comp == 0 && p instanceof RefPointWithID){
			if(this.getY() < ((RefPointWithID) p).getY()){
				return 1;
			}else if(this.getY() > ((RefPointWithID) p).getY()){
				return -1;
			}else{
				if(this.getX() < ((RefPointWithID) p).getX()){
					return -1;
				}else if(this.getX() > ((RefPointWithID) p).getX()){
					return 1;
				}else{
					return 0;
				}
			}
		}
		return comp;
	}
	
}
