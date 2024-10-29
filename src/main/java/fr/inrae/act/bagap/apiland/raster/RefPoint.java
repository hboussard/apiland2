package fr.inrae.act.bagap.apiland.raster;

public class RefPoint implements Comparable<RefPoint>{

	private double X;
	
	private double Y;
	
	public RefPoint(double X, double Y){
		this.X = X;
		this.Y = Y;
	}
	
	@Override
	public String toString(){
		return "("+X+","+Y+")";
	}

	public double getX(){
		return X;
	}
	
	public double getY(){
		return Y;
	}
	
	@Override
	public boolean equals(Object p){
		if(p instanceof RefPoint){
			return (X == ((RefPoint)p).X) && (Y == ((RefPoint)p).Y);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (int) X * 152 /*+ Y *7*/ + 1;
	}
	
	@Override
	public int compareTo(RefPoint p) {
		if(this.Y > p.Y){
			return -1;
		}else if(this.Y < p.Y){
			return 1;
		}else{
			if(this.X < p.X){
				return -1;
			}else if(this.X > p.X){
				return 1;
			}else{
				return 0;
			}
		}
	}

	
}
