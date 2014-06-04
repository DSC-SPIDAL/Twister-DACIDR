/**
 * @author Yang Ruan (yangruan@indiana.edu)
 */
package cgl.imr.samples.dacidr.inter.type;

public class Point implements Comparable<Point>{
	private Vec3D position;
	int id;
	int group = 100;
	String label;
	
	public Point(int p_id, Vec3D p_position){
		position = new Vec3D(p_position.getX(), p_position.getY(), p_position.getZ());
		id = p_id;
	}
	
	public Point(int p_id, Vec3D p_position, int p_group){
		position = new Vec3D(p_position.getX(), p_position.getY(), p_position.getZ());
		id = p_id;
		group = p_group;
	}
	
	public Point(){
		position = new Vec3D();
	}

	public String getLabel(){
		return label;
	}
	public void setLabel(String label){
		this.label = label;
	}
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public void reset(){
		id = 0;
		position = null;
	}
	public Vec3D getPosition() {
		return position;
	}

	public void setPosition(Vec3D position) {
		this.position = position;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Point point) {
		if(this.id >= point.id) return 1;
		else
			return -1;
	}
	
}
