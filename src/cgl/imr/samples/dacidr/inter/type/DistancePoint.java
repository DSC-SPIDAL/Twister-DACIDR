package cgl.imr.samples.dacidr.inter.type;


public class DistancePoint implements Comparable<DistancePoint> {
	private Point point;
	private double distance;
	private int alignedLength;
	private int identicalPairs;
	public DistancePoint(){
		
	}
	
	public DistancePoint(Point point, double distance) {
		super();
		this.point = point;
		this.distance = distance;
	}
	
	public int getAlignedLength() {
		return alignedLength;
	}
	public void setAlignedLength(int alignedLength) {
		this.alignedLength = alignedLength;
	}
	public int getIdenticalPairs() {
		return identicalPairs;
	}
	public void setIdenticalPairs(int identicalPairs) {
		this.identicalPairs = identicalPairs;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	@Override
	public int compareTo(DistancePoint p) {
		// TODO Auto-generated method stub
		if(this.distance >= p.distance)
			return 1;
		else
			return -1;
	}
	
}
