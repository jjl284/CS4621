package cs4620.splines;
import java.util.ArrayList;
import java.util.Collections;

import cs4620.mesh.MeshData;
import egl.NativeMem;
import egl.math.*;

public class BSpline {
	
	private float epsilon;
	
	//BSpline Control Points
	private ArrayList<Vector2> controlPoints;
	
	//Bezier Curves that make up this BSpline
	private ArrayList<CubicBezier> approximationCurves;
	
	//Whether or not this curve is a closed curve
	private boolean isClosed;
	
	public static final float DIST_THRESH = 0.15f;
	public static final int MIN_OPEN_CTRL_POINTS= 4,
			                           MIN_CLOSED_CTRL_POINTS= 3,
			                           MAX_CTRL_POINTS= 20;

	public BSpline(ArrayList<Vector2> controlPoints, boolean isClosed, float epsilon) throws IllegalArgumentException {
		if(isClosed) {
			if(controlPoints.size() < MIN_CLOSED_CTRL_POINTS)
				throw new IllegalArgumentException("Closed Splines must have at least 3 control points.");
		} else {
			if(controlPoints.size() < MIN_OPEN_CTRL_POINTS)
				throw new IllegalArgumentException("Open Splines must have at least 4 control points.");
		}

		this.controlPoints = controlPoints;
		this.isClosed = isClosed;
		this.epsilon = epsilon;
		setBeziers();
	}
	
	public boolean isClosed() {
		return this.isClosed;
	}
	
	public boolean setClosed(boolean closed) {
		if(this.isClosed && this.controlPoints.size() == 3) {
			System.err.println("You must have at least 4 control points to make an open spline.");
			return false;
		}
		this.isClosed= closed;
		setBeziers();
		return true;
	}
	
	public ArrayList<Vector2> getControlPoints() {
		return this.controlPoints;
	}
	
	public void setControlPoint(int index, Vector2 point) {
		this.controlPoints.set(index, point);
		setBeziers();
	}
	
	public boolean addControlPoint(Vector2 point) {
		if(this.controlPoints.size() == MAX_CTRL_POINTS) {
			System.err.println("You can only have "+BSpline.MAX_CTRL_POINTS+" control points per spline.");
			return false;
		}
		/* point= (x0, y0), prev= (x1, y1), curr= (x2,y2)
		 * 
		 * v= [ (y2-y1), -(x2-x1) ]
		 * 
		 * r= [ (x1-x0), (y1-y0) ]
		 * 
		 * distance between point and line prev -> curr is v . r
		 */
		Vector2 curr, prev;
		Vector2 r= new Vector2(), v= new Vector2();
		float distance= Float.POSITIVE_INFINITY;
		int index= -1;
		for(int i= 0; i < controlPoints.size(); i++) {
			curr= controlPoints.get(i);
			if(i == 0) {
				if(isClosed) {
					// add line between first and last ctrl points
					prev= controlPoints.get(controlPoints.size()-1);
				} else {
					continue;
				}
			} else {
				prev= controlPoints.get(i-1);
			}
			v.set(curr.y-prev.y, -(curr.x-prev.x)); v.normalize();
			r.set(prev.x-point.x, prev.y-point.y);
			float newDist = Math.abs(v.dot(r));
			Vector2 v2 = curr.clone().sub(prev);
			v2.mul(1.0f / v2.lenSq());
			float newParam = -v2.dot(r);
			if(newDist < DIST_THRESH && newDist <= distance && 0 < newParam && newParam < 1) {
				distance= newDist;
				index= i;
			}
		}
		
		if (index >= 0) {
			controlPoints.add(index, point);
			setBeziers();
			return true;
		}
		System.err.println("Invalid location, try selecting a point closer to the spline.");
		return false;
	}
	
	public boolean removeControlPoint(int index) {
		if(this.isClosed) {
			if(this.controlPoints.size() == MIN_CLOSED_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_CLOSED_CTRL_POINTS+" for a closed Spline.");
				return false;
			}
		} else {
			if(this.controlPoints.size() == MIN_OPEN_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_OPEN_CTRL_POINTS+" for an open Spline.");
				return false;
			}
		}
		this.controlPoints.remove(index);
		setBeziers();
		return true;
	}
	
	public void modifyEpsilon(float newEps) {
		epsilon = newEps;
		setBeziers();
	}
	
	public float getEpsilon() {
		return epsilon;
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getPoints() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getPoints())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getNormals() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getNormals())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getTangents() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getTangents())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Using this.controlPoints, create the CubicBezier objects that approxmiate this curve and
	 * save them to this.approximationCurves. Assure that the order of the Bezier curves that you
	 * add to approximationCurves is the order in which they approximate the overall BSpline
	 */
	private void setBeziers() {
		//TODO A5
		//placeholder code so this compiles
		approximationCurves = new ArrayList<CubicBezier>();
		CubicBezier tmp = new CubicBezier(new Vector2(0,0),new Vector2(0,0),new Vector2(0,0),new Vector2(0,0),0f, this);
		approximationCurves.add(tmp);
	}
	
	/**
	 * Reverses the tangents and normals associated with this BSpline
	 */
	public void reverseNormalsAndTangents() {
		for(CubicBezier b : approximationCurves) {
			for(Vector2 p : b.getNormalReferences())
				p.mul(-1);
			for(Vector2 p : b.getTangentReferences())
				p.mul(-1);
		}
	}
	
	
	/**
	 * Given a closed curve and a sweep curve, fill the three GLBuffer objects appropriately. Here, we sweep the
	 * closed curve along the sweep curve
	 * @param crossSection, the BSpline cross section
	 * @param sweepCurve, the BSpline we are sweeping along
	 * @param data, a MeshData where we will output our triangle mesh
	 * @param scale > 0, parameter that controls how big the closed curve with respect to the sweep curve
	 */
	public static void build3DSweep(BSpline crossSection, BSpline sweepCurve, MeshData data, float scale) {
		//TODO A5
		//Our goal is to fill these arrays. Then we can put them in the buffers properly.
		
		
		/* Initialize the buffers for data.positions, data.normals, data.indices, and data.uvs as
		 * you did for A1.  Although you will not be using uv's, you DO need to initialize the
		 * buffer with space.  Don't forget to initialize data.indexCount and data.vertexCount.
		 * 
		 * Then set the data of positions / normals / indices with what you have calculated.
		 */
	}
}