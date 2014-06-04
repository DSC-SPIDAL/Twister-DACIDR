package cgl.imr.samples.dacidr.inter.type;

import edu.indiana.salsahpc.Sequence;

public class SequencePoint {
	Sequence sequence;
	Point point;
	public SequencePoint(Sequence sequence, Point point) {
		super();
		this.sequence = sequence;
		this.point = point;
	}
	public Sequence getSequence() {
		return sequence;
	}
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	
}
