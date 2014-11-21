package cs4620.ray1.surface;

import cs4620.mesh.MeshData;
import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import egl.math.Vector3i;
import cs4620.ray1.shader.Shader;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3d norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  MeshData mesh;
  
  /** 3 indices to the vertices of this triangle. */
  Vector3i index;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, Vector3i index, Shader shader) {
    this.owner = owner;
    this.index = new Vector3i(index);
    
    Vector3d v0 = owner.getPosition(index.x);
    Vector3d v1 = owner.getPosition(index.y);
    Vector3d v2 = owner.getPosition(index.z);
    
    if (!owner.hasNormals()) {
    	Vector3d e0 = new Vector3d(), e1 = new Vector3d();
    	e0.set(v1).sub(v0);
    	e1.set(v2).sub(v0);
    	norm = new Vector3d();
    	norm.set(e0).cross(e1);
    }
    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }
  
  public Triangle(MeshData mesh, Vector3i index) {
	    this.mesh = mesh;
	    this.index = new Vector3i(index);
	    
	    Vector3 v0 = getPosition(index.x);
	    Vector3 v1 = getPosition(index.y);
	    Vector3 v2 = getPosition(index.z);
	    
	    a = v0.x-v1.x;
	    b = v0.y-v1.y;
	    c = v0.z-v1.z;
	    
	    d = v0.x-v2.x;
	    e = v0.y-v2.y;
	    f = v0.z-v2.z;
  }

  /**
   * Get the position of a vertex.
   * @param index The vertex index.
   * @return The position of the specified vertex.
   */
  public Vector3 getPosition(int index) {
	  return new Vector3(mesh.positions.get(3*index), 
			  mesh.positions.get(3*index+1), 
			  mesh.positions.get(3*index+2));
  }
  
  /**
   * Get the UV-coordinates of a vertex, assuming the coordinates exist.
   * @param index The vertex index.
   * @return The UV-coordinates of the specified vertex.
   */
  public Vector2d getUV(int index) {
	  return new Vector2d(mesh.uvs.get(2*index), 
			  mesh.uvs.get(2*index+1));
  }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
	    // TODO#A2 fill in this function.
	  
	    Vector3 v0 = getPosition(index.x);
	    
	    double g = rayIn.direction.x;
	    double h = rayIn.direction.y;
	    double i = rayIn.direction.z;
	    double j = v0.x - rayIn.origin.x;
	    double k = v0.y - rayIn.origin.y;
	    double l = v0.z - rayIn.origin.z;
	    double M = a*(e*i-h*f) + b*(g*f-d*i) + c*(d*h-e*g);
	    
	    double ei_hf = e*i-h*f;
	    double gf_di = g*f-d*i;
	    double dh_eg = d*h-e*g;
	    double ak_jb = a*k-j*b;
	    double jc_al = j*c-a*l;
	    double bl_kc = b*l-k*c;
	    
	    double t = -(f*(ak_jb) + e*(jc_al) + d*(bl_kc))/M;
	    if(t > rayIn.end || t < rayIn.start) return false;
	    
	    double beta = (j*(ei_hf) + k*(gf_di) + l*(dh_eg))/M;
	    if(beta < 0 || beta > 1) return false;
	    
	    double gamma = (i*(ak_jb) + h*(jc_al) + g*(bl_kc))/M;
	    if(gamma < 0 || gamma + beta > 1) return false;
	    
	    
	    // There was an intersection, fill out the intersection record
	    if (outRecord != null) {
	      outRecord.t = t;
	      rayIn.evaluate(outRecord.location, t);
	      outRecord.surface = this;
	      
	      if (norm != null) {
	        outRecord.normal.set(new Vector3((float)norm.x, (float)norm.y, (float)norm.z));
	      } else {        
	    	  outRecord.normal.setZero();
	      }
	      outRecord.normal.normalize();
	      if (mesh.hasUVs()) {
	        outRecord.texCoords.setZero()
	        				   .addMultiple(1-beta-gamma, getUV(index.x))
	        				   .addMultiple(beta, getUV(index.y))
	        				   .addMultiple(gamma, getUV(index.z));
	      }
	    }
	  
	    return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}