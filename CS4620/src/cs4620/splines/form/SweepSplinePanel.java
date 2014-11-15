package cs4620.splines.form;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import cs4620.splines.BSpline;
import egl.GLBuffer;
import egl.GLError;
import egl.RasterizerState;
import egl.math.Vector3;

public class SweepSplinePanel extends SplinePanel {
	
	public BSpline toSweep;
	public BSpline toSweepAlong;
	
	public SplineScreen owner;
	
	public boolean clickStartedHere= false;
	
	public float scale= 1.0f;
	
	GLBuffer vertexPositions, vertexNormals, triangleIndices;
	
	ArrayList<Vector3> testPositions;

	public SweepSplinePanel(int index, BSpline toSweep, BSpline toSweepAlong, SplineScreen owner) {
		this.toSweepAlong= toSweep;
		this.toSweepAlong= toSweepAlong;
		this.owner= owner;
		this.index= index;
	}
	
	@Override
	public void draw() {
		GL11.glViewport(this.index * SplinePanel.panelWidth, 0, SplinePanel.panelWidth, SplinePanel.panelHeight);
		owner.rController.update(owner.renderer, owner.camController);
		
		if(owner.camController.camera != null){
			owner.renderer.draw(owner.camController.camera, owner.rController.env.lights, RasterizerState.CULL_NONE);
			
			if (owner.showGrid)
				owner.gridRenderer.draw(owner.camController.camera);
		}
        GLError.get("draw");
	}
}
