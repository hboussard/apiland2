package fr.inrae.act.bagap.apiland.raster;

public class PixelWithID extends Pixel {

	private String id;
	
	private double X, Y;
	
	public PixelWithID(int x, int y, String id, double X, double Y){
		super(x, y);
		this.id = id;
		this.X = X;
		this.Y = Y;
	}
	
	@Override
	public boolean equals(Object p){
		if(super.equals(p)){
			if(p instanceof PixelWithID){
				return (X == ((PixelWithID)p).X) 
						&& (Y == ((PixelWithID)p).Y)
						&& id.equals(((PixelWithID)p).id);
			}	
		}
		return false;
	}
	
	public String getId(){
		return id;
	}
	
	public double getX(){
		return X;
	}
	
	public double getY(){
		return Y;
	}
	
	@Override
	public int compareTo(Pixel p) {
		int comp = super.compareTo(p);
		if(comp == 0 && p instanceof PixelWithID){
			
			if(this.Y < ((PixelWithID) p).Y){
				return 1;
			}else if(this.Y > ((PixelWithID) p).Y){
				return -1;
			}else{
				if(this.X < ((PixelWithID) p).X){
					return -1;
				}else if(this.X > ((PixelWithID) p).X){
					return 1;
				}else{
					//System.out.println(this.id+" "+this.X+" "+this.Y+" "+((PixelWithID) p).id+" "+((PixelWithID) p).X+" "+((PixelWithID) p).Y);
					//return 0;
					return this.id.compareTo(((PixelWithID) p).id);
				}
			}
		}
		return comp;
	}
	
}
