package fr.inrae.act.bagap.apiland.raster;

public class Pixel implements Comparable<Pixel>{
	
	private int x;
	
	private int y;
	
	public Pixel(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	@Override
	public boolean equals(Object p){
		if(p instanceof Pixel){
			return (x == ((Pixel)p).x) && (y == ((Pixel)p).y);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return x * 152 + y * 7 + 1;
	}
	
	public boolean equals(int x, int y){
		return (this.x == x) && (this.y == y);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int compareTo(Pixel p) {
		if(this.y < p.y){
			return -1;
		}else if(this.y > p.y){
			return 1;
		}else{
			if(this.x < p.x){
				return -1;
			}else if(this.x > p.x){
				return 1;
			}else{
				return 0;
			}
		}
	}

}
