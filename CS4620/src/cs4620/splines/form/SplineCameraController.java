package cs4620.splines.form;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import cs4620.common.Scene;
import cs4620.common.event.SceneTransformationEvent;
import cs4620.gl.CameraController;
import cs4620.gl.RenderCamera;
import cs4620.gl.RenderEnvironment;
import cs4620.gl.RenderObject;
import egl.math.Matrix4;
import egl.math.Vector3;

public class SplineCameraController extends CameraController{
	private SweepSplinePanel sweep;
	
	public SplineCameraController(Scene s, RenderEnvironment re, RenderCamera c, SweepSplinePanel sweep) {
		super(s, re, c);
		this.sweep= sweep;
		orbitMode = true;
	}

	@Override
	public void update(double et) {
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();

		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { motion.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { motion.add(0, 1, 0); }

		if(Keyboard.isKeyDown(Keyboard.KEY_E)) { rotation.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) { rotation.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotation.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotation.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotation.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotation.add(0, 1, 0); }

		if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 

		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown && this.sweep.clickStartedHere) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
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
}
