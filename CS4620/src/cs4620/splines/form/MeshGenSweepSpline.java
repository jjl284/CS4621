package cs4620.splines.form;

import cs4620.common.BasicType;
import cs4620.mesh.MeshData;
import cs4620.mesh.gen.MeshGenOptions;
import cs4620.mesh.gen.MeshGenerator;
import cs4620.splines.BSpline;

public class MeshGenSweepSpline extends MeshGenerator {

	BSpline toSweep;
	BSpline toSweepAlong;
	float scale;
	
	@Override
	public void generate(MeshData outData, MeshGenOptions opt) {
		BSpline.build3DSweep(toSweep, toSweepAlong, outData, scale);
	}

	@Override
	public BasicType getType() {
		return null;
	}

	public void setSplineToSweep(BSpline toSweep) {
		this.toSweep= toSweep;
	}
	
	public void setSplineToSweepAlong(BSpline toSweepAlong) {
		this.toSweepAlong= toSweepAlong;
	}
	
	public void setScale(float scale) {
		this.scale= scale;
	}
}
