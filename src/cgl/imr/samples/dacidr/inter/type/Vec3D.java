/**
 * @author Yang Ruan (yangruan@indiana.edu)
 */
package cgl.imr.samples.dacidr.inter.type;

public class Vec3D {
	private double x, y, z;

	public Vec3D(double p_x, double p_y, double p_z){
		x = p_x;
		y = p_y;
		z = p_z;
	}
	
	public Vec3D(Vec3D v){
		x = v.getX();
		y = v.getY();
		z = v.getZ();
	}
	
	public Vec3D(){
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
}
