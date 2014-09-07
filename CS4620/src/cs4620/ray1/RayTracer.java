package cs4620.ray1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cs4620.ray1.camera.Camera;
import egl.math.Colord;
import cs4620.ray1.shader.Shader;
import cs4620.ray1.surface.Surface;

public class RayTracer {
	/**
	 * This directory precedes the arguments passed in via the command line.
	 */
	public static String directory = "data/scenes/ray1";
	
	/**
	 * The main method takes all the parameters and assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png. A '-p' option may be passed in to change the
	 * path that is prepended to each file that is included.
	 *
	 * @param args
	 */
	public static final void main(String[] args) {
		String[] newArgs;
		if (args.length > 0 && (args[0].equals("p") || args[0].equals("-p"))) {
			if (args.length == 1) {
				printUsage();
				System.exit(-1);
			}
			directory = args[1];
			if (directory.endsWith("/"))
				directory = directory.substring(0, directory.length()-1);
			newArgs = new String[args.length-2];
			for (int i=0; i<args.length-2; i++) {
				newArgs[i] = args[i+2];
			}
		} else {
			newArgs = args;
		}
		RayTracer rayTracer = new RayTracer();
		rayTracer.run(newArgs);
	}
	
	/**
	 * If filename is a directory, set testFolderPath = fn.
	 * And return a list of all .xml files inside the directory
	 * @param fn Filename or directory
	 * @return fn itself in case fn is a file, or all .xml files inside fn
	 */
	public ArrayList<String> getFileLists(String fn) {
		if(fn.endsWith("*"))
			fn = fn.substring(0, fn.length()-1);
		if(fn.endsWith("/"))
			fn = fn.substring(0, fn.length()-1);

		File file = new File(fn);
		ArrayList<String> output = new ArrayList<String>();
		if(file.exists()) {
			if(file.isFile()) {
				// Extract the folder part of the name
				int dir_index = fn.lastIndexOf('/');
				if (dir_index > 0 && dir_index < fn.length()) {
					SceneFolderPath.set(fn.substring(0, dir_index + 1));
				} else {  
					SceneFolderPath.set("");
				}
				output.add(fn);
			} else {
				SceneFolderPath.set(fn + "/");				
				for(String fl : file.list()) {
					if(fl.endsWith(".xml")) {
						output.add(SceneFolderPath.get() + fl);
					}
				}
				if (output.size() == 0) {
					System.err.println("Warning: no XML files found in the directory " + fn);
				}
			}
		} else {
			System.err.println("Error: File or directory " + fn + " not found.");
			printUsage();
			System.exit(-1);
		}
		return output;
	}

	public static void printUsage() {
		System.out.println("Usage: java RayTracer [-p path] [directory1 directory2 ... | file1 file2 ...]");
		System.out.println("List each scene file you would like to render on the command line separated by spaces.");
		System.out.println("You may also specify a directory, and all scene files in that directory will be rendered.");
		System.out.println("By default, all files specified are prepended with a given path. Use the -p option to");
		System.out.println("override this path. This path is currently: " + directory);
		System.out.println("NB: the path starts from where the base of the project is stored in the file system.");
	}
	
	/**
	 * The run method takes all the parameters and assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png.
	 *
	 * @param args
	 */
	public void run(String[] args) {
		if (args.length == 0) {
			System.out.println("No arguments found... attempting to render all scenes");
			args = new String[] {"."};
		}
		
		Parser parser = new Parser();
		for (int ctr = 0; ctr < args.length; ctr++) {

			String arg;
			if (args[ctr].startsWith(directory)) {
				arg = args[ctr];
			} else {
				arg = directory + "/" + args[ctr];
			}
			ArrayList<String> fileLists = getFileLists(arg);

			for (String inputFilename : fileLists) {
				String outputFilename = inputFilename + ".png";

				// Parse the input file
				Scene scene = (Scene) parser.parse(inputFilename, Scene.class);
				
				// Create the acceleration structure.
				ArrayList<Surface> renderableSurfaces = new ArrayList<Surface>();
				List<Surface> surfaces = scene.getSurfaces();
				for (Iterator<Surface> iter = surfaces.iterator(); iter.hasNext();) {
					iter.next().appendRenderableSurfaces(renderableSurfaces);
				}
				scene.setSurfaces(renderableSurfaces);

				// Render the scene
				renderImage(scene);

				// Write the image out
				scene.getImage().write(outputFilename);
			}
		}
	}
	
	/**
	 * The renderImage method renders the entire scene.
	 *
	 * @param scene The scene to be rendered
	 */
	public void renderImage(Scene scene) {

		// Get the output image
		Image image = scene.getImage();
		Camera cam = scene.getCamera();

		// Set the camera aspect ratio to match output image
		int width = image.getWidth();
		int height = image.getHeight();

		// Timing counters
		long startTime = System.currentTimeMillis();

		// Do some basic setup
		Ray ray = new Ray();
		Colord rayColor = new Colord();

		int total = height * width;
		int counter = 0;
		int lastShownPercent = 0;
		
		double exposure = scene.getExposure();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				rayColor.setZero();

				cam.getRay(ray, (x + 0.5) / width, (y + 0.5) / height);				
				shadeRay(rayColor, scene, ray);
				
				rayColor.mul(exposure);
				image.setPixelColor(rayColor, x, y);

				counter ++;
				if((int)(100.0 * counter / total) != lastShownPercent) {
					lastShownPercent = (int)(100.0*counter / total);
					System.out.println(lastShownPercent + "%");
				}
			}
		}

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Done.  Total rendering time: "
				+ (totalTime / 1000.0) + " seconds");
	}

	/**
	 * This method returns the color along a single ray in outColor.
	 *
	 * @param outColor output space
	 * @param scene the scene
	 * @param ray the ray to shade
	 */
	public static void shadeRay(Colord outColor, Scene scene, Ray ray) {
		// TODO#A2: Compute the color of the intersection point.
		// 1) Find the first intersection of "ray" with the scene.
		//    Record intersection in intersectionRecord. If it doesn't hit anything,
		//    just return the scene's background color.
		// 2) Get the shader from the intersection record.
		// 3) Call the shader's shade() method to set the color for this ray.

	}
}
