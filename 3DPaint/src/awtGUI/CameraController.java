package awtGUI;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cs4620.common.Scene;
import cs4620.common.event.SceneTransformationEvent;
import cs4620.gl.RenderCamera;
import cs4620.gl.RenderEnvironment;
import cs4620.gl.RenderObject;
import cs4620.mesh.MeshData;
import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.surface.Triangle;
import egl.math.Matrix4;
import egl.math.Vector3;
import egl.math.Vector3i;

public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	protected final RenderEnvironment rEnv;
	
	protected boolean prevFrameButtonDown = false;
	protected int prevMouseX, prevMouseY;
	private MeshData mesh;
	private PaintTexture paintTexture;
	
	//protected boolean orbitMode = true;
	
	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
	}
	
	public void givePaintMeshInfo(MeshData meshData, PaintTexture pTexture) {
		mesh = meshData;
		paintTexture = pTexture;
	}
	
	/**
	 * Update the camera's transformation matrix in response to user input.
	 * 
	 * Pairs of keys are available to translate the camera in three direction oriented to the camera,
	 * and to rotate around three axes oriented to the camera.  Mouse input can also be used to rotate 
	 * the camera around the horizontal and vertical axes.  All effects of these controls are achieved
	 * by altering the transformation stored in the SceneCamera that is referenced by the RenderCamera
	 * this controller is associated with.
	 * 
	 * @param et  time elapsed since previous frame
	 */
	public void update(double et) {
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }

		if(Keyboard.isKeyDown(Keyboard.KEY_E)) { rotation.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) { rotation.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { rotation.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { rotation.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)) { rotation.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) { rotation.add(0, 1, 0); }
		
		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
		
		paint(thisMouseX, thisMouseY);
		
		prevMouseX = thisMouseX;
		prevMouseY = thisMouseY;
		
		RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
		Matrix4 pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		if(motion.lenSq() > 0.01) {
			motion.normalize();
			motion.mul(5 * (float)et);
			translate(pMat, camera.sceneObject.transformation, motion);
		}
		if(rotation.lenSq() > 0.01) {
			rotation.mul((float)(100.0 * et));
			rotate(pMat, camera.sceneObject.transformation, rotation);
		}
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
	}

	/**
	 * Apply a rotation to the camera.
	 * 
	 * Rotate the camera about one ore more of its local axes, by modifying <b>transformation</b>.  The 
	 * camera is rotated by rotation.x about its horizontal axis, by rotation.y about its vertical axis, 
	 * and by rotation.z around its view direction.  The rotation is about the camera's viewpoint, if 
	 * this.orbitMode is false, or about the world origin, if this.orbitMode is true.
	 * 
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param rotation  The rotation in degrees, as Euler angles (rotation angles about x, y, z axes)
	 */
	protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
		if(!scene.editMode){
			rotation = rotation.clone().mul((float)(Math.PI / 180.0));
			Matrix4 mRot = Matrix4.createRotationX(rotation.x);
			mRot.mulAfter(Matrix4.createRotationY(rotation.y));
			mRot.mulAfter(Matrix4.createRotationZ(rotation.z));
	
			Vector3 rotCenter = new Vector3(0,0,0);
			transformation.clone().invert().mulPos(rotCenter);
			parentWorld.clone().invert().mulPos(rotCenter);
			mRot.mulBefore(Matrix4.createTranslation(rotCenter.clone().negate()));
			mRot.mulAfter(Matrix4.createTranslation(rotCenter));
			transformation.mulBefore(mRot);
		}
	}
	
	private void getRay(int mouseX, int mouseY, Vector3 outOrigin, Vector3 outDirection) {
		Vector3 p1 = new Vector3(mouseX, mouseY, -1);
		Vector3 p2 = new Vector3(mouseX, mouseY, 1);
		Matrix4 mVPI = camera.mViewProjection.clone().invert();
		mVPI.mulPos(p1);
		mVPI.mulPos(p2);
		outOrigin = p1;
		outDirection = p2.clone().sub(p1);
	}
	
	protected void paint(int curMouseX, int curMouseY) {
		System.out.println("paint called");
		Vector3 rayOrigin = new Vector3();
		Vector3 rayDirection = new Vector3();
		getRay(curMouseX, curMouseY, rayOrigin, rayDirection);
		
		//intersect ray with mesh and find intersection point and corresponding uv
		ArrayList<Triangle> tris = new ArrayList<Triangle>();
		for (int i=0; i < mesh.indexCount / 3; i++) {
			Vector3i triVec = new Vector3i(mesh.indices.get(3*i),
					mesh.indices.get(3*i+1), mesh.indices.get(3*i+2));
			Triangle t = new Triangle(mesh, new Vector3i(triVec));
			tris.add(t);
		}
		
		IntersectionRecord outRecord = new IntersectionRecord();
		for (Triangle t : tris) {
			if (t.intersect(outRecord, rayOrigin, rayDirection))
				break;
		}
		
		paintTexture.addPaint(outRecord.location, outRecord.texCoords, mesh);	
	}
	
	/**
	 * Apply a translation to the camera.
	 * 
	 * Translate the camera by an offset measured in camera space, by modifying <b>transformation</b>.
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param motion  The translation in camera-space units
	 */
	protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
		if(!scene.editMode){
			Matrix4 mTrans = Matrix4.createTranslation(motion);
			transformation.mulBefore(mTrans);
		}		
	}
}

