package egl.math;

/**
 * A SRT Transformation Decomposed Into Its Individual Parts
 * <br>Single Precision</br>
 * @author Cristian
 *
 */
public class Transform {
	/**
	 * Scale
	 */
	public final Vector3 scale;
	/**
	 * Quaternion Rotation
	 */
	public final Quat rotation;
	/**
	 * Translation
	 */
	public final Vector3 translation;

	/**
	 * Property Constructor
	 * @param s [{@link Vector3 ARR}] Scale
	 * @param r Rotation
	 * @param t [{@link Vector3 OFF}] Translation
	 */
	public Transform(Vector3 s, Quat r, Vector3 t) {
		scale = new Vector3(s);
		rotation = new Quat(r);
		translation = new Vector3(t);
	}
	/**
	 * Decomposition Constructor
	 * @param m Affine Transformation
	 */
	public Transform(Matrix4 m) {
		scale = new Vector3();
		translation = new Vector3();
		rotation = new Quat();
		set(m);
	}
	/**
	 * Copy Constructor
	 * @param t Transformation
	 */
	public Transform(Transform t) {
		this(t.scale, t.rotation, t.translation);
	}
	/**
	 * Identity Constructor
	 */
	public Transform() {
		scale = new Vector3(1);
		rotation = new Quat();
		translation = new Vector3();
	}

	/**
	 * Property Setter
	 * @param s [{@link Vector3 ARR}] Scale
	 * @param r Rotation
	 * @param t [{@link Vector3 OFF}] Translation
	 * @return This
	 */
	public Transform set(Vector3 s, Quat r, Vector3 t) {
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
	public Transform set(Matrix4 m) {
		m.getTrans(translation);
		Vector3 x = m.getX();
		Vector3 y = m.getY();
		Vector3 z = m.getZ();
		scale.set(x.len(), y.len(), z.len());
		x.mul(1 / scale.x);
		y.mul(1 / scale.y);
		z.mul(1 / scale.z);
		rotation.set(new Matrix3(x, y, z));
		return this;
	}
	/**
	 * Copy Transform Into This
	 * @param t Transform
	 * @return This
	 */
	public Transform set(Transform t) {
		scale.set(t.scale);
		rotation.set(t.rotation);
		translation.set(t.translation);
		return this;
	}
	/**
	 * Set This Transform To The Identity Transform
	 * @return This
	 */
	public Transform setIdentity() {
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
	public Matrix4 toMatrix(Matrix4 m) {
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
		Matrix4 m = 
				Matrix4.createTranslation(3, 3, 3).mul(
				Quat.createRotationX(Util.PIf / 2f).toRotationMatrix(new Matrix4()).mul(
				Matrix4.createScale(3, 3, 3)
				));
		// Hooray For Floating-Point Errors
		System.out.println(m);
		Transform t = new Transform(m);
		System.out.println(t.toMatrix(new Matrix4()));
	}
}
