package egl.math;

/**
 * A SRT Transformation Decomposed Into Its Individual Parts
 * <br>Double Precision</br>
 * @author Cristian
 *
 */
public class Transformd {
	/**
	 * Scale
	 */
	public final Vector3d scale;
	/**
	 * Quaternion Rotation
	 */
	public final Quatd rotation;
	/**
	 * Translation
	 */
	public final Vector3d translation;

	/**
	 * Property Constructor
	 * @param s [{@link Vector3d ARR}] Scale
	 * @param r Rotation
	 * @param t [{@link Vector3d OFF}] Translation
	 */
	public Transformd(Vector3d s, Quatd r, Vector3d t) {
		scale = new Vector3d(s);
		rotation = new Quatd(r);
		translation = new Vector3d(t);
	}
	/**
	 * Decomposition Constructor
	 * @param m Affine Transformation
	 */
	public Transformd(Matrix4d m) {
		scale = new Vector3d();
		translation = new Vector3d();
		rotation = new Quatd();
		set(m);
	}
	/**
	 * Copy Constructor
	 * @param t Transformation
	 */
	public Transformd(Transformd t) {
		this(t.scale, t.rotation, t.translation);
	}
	/**
	 * Identity Constructor
	 */
	public Transformd() {
		scale = new Vector3d(1);
		rotation = new Quatd();
		translation = new Vector3d();
	}

	/**
	 * Property Setter
	 * @param s [{@link Vector3d ARR}] Scale
	 * @param r Rotation
	 * @param t [{@link Vector3d OFF}] Translation
	 * @return This
	 */
	public Transformd set(Vector3d s, Quatd r, Vector3d t) {
		scale.set(s);
		rotation.set(r);
		translation.set(t);
		return this;
	}
	/**
	 * Decomposition Setter
	 * @param m Matrix
	 * @return This
	 */
	public Transformd set(Matrix4d m) {
		m.getTrans(translation);
		Vector3d x = m.getX();
		Vector3d y = m.getY();
		Vector3d z = m.getZ();
		scale.set(x.len(), y.len(), z.len());
		x.mul(1 / scale.x);
		y.mul(1 / scale.y);
		z.mul(1 / scale.z);
		rotation.set(new Matrix3d(x, y, z));
		return this;
	}
	/**
	 * Copy Transform Into This
	 * @param t Transform
	 * @return This
	 */
	public Transformd set(Transformd t) {
		scale.set(t.scale);
		rotation.set(t.rotation);
		translation.set(t.translation);
		return this;
	}
	/**
	 * Set This Transform To The Identity Transform
	 * @return This
	 */
	public Transformd setIdentity() {
		scale.set(1);
		rotation.setIdentity();
		translation.setZero();
		return this;
	}

	/**
	 * Create A SRT Affine Matrix From This Transformation
	 * @param m Non-null Output Matrix
	 * @return Matrix
	 */
	public Matrix4d toMatrix(Matrix4d m) {
		rotation.toRotationMatrix(m);
		m.m[0] *= scale.x; m.m[1] *= scale.y; m.m[2]  *= scale.z;
		m.m[4] *= scale.x; m.m[5] *= scale.y; m.m[6]  *= scale.z;
		m.m[8] *= scale.x; m.m[9] *= scale.y; m.m[10] *= scale.z;
		m.m[3] = translation.x;
		m.m[7] = translation.y;
		m.m[11] = translation.z;
		return m;
	}

	/**
	 * Test For Decomposition And Recomposition
	 * @param args Like Really Bro... Really?
	 */
	public static void main(String[] args) {
		Matrix4d m = 
				Matrix4d.createTranslation(3, 3, 3).mul(
				Quatd.createRotationX(Util.PIf / 2f).toRotationMatrix(new Matrix4d()).mul(
				Matrix4d.createScale(3, 3, 3)
				));
		// Hooray For Floating-Point Errors
		System.out.println(m);
		Transformd t = new Transformd(m);
		System.out.println(t.toMatrix(new Matrix4d()));
	}
}
